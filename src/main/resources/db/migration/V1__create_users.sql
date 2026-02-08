create table users (
                       id uuid primary key,
                       email varchar(255) not null unique,
                       password_hash varchar(255) not null,
                       role varchar(50) not null,
                       created_at timestamptz not null default now(),
                       updated_at timestamptz not null default now()
);