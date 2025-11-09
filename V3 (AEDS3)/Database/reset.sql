use exy;

-- Disable constraints (FKs)
SET FOREIGN_KEY_CHECKS = 0;

-- Limpa os dados de todas as tabelas
TRUNCATE TABLE Command;
TRUNCATE TABLE Member;
TRUNCATE TABLE Server;
TRUNCATE TABLE User;
TRUNCATE TABLE RegisterServerCommand;
TRUNCATE TABLE RegisterUserCommand;

-- Enable constraints
SET FOREIGN_KEY_CHECKS = 1;

