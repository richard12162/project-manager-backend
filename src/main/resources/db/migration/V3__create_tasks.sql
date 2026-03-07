create table tasks
(
    id          uuid primary key,
    project_id  uuid         not null,
    title       varchar(255) not null,
    description text,
    status      varchar(50)  not null,
    priority    varchar(50)  not null,
    assignee_id uuid,
    due_date    timestamptz,
    created_at  timestamptz  not null,
    updated_at  timestamptz  not null,
    constraint fk_tasks_project
        foreign key (project_id) references projects (id) on delete cascade,
    constraint fk_tasks_assignee
        foreign key (assignee_id) references users (id)
);

create index idx_tasks_project_id on tasks (project_id);
create index idx_tasks_assignee_id on tasks (assignee_id);
create index idx_tasks_status on tasks (status);
create index idx_tasks_priority on tasks (priority);