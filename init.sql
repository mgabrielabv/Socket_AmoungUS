CREATE TABLE IF NOT EXISTS jugadores (
    id         SERIAL PRIMARY KEY,
    nombre     VARCHAR(50) UNIQUE NOT NULL,
    color      VARCHAR(20) NOT NULL,
    contrasena VARCHAR(100) NOT NULL
);
