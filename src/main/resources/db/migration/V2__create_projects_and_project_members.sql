create table projects
(
    id          uuid primary key,
    name        varchar(255) not null,
    description text,
    owner_id    uuid         not null,
    created_at  timestamptz  not null,
    updated_at  timestamptz  not null,
    constraint fk_projects_owner
        foreign key (owner_id) references users (id)
);

create table project_members
(
    id         uuid primary key,
    project_id uuid        not null,
    user_id    uuid        not null,
    role       varchar(50) not null,
    created_at timestamptz not null,
    constraint fk_project_members_project
        foreign key (project_id) references projects (id) on delete cascade,
    constraint fk_project_members_user
        foreign key (user_id) references users (id) on delete cascade,
    constraint uk_project_member_unique
        unique (project_id, user_id)
);