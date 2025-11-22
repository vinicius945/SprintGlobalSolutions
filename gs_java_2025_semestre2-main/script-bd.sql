-- Script de Criação do Banco de Dados - Global Solution 2025
-- Baseado nas Entidades JPA do projeto LTAKN

-- Tabela de Usuários (Login)
CREATE TABLE GS_TB_USER (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('USER', 'ADMIN'))
);

-- Tabela de Departamentos
CREATE TABLE GS_TB_DEPARTAMENTO (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    numero_horas_maximas INT NOT NULL
);

-- Tabela de Funcionários
CREATE TABLE GS_TB_FUNCIONARIO (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    horas_trabalhadas_ultimo_mes INT NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('SAUDAVEL', 'EM_RISCO')),
    departamento_id BIGINT NOT NULL,
    CONSTRAINT FK_FUNCIONARIO_DEPARTAMENTO FOREIGN KEY (departamento_id) 
    REFERENCES GS_TB_DEPARTAMENTO(id)
);

-- Inserção de dados iniciais (Opcional, mas bom para testes)
INSERT INTO GS_TB_DEPARTAMENTO (nome, numero_horas_maximas) VALUES ('TI', 160);
INSERT INTO GS_TB_DEPARTAMENTO (nome, numero_horas_maximas) VALUES ('RH', 180);