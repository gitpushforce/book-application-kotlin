FROM mysql:5.7.29

#TIMEZONE
RUN echo "${TZ}" > /etc/timezone \
    && dpkg-reconfigure -f noninteractive tzdata

# copy config file
COPY files/conf/characterset.cnf /etc/mysql/conf.d/characterset.cnf
RUN chmod 644 /etc/mysql//conf.d/characterset.cnf

# listen port
EXPOSE 3306

