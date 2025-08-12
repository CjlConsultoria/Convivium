-- Tabela de roles
INSERT INTO tb_usuario_role (id, role) VALUES (1, 'ADMIN') ON CONFLICT (id) DO NOTHING;
INSERT INTO tb_usuario_role (id, role) VALUES (2, 'ADMINISTRATIVO') ON CONFLICT (id) DO NOTHING;
INSERT INTO tb_usuario_role (id, role) VALUES (3, 'USUARIO') ON CONFLICT (id) DO NOTHING;

-- Tabela tb_tipo (cargos)
INSERT INTO convivium.tb_tipo (id, descricao) VALUES (1, 'Síndico') ON CONFLICT (id) DO NOTHING;
INSERT INTO convivium.tb_tipo (id, descricao) VALUES (2, 'Conselheiro') ON CONFLICT (id) DO NOTHING;
INSERT INTO convivium.tb_tipo (id, descricao) VALUES (3, 'Zelador') ON CONFLICT (id) DO NOTHING;
INSERT INTO convivium.tb_tipo (id, descricao) VALUES (4, 'Administrativo') ON CONFLICT (id) DO NOTHING;
INSERT INTO convivium.tb_tipo (id, descricao) VALUES (5, 'Morador') ON CONFLICT (id) DO NOTHING;
INSERT INTO convivium.tb_tipo (id, descricao) VALUES (6, 'Inquilino') ON CONFLICT (id) DO NOTHING;
INSERT INTO convivium.tb_tipo (id, descricao) VALUES (7, 'Funcionário') ON CONFLICT (id) DO NOTHING;
INSERT INTO convivium.tb_tipo (id, descricao) VALUES (8, 'Segurança') ON CONFLICT (id) DO NOTHING;
INSERT INTO convivium.tb_tipo (id, descricao) VALUES (9, 'Portaria') ON CONFLICT (id) DO NOTHING;
INSERT INTO convivium.tb_tipo (id, descricao) VALUES (10, 'Vigilante') ON CONFLICT (id) DO NOTHING;
INSERT INTO convivium.tb_tipo (id, descricao) VALUES (11, 'Limpeza') ON CONFLICT (id) DO NOTHING;
INSERT INTO convivium.tb_tipo (id, descricao) VALUES (99, 'Outro') ON CONFLICT (id) DO NOTHING;

-- Empresa 1: ADMIN
INSERT INTO convivium.tb_usuario_empresa (id, empresa, cnpj, cep, logradouro, numero, complemento, bairro, cidade, estado, id_usuario_responsavel, data_criacao, data_atualizacao) VALUES (1, 'ADMIN LTDA', '00.000.000/0001-00', '01001000', 'Praça da Sé', '100', 'Ed. Central', 'Centro', 'São Paulo', 'SP', null, now(), now()) ON CONFLICT (id) DO NOTHING;

-- Empresa 2: WATCHCAR
INSERT INTO convivium.tb_usuario_empresa (id, empresa, cnpj, cep, logradouro, numero, complemento, bairro, cidade, estado, id_usuario_responsavel, data_criacao, data_atualizacao) VALUES (2, 'WATCHCAR SOLUÇÕES', '11.111.111/0001-11', '04547000', 'Av. Eng. Luís Carlos Berrini', '500', 'Conj. 102', 'Brooklin', 'São Paulo', 'SP', null, now(), now()) ON CONFLICT (id) DO NOTHING;

-- Empresa 3: CJL CONSULTORIA
INSERT INTO convivium.tb_usuario_empresa (id, empresa, cnpj, cep, logradouro, numero, complemento, bairro, cidade, estado, id_usuario_responsavel, data_criacao, data_atualizacao) VALUES (3, 'CJL CONSULTORIA E TECNOLOGIA', '22.222.222/0001-22', '30130010', 'Rua da Bahia', '800', 'Sala 10', 'Centro', 'Belo Horizonte', 'MG', null, now(), now()) ON CONFLICT (id) DO NOTHING;


-- Usuário
INSERT INTO convivium.tb_usuario (id, nome, sobrenome, senha, email, cpf, telefone, cep, logradouro, cidade, estado, bairro, numero, complemento, genero, alerta, ativo, status, bloco, apartamento, vaga_carro, vaga_moto, tipo, role, empresa, data_criacao, data_atualizacao) VALUES (1, 'Cleyton', 'Sales', '123', 'joao.silva@email.com', '47599293809', '11999999999', '12345000', 'Rua das Flores', 'São Paulo', 'SP', 'Jardim Paulista', '123', 'Apto 35', 'Masculino', true, true, 'ativo', 'B', '101', '15', '3', 1, 1, 2, now(), now()) ON CONFLICT (id) DO NOTHING;



