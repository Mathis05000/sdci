FROM node:23-alpine3.20

WORKDIR /app

RUN npm install express request systeminformation yargs

COPY . .

EXPOSE 8080

CMD node gateway.js --local_ip "127.0.0.1" --local_port 8080 --local_name "gwi" --remote_ip "sdci-server.default.svc.cluster.local" --remote_port 8080 --remote_name "srv"