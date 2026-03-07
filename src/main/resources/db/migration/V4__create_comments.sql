create table comments
(
    id         uuid primary key,
    task_id    uuid        not null,
    author_id  uuid        not null,
    content    text        not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint fk_comments_task
        foreign key (task_id) references tasks (id) on delete cascade,
    constraint fk_comments_author
        foreign key (author_id) references users (id)
);

create index idx_comments_task_id on comments (task_id);
create index idx_comments_author_id on comments (author_id);
create index idx_comments_created_at on comments (created_at);