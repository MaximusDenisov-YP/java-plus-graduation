create table if not exists user_event_interactions (
   user_id bigint not null,
   event_id bigint not null,
   weight double precision not null,
   updated_at timestamp not null,
   primary key (user_id, event_id)
);

create table if not exists event_similarity (
    event_a bigint not null,
    event_b bigint not null,
    score double precision not null,
    updated_at timestamp not null,
    primary key (event_a, event_b)
);

create index if not exists idx_user_event_interactions_user_updated
    on user_event_interactions (user_id, updated_at desc);

create index if not exists idx_user_event_interactions_event
    on user_event_interactions (event_id);

create index if not exists idx_event_similarity_event_a_score
    on event_similarity (event_a, score desc);

create index if not exists idx_event_similarity_event_b_score
    on event_similarity (event_b, score desc);