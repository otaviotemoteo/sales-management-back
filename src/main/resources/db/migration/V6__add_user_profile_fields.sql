ALTER TABLE users
    ADD COLUMN phone       VARCHAR(20),
    ADD COLUMN cpf         VARCHAR(14),
    ADD COLUMN city        VARCHAR(100),
    ADD COLUMN state       VARCHAR(2),
    ADD COLUMN bio         TEXT,
    ADD COLUMN avatar_url  VARCHAR(500);

CREATE UNIQUE INDEX idx_users_cpf ON users(cpf) WHERE cpf IS NOT NULL;
