package ru.yandex.practicum.events.service.event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.client.StatisticsService;
import ru.yandex.practicum.contracts.dto.category.CategoryDto;
import ru.yandex.practicum.contracts.dto.event.*;
import ru.yandex.practicum.contracts.dto.user.UserShortDto;
import ru.yandex.practicum.contracts.enums.EventState;
import ru.yandex.practicum.contracts.enums.SortValue;
import ru.yandex.practicum.contracts.exception.*;
import ru.yandex.practicum.contracts.feign.category.CategoryClient;
import ru.yandex.practicum.contracts.feign.users.UsersClient;
import ru.yandex.practicum.events.entity.Event;
import ru.yandex.practicum.events.entity.Location;
import ru.yandex.practicum.events.mapper.EventMapper;
import ru.yandex.practicum.events.repository.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.parse;
import static ru.yandex.practicum.contracts.util.SearchValidators.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UsersClient usersClient;
    private final CategoryClient categoryClient;
    private final StatisticsService statisticsService;
    private final EntityManager entityManager;
    private final EventMapper eventMapper;
    private final DateTimeFormatter spaceFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Map<String, Set<Long>> viewCache = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {

        if (!usersClient.userExists(userId)) {
            throw new UserNotExistException("User with id=" + userId + " was not found");
        }

        if (!categoryClient.categoryExists(newEventDto.getCategory())) {
            throw new CategoryNotExistException("Category with id=" + newEventDto.getCategory() + " was not found");
        }

        log.info("createEvent newEventDto-> " + newEventDto);

        LocalDateTime eventDate = parse(newEventDto.getEventDate(), spaceFormat);
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongTimeException("Event date must be at least 2 hours from now" + eventDate);
        }

        Event event = eventMapper.toEvent(newEventDto, userId, newEventDto.getLocation());
        Event savedEvent = eventRepository.save(event);

        UserShortDto userShortDto = usersClient.getUserShort(userId);
        CategoryDto categoryDto = categoryClient.getCategory(savedEvent.getCategoryId());

        return eventMapper.toEventFullDto(savedEvent, userShortDto, categoryDto);
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        if (!usersClient.userExists(userId)) {
            throw new UserNotExistException("User with id=" + userId + " was not found");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").descending());

        Page<Event> eventsPage = eventRepository.findAllByInitiatorId(userId, pageable);
        if (eventsPage.hasContent()) {
            List<Long> eventIds = eventsPage.getContent().stream()
                    .map(Event::getId)
                    .collect(Collectors.toList());

            Map<Long, Long> viewsMap = statisticsService.getEventsViews(eventIds, null, false);
            eventsPage.getContent().forEach(event ->
                    event.setViews(viewsMap.getOrDefault(event.getId(), 0L))
            );
        }
        return eventsPage.getContent().stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserDto updateEventUserDto) {
        log.info("Вызван updateEventByUser. На вход пришёл updateEventUserDto = {}", updateEventUserDto);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotExistException("Event with id=" + eventId + " was not found"));

        if (event.getPublishedOn() != null) {
            throw new AlreadyPublishedException("Cannot update published event");
        }

        if (updateEventUserDto == null) {
            UserShortDto userShortDto = usersClient.getUserShort(userId);
            CategoryDto categoryDto = categoryClient.getCategory(event.getCategoryId());
            return eventMapper.toEventFullDto(event, userShortDto, categoryDto);
        }

        updateEventFieldsFromUserDto(event, updateEventUserDto);

        if (updateEventUserDto.getStateAction() != null) {
            switch (updateEventUserDto.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }

        Event updatedEvent = eventRepository.save(event);
        UserShortDto userShortDto = usersClient.getUserShort(userId);
        CategoryDto categoryDto = categoryClient.getCategory(updatedEvent.getCategoryId());
        return eventMapper.toEventFullDto(updatedEvent, userShortDto, categoryDto);
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotExistException("Event with id=" + eventId + " was not found"));

        Map<Long, Long> viewsMap = statisticsService.getEventsViews(List.of(eventId), null, false);
        event.setViews(viewsMap.getOrDefault(eventId, 0L));

        UserShortDto userShortDto = usersClient.getUserShort(userId);
        CategoryDto categoryDto = categoryClient.getCategory(event.getCategoryId());
        return eventMapper.toEventFullDto(event, userShortDto, categoryDto);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminDto updateEventAdminDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotExistException("Event with id=" + eventId + " was not found"));

        if (updateEventAdminDto == null) {
            UserShortDto userShortDto = usersClient.getUserShort(event.getInitiatorId());
            CategoryDto categoryDto = categoryClient.getCategory(event.getCategoryId());
            return eventMapper.toEventFullDto(event, userShortDto, categoryDto);
        }

        updateEventFieldsFromAdminDTO(event, updateEventAdminDto);

        if (updateEventAdminDto.getStateAction() != null) {
            switch (updateEventAdminDto.getStateAction()) {
                case PUBLISH_EVENT -> {
                    if (event.getPublishedOn() != null) {
                        throw new AlreadyPublishedException("Event already published");
                    }
                    if (event.getState().equals(EventState.CANCELED)) {
                        throw new EventAlreadyCanceledException("Event already canceled");
                    }
                    if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                        throw new WrongTimeException("Event date must be at least 1 hour from publication date");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> {
                    if (event.getPublishedOn() != null) {
                        throw new AlreadyPublishedException("Event already published");
                    }
                    event.setState(EventState.CANCELED);
                }
            }
        }

        Event updatedEvent = eventRepository.save(event);
        UserShortDto userShortDto = usersClient.getUserShort(event.getInitiatorId());
        CategoryDto categoryDto = categoryClient.getCategory(updatedEvent.getCategoryId());
        return eventMapper.toEventFullDto(updatedEvent, userShortDto, categoryDto);
    }

    @Override
    @Transactional
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndPublishedOnIsNotNull(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        String clientIp = getClientIp(request);
        boolean isUnique = isUniqueView(eventId, clientIp);

        Map<Long, Long> viewsMap = statisticsService.getEventsViews(List.of(eventId), request, true);
        Long statsViews = viewsMap.getOrDefault(eventId, 0L);

        Long newViews;
        if (isUnique) {
            newViews = event.getViews() + 1;
        } else {
            newViews = Math.max(statsViews, event.getViews());
        }

        if (!newViews.equals(event.getViews())) {
            event.setViews(newViews);
            event = eventRepository.save(event);
        }

        UserShortDto userShortDto = usersClient.getUserShort(event.getInitiatorId());
        CategoryDto categoryDto = categoryClient.getCategory(event.getCategoryId());

        return eventMapper.toEventFullDto(event, userShortDto, categoryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsWithParamsByAdmin(AdminEventSearchRequest request) {
        LocalDateTime start = request.getRangeStart() != null ? parse(request.getRangeStart(), spaceFormat) : null;
        LocalDateTime end = request.getRangeEnd() != null ? parse(request.getRangeEnd(), spaceFormat) : null;

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = buildAdminPredicates(builder, root, request, start, end);

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(builder.desc(root.get("createdOn")));

        List<Event> events = entityManager.createQuery(query)
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> viewsMap = statisticsService.getEventsViews(eventIds, null, false);
        events.forEach(event -> event.setViews(viewsMap.getOrDefault(event.getId(), 0L)));
        return getEventsFullDtoList(events);
    }

    private List<EventFullDto> getEventsFullDtoList(List<Event> events) {
        List<Long> usersIds = events.stream().map(Event::getInitiatorId).toList();
        List<Long> categorysIds = events.stream().map(Event::getCategoryId).toList();
        List<CategoryDto> categoryDtos = categoryClient.getCategoriesByIds(categorysIds);
        List<UserShortDto> userShortDtos = usersClient.getUsersShort(usersIds);
        Map<Long, UserShortDto> usersById = userShortDtos.stream()
                .collect(Collectors.toMap(UserShortDto::getId, Function.identity()));
        Map<Long, CategoryDto> categoriesById = categoryDtos.stream()
                .collect(Collectors.toMap(CategoryDto::getId, Function.identity()));

        return events.stream()
                .map(event -> eventMapper.toEventFullDto(
                        event,
                        usersById.get(event.getInitiatorId()),
                        categoriesById.get(event.getCategoryId())
                )).toList();
    }

    @Override
    public List<EventFullDto> getEventsWithParamsByUser(PublicEventSearchRequest request,
                                                        HttpServletRequest httpRequest) {
        if (request.getRangeStart() != null && request.getRangeEnd() != null) {
            LocalDateTime start = parse(request.getRangeStart(), spaceFormat);
            LocalDateTime end = parse(request.getRangeEnd(), spaceFormat);
            if (start.isAfter(end)) {
                throw new ValidationException("Range start cannot be after range end");
            }
        }

        LocalDateTime start = request.getRangeStart() != null ? parse(request.getRangeStart(), spaceFormat) : null;
        LocalDateTime end = request.getRangeEnd() != null ? parse(request.getRangeEnd(), spaceFormat) : null;

        List<Event> events = findPublicEvents(request, start, end);
        if (isOnlyAvailable(request.getOnlyAvailable())) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() == 0 ||
                            event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }
        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> viewsMap = statisticsService.getEventsViews(eventIds, httpRequest, true);

        events.forEach(event -> event.setViews(viewsMap.getOrDefault(event.getId(), 0L)));

        if (shouldSort(request.getSort())) {
            Comparator<Event> comparator = request.getSort() == SortValue.VIEWS ?
                    Comparator.comparing(Event::getViews, Comparator.nullsLast(Long::compareTo)).reversed() :
                    Comparator.comparing(Event::getEventDate, Comparator.nullsLast(LocalDateTime::compareTo));
            events = events.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }

        return getEventsFullDtoList(events);
    }

    @Override
    public EventFullDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotExistException("Event with id=" + eventId + " was not found"));

        UserShortDto userShortDto = usersClient.getUserShort(event.getInitiatorId());
        CategoryDto categoryDto = categoryClient.getCategory(event.getCategoryId());
        return eventMapper.toEventFullDto(event, userShortDto, categoryDto);
    }

    @Override
    public List<EventFullDto> getTopEvent(int count) {
        List<Event> topEvents = eventRepository.getTopByComments(count);
        return getEventsFullDtoList(topEvents);
    }

    @Override
    public boolean eventExists(Long eventId) {
        return eventRepository.existsById(eventId);
    }

    @Override
    public boolean existsByCategoryId(Long categoryId) {
        return eventRepository.existsByCategoryId(categoryId);
    }

    @Override
    public List<EventFullDto> getEventsByIds(Set<Long> ids) {
        List<Event> events = eventRepository.findAllById(ids);
        return getEventsFullDtoList(events);
    }

    private void updateEventFieldsFromUserDto(Event event, UpdateEventUserDto dto) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getCategory() != null) {
            Long categoryId = dto.getCategory();
            if (categoryClient.categoryExists(categoryId)) {
                throw new CategoryNotExistException("Category with id=" + dto.getCategory() + " was not found");
            }
            event.setCategoryId(categoryId);
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getEventDate() != null) {
            LocalDateTime newEventDate = parse(dto.getEventDate(), spaceFormat);
            if (newEventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new WrongTimeException("Event date must be at least 2 hours from now" + newEventDate);
            }
            event.setEventDate(newEventDate);
        }

        if (dto.getLocation() != null) {
            Location location = new Location();
            location.setLat(dto.getLocation().getLat());
            location.setLon(dto.getLocation().getLon());
            event.setLocation(location);
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
    }

    private void updateEventFieldsFromAdminDTO(Event event, UpdateEventAdminDto dto) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getConfirmedRequests() != null) {
            event.setConfirmedRequests(dto.getConfirmedRequests());
        }

        if (dto.getCategory() != null) {
            Long categoryId = dto.getCategory();
            if (!categoryClient.categoryExists(categoryId)) {
                throw new CategoryNotExistException("Category with id=" + dto.getCategory() + " was not found");
            }
            event.setCategoryId(categoryId);
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getEventDate() != null) {
            LocalDateTime newEventDate = parse(dto.getEventDate(), spaceFormat);
            if (event.getPublishedOn() != null && newEventDate.isBefore(event.getPublishedOn().plusHours(1))) {
                throw new WrongTimeException("Event date must be at least 1 hour after publication" + newEventDate);
            }
            if (newEventDate.isBefore(LocalDateTime.now())) {
                throw new WrongTimeException("Event date cannot be in the past");
            }
            event.setEventDate(newEventDate);
        }

        if (dto.getLocation() != null) {
            Location location = new Location();
            location.setLat(dto.getLocation().getLat());
            location.setLon(dto.getLocation().getLon());
            event.setLocation(location);
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
    }

    private List<Predicate> buildAdminPredicates(CriteriaBuilder builder, Root<Event> root,
                                                 AdminEventSearchRequest request,
                                                 LocalDateTime start, LocalDateTime end) {
        List<Predicate> predicates = new ArrayList<>();

        if (hasCategories(request.getCategories())) {
            Predicate categoryFilter = root.get("categoryId").in(request.getCategories());
            predicates.add(categoryFilter);
        }

        if (hasUsers(request.getUsers())) {
            Predicate userFilter = root.get("initiatorId").in(request.getUsers());
            predicates.add(userFilter);
        }

        if (hasStates(request.getStates())) {
            Predicate stateFilter = root.get("state").in(request.getStates());
            predicates.add(stateFilter);
        }

        if (start != null) {
            Predicate startDateFilter = builder.greaterThanOrEqualTo(root.get("eventDate"), start);
            predicates.add(startDateFilter);
        }

        if (end != null) {
            Predicate endDateFilter = builder.lessThanOrEqualTo(root.get("eventDate"), end);
            predicates.add(endDateFilter);
        }

        return predicates;
    }

    private List<Predicate> buildPublicPredicates(CriteriaBuilder builder, Root<Event> root,
                                                  PublicEventSearchRequest request,
                                                  LocalDateTime start, LocalDateTime end) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(root.get("state"), EventState.PUBLISHED));

        if (hasText(request.getText())) {
            String searchText = "%" + request.getText().toLowerCase() + "%";
            Predicate annotationMatch = builder.like(builder.lower(root.get("annotation")), searchText);
            Predicate descriptionMatch = builder.like(builder.lower(root.get("description")), searchText);

            Predicate textSearch = builder.or(annotationMatch, descriptionMatch);
            predicates.add(textSearch);
        }

        if (hasCategories(request.getCategories())) {
            Predicate categoryFilter = root.get("categoryId").in(request.getCategories());
            predicates.add(categoryFilter);
        }

        if (request.getPaid() != null) {
            Predicate paidFilter = request.getPaid() ?
                    builder.isTrue(root.get("paid")) :
                    builder.isFalse(root.get("paid"));
            predicates.add(paidFilter);
        }

        if (start != null) {
            Predicate startDateFilter = builder.greaterThanOrEqualTo(root.get("eventDate"), start);
            predicates.add(startDateFilter);
        }

        if (end != null) {
            Predicate endDateFilter = builder.lessThanOrEqualTo(root.get("eventDate"), end);
            predicates.add(endDateFilter);
        }

        return predicates;
    }

    private List<Event> findPublicEvents(PublicEventSearchRequest request, LocalDateTime start, LocalDateTime end) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = buildPublicPredicates(builder, root, request, start, end);

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(builder.asc(root.get("eventDate")));

        return entityManager.createQuery(query)
                .setFirstResult(request.getFrom())
                .setMaxResults(request.getSize())
                .getResultList();
    }

    private boolean isUniqueView(Long eventId, String clientIp) {
        return viewCache.computeIfAbsent(clientIp, k -> new HashSet<>()).add(eventId);
    }

    private String getClientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
