create table activity_logs
(
    id          uuid primary key,
    project_id  uuid         not null,
    actor_id    uuid         not null,
    type        varchar(100) not null,
    entity_id   uuid         not null,
    entity_type varchar(100) not null,
    message     text         not null,
    created_at  timestamptz  not null,
    constraint fk_activity_logs_project
        foreign key (project_id) references projects (id) on delete cascade,
    constraint fk_activity_logs_actor
        foreign key (actor_id) references users (id)
);

create index idx_activity_logs_project_id on activity_logs (project_id);
create index idx_activity_logs_actor_id on activity_logs (actor_id);
create index idx_activity_logs_created_at on activity_logs (created_at desc);