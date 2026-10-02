-- Dados iniciais carregados pelo Hibernate em dev e test.
-- Tonalidade usa o ordinal JPA: CLARO=0, ESCURO=1, PURO=2.
-- StatusUso usa StatusUsoConverter: SEMINOVO=1, USADO=2.

insert into estado (nome, sigla) values ('Tocantins', 'TO');
insert into estado (nome, sigla) values ('Goias', 'GO');

insert into municipio (nome, estado_id)
values ('Palmas', (select id from estado where sigla = 'TO'));
insert into municipio (nome, estado_id)
values ('Araguaina', (select id from estado where sigla = 'TO'));
insert into municipio (nome, estado_id)
values ('Gurupi', (select id from estado where sigla = 'TO'));
insert into municipio (nome, estado_id)
values ('Goiania', (select id from estado where sigla = 'GO'));

insert into cor (nome, tonalidade) values ('Branco', 2);
insert into cor (nome, tonalidade) values ('Preto', 2);
insert into cor (nome, tonalidade) values ('Prata', 0);
insert into cor (nome, tonalidade) values ('Cinza', 1);
insert into cor (nome, tonalidade) values ('Vermelho', 2);
insert into cor (nome, tonalidade) values ('Azul', 1);

insert into carro (nome, statususo, cor_id)
values ('Toyota Corolla', 1, (select id from cor where nome = 'Prata'));
insert into carro (nome, statususo, cor_id)
values ('Honda Civic', 2, (select id from cor where nome = 'Preto'));
insert into carro (nome, statususo, cor_id)
values ('Volkswagen Gol', 2, (select id from cor where nome = 'Branco'));
insert into carro (nome, statususo, cor_id)
values ('Chevrolet Onix', 1, (select id from cor where nome = 'Vermelho'));
insert into carro (nome, statususo, cor_id)
values ('Hyundai HB20', 2, (select id from cor where nome = 'Cinza'));
insert into carro (nome, statususo, cor_id)
values ('Fiat Argo', 1, (select id from cor where nome = 'Azul'));
insert into carro (nome, statususo, cor_id)
values ('Jeep Renegade', 2, (select id from cor where nome = 'Preto'));
insert into carro (nome, statususo, cor_id)
values ('Ford Ka', 2, (select id from cor where nome = 'Branco'));
insert into carro (nome, statususo, cor_id)
values ('Nissan Kicks', 1, (select id from cor where nome = 'Prata'));
insert into carro (nome, statususo, cor_id)
values ('Renault Kwid', 2, (select id from cor where nome = 'Vermelho'));

insert into cliente (nome, cpf, email, telefone)
values ('Ana Paula Ribeiro', '11144477735', 'ana.ribeiro@example.com', '63992345678');
insert into cliente (nome, cpf, email, telefone)
values ('Carlos Eduardo Lima', '93541134780', 'carlos.lima@example.com', '63993456789');
insert into cliente (nome, cpf, email, telefone)
values ('Fernanda Souza Alves', '39053344705', 'fernanda.alves@example.com', '62994567890');

insert into endereco (cep, logradouro, numero, complemento, bairro, municipio_id, cliente_id)
values (
	'77001000', 'Avenida JK', '1200', 'Casa 3', 'Plano Diretor Norte',
	(select id from municipio where nome = 'Palmas' and estado_id = (select id from estado where sigla = 'TO')),
	(select id from cliente where cpf = '11144477735')
);
insert into endereco (cep, logradouro, numero, complemento, bairro, municipio_id, cliente_id)
values (
	'77800000', 'Rua das Palmeiras', '85', null, 'Centro',
	(select id from municipio where nome = 'Araguaina' and estado_id = (select id from estado where sigla = 'TO')),
	(select id from cliente where cpf = '93541134780')
);
insert into endereco (cep, logradouro, numero, complemento, bairro, municipio_id, cliente_id)
values (
	'74000000', 'Rua 10', '450', 'Apartamento 802', 'Setor Oeste',
	(select id from municipio where nome = 'Goiania' and estado_id = (select id from estado where sigla = 'GO')),
	(select id from cliente where cpf = '39053344705')
);