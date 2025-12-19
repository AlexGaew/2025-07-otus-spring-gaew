insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3'),
       ('Genre_4'), ('Genre_5'), ('Genre_6');

insert into books(title, author_id)
values ('BookTitle_1', 1), ('BookTitle_2', 2), ('BookTitle_3', 3);

insert into books_genres(book_id, genre_id)
values (1, 1),   (1, 2),
       (2, 3),   (2, 4),
       (3, 5),   (3, 6);

insert into comments(comment, book_id)
values ('qwer', 1), ('asd', 2), ('zxc', 3),
        ('ccc', 1), ('bbb', 2), ('rrr', 1);

insert into users(username, password)
values
('user', '$2a$10$u0nvEbyBxDAbBxaXc1h89.ICfe.5h512KFtzJDIotFfOuEOR34nIW'),
('admin', '$2a$10$RoyJI6Ny4bEYPceXXptMCe40RNiy3mAo.8KRtQf7lSi81xCrBp5eS'),
('manager', '$2a$10$RoyJI6Ny4bEYPceXXptMCe40RNiy3mAo.8KRtQf7lSi81xCrBp5eS');

insert into user_authorities(user_id, authority)
values
(1, 'ROLE_USER'),
(2, 'ROLE_ADMIN'),
(3, 'ROLE_MANAGER'),
(3, 'ROLE_USER');
