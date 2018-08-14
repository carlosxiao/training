CREATE TABLE if NOT EXISTS user (
id int not NULL  PRIMARY KEY auto_increment,
username VARCHAR(200),
password VARCHAR(200),
status int
)