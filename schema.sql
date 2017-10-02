drop table if exists user;
create table user (
  user_id integer primary key autoincrement,
  user_name varchar not null,
  user_email varchar not null,
  user_role boolean not null,
  user_votes varchar
);
drop table if exists election;

create table election (
  election_id integer primary key autoincrement,
  election_name varchar not null,
  category varchar not null,
  start_time varchar not null,
  end_time varchar not null,
  in_favour integer not null,
  not_in_favour integer not null,
  status boolean not null,
  total_votes varchar,
);