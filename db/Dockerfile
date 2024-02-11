# Use latest MySQL image
FROM mysql
# Set working directory
WORKDIR /tmp
# Copy all files to the working directory
COPY test_db/*.sql /tmp/
COPY test_db/*.dump /tmp/
# Copy main SQL file to docker-entrypoint-initdb.d
# Script and SQL files in this folder are executed on container setup
# Specific to MySQL
COPY test_db/employees.sql /docker-entrypoint-initdb.d/
# Set root password
ENV MYSQL_ROOT_PASSWORD password