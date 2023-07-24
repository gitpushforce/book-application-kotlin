FROM mysql:8.0

# copy config file
COPY files/conf/characterset.cnf /etc/mysql/conf.d/characterset.cnf
RUN chmod 644 /etc/mysql//conf.d/characterset.cnf

# listen port
EXPOSE 3306

