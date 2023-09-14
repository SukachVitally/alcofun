CREATE TABLE IF NOT EXISTS category (
    id SERIAL PRIMARY KEY,
    type_id smallint UNIQUE,
    created_at timestamp NOT NULL DEFAULT NOW()
);

CREATE SEQUENCE IF NOT EXISTS category_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS provider (
    id SERIAL PRIMARY KEY,
    type_id smallint UNIQUE,
    created_at timestamp NOT NULL DEFAULT NOW()
);

CREATE SEQUENCE IF NOT EXISTS provider_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    name character varying(255),
    ean character varying(255) UNIQUE,
    description text,
    category_id BIGINT NOT NULL REFERENCES category(id),
    provider_id BIGINT NOT NULL REFERENCES provider(id),
    created_at timestamp NOT NULL DEFAULT NOW()
);

CREATE SEQUENCE IF NOT EXISTS product_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS characteristic (
    id SERIAL PRIMARY KEY,
    type_id smallint,
    value character varying(255),
    created_at timestamp NOT NULL DEFAULT NOW(),
    CONSTRAINT unique_type_id_value UNIQUE (type_id, value)
);

CREATE SEQUENCE IF NOT EXISTS characteristic_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS product_image (
    id SERIAL PRIMARY KEY,
    path character varying(255),
    product_id BIGINT NOT NULL REFERENCES product(id),
    created_at timestamp NOT NULL DEFAULT NOW()
);

CREATE SEQUENCE IF NOT EXISTS product_image_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS provider_product (
    id SERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id),
    provider_id BIGINT NOT NULL REFERENCES provider(id),
    link character varying(255),
    price numeric(10, 2),
    created_at timestamp NOT NULL DEFAULT NOW()
);

CREATE INDEX provider_product_create_at_idx ON provider_product(created_at);

CREATE SEQUENCE IF NOT EXISTS provider_product_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS product_characteristic (
    id SERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id),
    characteristic_id BIGINT NOT NULL REFERENCES characteristic(id),
    created_at timestamp NOT NULL DEFAULT NOW(),
    CONSTRAINT unique_characteristic_id_product_id UNIQUE (characteristic_id, product_id)
);

CREATE SEQUENCE IF NOT EXISTS product_characteristic_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;