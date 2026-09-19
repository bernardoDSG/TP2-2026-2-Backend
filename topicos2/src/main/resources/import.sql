-- Dados iniciais carregados pelo Hibernate em dev e test.
-- Tonalidade usa o ordinal JPA: CLARO=0, ESCURO=1, PURO=2.
-- StatusUso usa StatusUsoConverter: SEMINOVO=1, USADO=2.

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