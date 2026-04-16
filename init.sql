CREATE TABLE IF NOT EXISTS jugadores (
    id         SERIAL PRIMARY KEY,
    nombre     VARCHAR(50) UNIQUE NOT NULL,
    color      VARCHAR(20) NOT NULL,
    contrasena VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS partidas (
    id                 SERIAL PRIMARY KEY,
    fecha              VARCHAR(20) NOT NULL,
    cantidad_jugadores INT
);

CREATE TABLE IF NOT EXISTS jugadores_partida (
    id_partida      INT REFERENCES partidas(id),
    nombre_jugador  VARCHAR(50) REFERENCES jugadores(nombre)
);
