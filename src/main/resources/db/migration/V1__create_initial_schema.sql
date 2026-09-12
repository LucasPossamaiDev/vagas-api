CREATE TABLE empresas (

    id BIGSERIAL PRIMARY KEY,
    nome varchar(150) NOT NULL,
    email varchar(150) NOT NULL UNIQUE,
    cnpj varchar(14) NOT NULL,
    senha varchar(255) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE usuarios (

    id BIGSERIAL PRIMARY KEY,
    nome varchar(150) NOT NULL,
    senha varchar(255) NOT NULL,
    email varchar(150) NOT NULL UNIQUE,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE curriculos (

    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE REFERENCES usuarios(id),
    resumo varchar(500),
    arquivos_pdf_url varchar(500),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now()

);

CREATE TABLE vagas (

    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    titulo varchar (150) NOT NULL,
    descricao varchar (500) NOT NULL,
    status varchar (20) NOT NULL DEFAULT 'aberta',CHECK (status IN ('aberta', 'fechada')),
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now()

);

CREATE TABLE tecnologias
(

    id   BIGSERIAL PRIMARY KEY,
    nome varchar(150) NOT NULL UNIQUE
);
CREATE TABLE vagas_tecnologias (

    vaga_id BIGINT NOT NULL REFERENCES vagas(id),
    tecnologia_id BIGINT NOT NULL REFERENCES tecnologias(id),
    PRIMARY KEY (vaga_id, tecnologia_id)
);

CREATE TABLE curriculos_tecnologias (

    curriculo_id BIGINT NOT NULL REFERENCES curriculos(id),
    tecnologia_id BIGINT NOT NULL REFERENCES tecnologias(id),
    PRIMARY KEY (curriculo_id, tecnologia_id)
);

CREATE TABLE candidaturas (

    id BIGSERIAL PRIMARY KEY,
    vaga_id BIGINT NOT NULL REFERENCES vagas(id),
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    status varchar (20) NOT NULL DEFAULT 'pendente', CHECK (status IN ('pendente', 'aceita', 'rejeitada')),
    data_candidatura TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (vaga_id, usuario_id)
);