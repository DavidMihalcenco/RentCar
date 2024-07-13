DROP TABLE IF EXISTS rent;
DROP TABLE IF EXISTS rent_request;
DROP TABLE IF EXISTS car;
DROP TABLE IF EXISTS agency;
DROP TABLE IF EXISTS user;

CREATE TABLE IF NOT EXISTS user (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    email varchar(70)
);

CREATE TABLE IF NOT EXISTS agency (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    location varchar(50),
    name varchar(50),
    phone_number varchar(15),
    img varchar(350),
    owner_id bigint,
    CONSTRAINT agency_user_fk FOREIGN KEY (owner_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS car(
    id bigint AUTO_INCREMENT PRIMARY KEY,
    nr_seats int,
    price decimal(10, 2),
    img varchar(350),
    agency_id bigint,
    CONSTRAINT car_agency_fk FOREIGN KEY (agency_id) REFERENCES agency (id)
);

CREATE TABLE IF NOT EXISTS rent_request (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    end_date datetime(6),
    start_date datetime(6),
    user_id bigint,
    car_id bigint,
    moderator_id bigint,
    CONSTRAINT rent_request_moderator_fk FOREIGN KEY (moderator_id) REFERENCES user (id),
    CONSTRAINT rent_request_user_fk FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT rent_request_car_fk FOREIGN KEY (car_id) REFERENCES car (id)
);

CREATE TABLE IF NOT EXISTS rent (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    end_date datetime(6),
    start_date datetime(6),
    status varchar(50),
    moderator_id bigint,
    user_id bigint,
    car_id bigint,
    CONSTRAINT booking_moderator_fk FOREIGN KEY (moderator_id) REFERENCES user (id),
    CONSTRAINT booking_guest_fk FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT booking_room_fk FOREIGN KEY (car_id) REFERENCES car (id)
);

