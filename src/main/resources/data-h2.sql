insert into post (version, author, created_date_time, last_modified_date_time, publication_date, scope, status, text)
    values (0, 'Adam Kowalski', '2025-03-26T12:48:19','2025-03-26T12:48:19', null, 'PUBLIC', 'ACTIVE', 'POST z data.sql');

insert into post (version, author, created_date_time, last_modified_date_time, publication_date, scope, status, text)
    values (0, 'Bartosz Kowalski', '2025-03-25T12:48:19','2025-03-25T12:48:19', null, 'PUBLIC', 'ACTIVE', 'POST2 z data.sql');

insert into post (version, author, created_date_time, last_modified_date_time, publication_date, scope, status, text)
    values (0, 'Mariusz Pająk', '2025-03-30T12:48:19','2025-03-30T12:48:19', null, 'PUBLIC', 'ACTIVE', 'POST2 z data.sql');

insert into post (version, author, created_date_time, last_modified_date_time, publication_date, scope, status, text)
    values (0, 'Zinedine Zidane', '2025-03-03T12:48:19','2025-03-03T12:48:19', null, 'PUBLIC', 'DELETED', 'POST3 z data.sql');

insert into post (version, author, created_date_time, last_modified_date_time, publication_date, scope, status, text)
    values (0, 'Cristiano Ronaldo', '2025-03-07T12:48:19','2025-03-07T12:48:19', null, 'PUBLIC', 'DELETED', 'POST4 z data.sql');

insert into post (version, author, created_date_time, last_modified_date_time, publication_date, scope, status, text)
    values (0, 'Cristiano Ronaldo', '2025-03-09T12:48:19','2025-03-09T12:48:19', null, 'PUBLIC', 'ACTIVE', 'POST4 z data.sql');

insert into post (version, author, created_date_time, last_modified_date_time, publication_date, scope, status, text)
    values (0, 'Lionel Messi', '2025-02-03T12:48:19','2025-02-03T12:48:19', null, 'PUBLIC', 'DELETED', 'POST5 z data.sql');

insert into post (version, author, created_date_time, last_modified_date_time, publication_date, scope, status, text)
    values (0, 'Maciej Nowak', '2025-03-26T12:48:19','2025-02-10T12:48:19', null, 'PUBLIC', 'ACTIVE', 'POST z data.sql');

insert into post (version, author, created_date_time, last_modified_date_time, publication_date, scope, status, text)
    values (0, 'Jan Kowalski', '2025-03-25T12:48:19','2025-03-29T12:48:19', null, 'PUBLIC', 'ACTIVE', 'POST2 z data.sql');



insert into invoice (buyer, created_date, last_modified_date_time, payment_date, seller, status, version)
    values ('Adam Kowalski', '2025-03-24T12:48:19', '2025-03-24T12:48:19', '2025-03-24', 'Seller1', 'ACTIVE', 0 );

insert into invoice(buyer, created_date, last_modified_date_time, payment_date, seller, status, version)
    values ('Bartosz Kowalski', '2025-02-25T12:48:19', '2025-02-25T12:48:19', '2025-02-25', 'Seller2', 'DRAFT', 0);

insert into invoice(buyer, created_date, last_modified_date_time, payment_date, seller, status, version)
    values ('Marcin Strzelecki', '2025-02-24T12:48:19', '2025-02-24T12:48:19', '2025-02-24', 'seller3', 'ACTIVE', 0);

insert into invoice(buyer, created_date, last_modified_date_time, payment_date, seller, status, version)
    values ('Kacper Kowalski', '2025-02-27T12:48:19', '2025-02-27T12:48:19', '2025-02-27', 'Seller4', 'DELETED', 0);

insert into invoice(buyer, created_date, last_modified_date_time, payment_date, seller, status, version)
    values ('Tomasz Kowalski', '2025-03-24T12:48:19', '2025-03-24T12:48:19', '2025-03-24', 'eller5', 'DRAFT', 0);

insert into invoice(buyer, created_date, last_modified_date_time, payment_date, seller, status, version)
    values ('Victor Orban', '2025-03-24T12:48:19', '2025-03-30T12:48:19', '2025-03-24', 'Seller6', 'DRAFT', 0);

insert into invoice(buyer, created_date, last_modified_date_time, payment_date, seller, status, version)
    values ('Marcin Kowalski2', '2025-03-30T12:48:19', '2025-03-30T12:48:19', '2025-03-30', 'Zbigniew Nowak', 'DELETED', 0);