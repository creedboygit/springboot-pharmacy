FROM mariadb:11

ENV TZ=Asia/Seoul

# Install MySQL client
RUN apt-get update && apt-get install -y mariadb-client

# Optionally, clean up the package cache to reduce image size
RUN apt-get clean && rm -rf /var/lib/apt/lists/*