DROP DATABASE IF EXISTS exy;
CREATE DATABASE exy;
USE exy;

-- User table

CREATE TABLE User (
	
	id_user INT PRIMARY KEY AUTO_INCREMENT,
    id CHAR(19) UNIQUE NOT NULL,
    id_dm CHAR(19) UNIQUE NOT NULL,
    username VARCHAR(45) NOT NULL,
    tag VARCHAR(45) UNIQUE NOT NULL,
    avatar_url VARCHAR(200) NOT NULL,
    is_bot BOOLEAN NOT NULL,
    role VARCHAR(10) NOT NULL,
    requests INT NOT NULL,
    first_request_time DATETIME NOT NULL,
    last_update_time DATETIME NOT NULL
    
);
    
-- Server table

CREATE TABLE Server (

	id_server INT PRIMARY KEY AUTO_INCREMENT,
	id CHAR(19) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL,
    name VARCHAR(45) NOT NULL,
    last_update_time DATETIME NOT NULL
);

-- Member table

CREATE TABLE Member (

	id_member INT PRIMARY KEY AUTO_INCREMENT,
    id_user INT NOT NULL, 
    id_server INT NOT NULL, 
    nickname VARCHAR(45) NOT NULL,
    roles VARCHAR (1000) NOT NULL,
    avatar_url VARCHAR(200) NOT NULL,
    joined_at DATETIME NOT NULL,
    last_update_time DATETIME NOT NULL,
    
    FOREIGN KEY (id_user) REFERENCES User(id_user),
    FOREIGN KEY (id_server) REFERENCES Server(id_server),
    
    UNIQUE(id_user, id_server)
);

-- Command table

CREATE TABLE Command (
	
    id_command INT PRIMARY KEY AUTO_INCREMENT,
    id_user INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    parameters VARCHAR(500) NOT NULL,
    request_time DATETIME NOT NULL,
    
    FOREIGN KEY (id_user) REFERENCES User(id_user)
);

-- RegisterUserCommand table

CREATE TABLE RegisterUserCommand (
	
    id_register_user_command INT PRIMARY KEY AUTO_INCREMENT,
    id_command INT UNIQUE NOT NULL,
    
    FOREIGN KEY (id_command) REFERENCES Command(id_command)
);

-- RegisterServerCommand table

CREATE TABLE RegisterServerCommand(
	
	id_register_server_command INT PRIMARY KEY AUTO_INCREMENT,
    id_command INT UNIQUE NOT NULL,
    id_server INT UNIQUE NOT NULL,
    
    FOREIGN KEY (id_command) REFERENCES Command(id_command),
    FOREIGN KEY (id_server) REFERENCES Server(id_server)
);

