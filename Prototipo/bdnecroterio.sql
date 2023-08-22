CREATE DATABASE bdnecroterio;

-- Apaga a tabela cadáver caso exista
DROP TABLE IF EXISTS cadaver CASCADE;

-- Cria tabela cadáver
CREATE TABLE cadaver (
	identificacao CHAR(14) PRIMARY KEY,
    nome_cadaver VARCHAR(100) NOT NULL,
    peso DOUBLE,
    dataMorte VARCHAR(10),
    horaMorte VARCHAR(5),
    cpf_funcionario CHAR(14) REFERENCES funcionario(cpf),
    situacao VARCHAR(50) NOT NULL
    );
 
-- Apaga a tabela funcionário caso exista
DROP TABLE IF EXISTS funcionario CASCADE;

-- Cria tabela funcionário
CREATE TABLE funcionario(
	cpf CHAR(14) PRIMARY KEY,
	nome VARCHAR(100) NOT NULL,
	login_acesso VARCHAR(100),
	senha VARCHAR(10),
	cargo VARCHAR(50) 
    );

-- Insere tuplas em funcionário
INSERT INTO funcionario
VALUES ('000.000.000-00', 'João da Silva', 'admin', 'admin', 'Administrador'),
('555.666.777-22', 'Maria Carolina', 'maria_carolina', 'maria123', 'Auxiliar de Necropsia'), 
('111.222.333-44' ,  'Pamela Miranda', 'pamela_miranda', 'pam123', 'Patologista'),
('888.999.000-55' ,  'Unaê Tuinambás', 'unae_tupinambas', 'unae123', 'Assistente de Necropsia'), 
('444.555.666-77',  'Lê Alves' , 'le_alves', 'le123', 'Técnico de Autopsia'),
('222.333.444-55', 'Fernanda Faria', 'fernanda_faria', 'fera123',  'Patologista'),
('666.777.888-99', 'Fernando Castro', 'fernando_castro', 'fero123', 'Auxiliar de Autopsia'),
('999.000.111-22', 'Helen Costa', 'helena_costa', 'helen123', 'Embalsamadora'),
('333.444.555-66', 'Ricardo Santos', 'ricardo_santos', 'ricardo123', 'Assitente de Necropsia'),
('123.456.789-00',  'Ana Pereira', 'ana_pereira', 'ana123', 'Auxiliar de Autópsia'),
('987.654.321-11', 'Marcelo Souza', 'marcelo_souza', 'marcelo123', 'Patologista');

-- Insere tuplas em cadáver
INSERT INTO cadaver
VALUES ('123.456.789-01', 'Maria Silva', 70.5, '13/08/2023', '15:30', '555.666.777-22', 'Aguardando Autópsia'),
('987.654321-02', 'João Santos', 85.2, '12/08/2023', '08:45', '555.666.777-22', 'Em Processo de Autópsia'),
  ('555.666.777-03', 'Ana Oliveira', 62.0, '11/08/2023', '20:10', '111.222.333-44', 'Recebido'),
  ('111.222.333-04', 'Carlos Pereira', 78.7, '11/08/2023', '14:20', '888.999.000-55', 'Aguardando Liberação'),
  ('888.999.000-05', 'Rita Alves', 56.8, '10/08/2023', '09:30', '444.555.666-77', 'Em Processo de Embalsamamento'),
  ('444.555.666-06', 'Paulo Rodrigues', 92.3, '01/08/2023', '11:15', '222.333.444-55', 'Em Preparação para Enterro'),
  ('222.333.444-07', 'Fernanda Costa', 68.5, '09/08/2023', '18:05', '666.777.888-99', 'Em Processo de Autópsia'),
  ('666.777.888-08', 'Lucas Souza', 74.9, '13/08/2023', '07:40', '999.000.111-22', 'Recebido'),
  ('999.000.111-09', 'Luisa Lima', 61.2, '12/08/2023', '16:25', '333.444.555-66', 'Recebido'),
  ('333.444.555-10', 'Ricardo Santos', 80.0, '02/08/2023', '13:50', '123.456.789-00', 'Aguardando Liberação');
  



