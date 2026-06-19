# gym-db/Dockerfile
FROM postgres:15-alpine

ENV TZ=Europe/Madrid

# Copia el script de inicio directamente a la carpeta  Postgres
COPY init.sql /docker-entrypoint-initdb.d/

EXPOSE 5432