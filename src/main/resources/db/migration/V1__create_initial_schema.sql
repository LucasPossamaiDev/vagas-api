CREATE TABLE empresas (

    id BIGSERIAL PRIMARY KEY,
    nome varchar(150) NOT NULL,
    email varchar(150) NOT NULL
);

CREATE TABLE usuarios (

    id BIGSERIAL PRIMARY KEY,
    nome varchar(150),
    senha varchar(15),
    email varchar(150)
);

CREATE TABLE curriculos (

    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
);