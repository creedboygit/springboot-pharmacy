FROM mariadb:11

ENV TZ=Asia/Seoul

# Install MySQL client & vim
RUN apt-get update && apt-get install -y mariadb-client && apt-get install -y vim

# Optionally, clean up the package cache to reduce image size
RUN apt-get clean && rm -rf /var/lib/apt/lists/*