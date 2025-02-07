FROM node:23-alpine3.20

WORKDIR /app

RUN npm install express request yargs

COPY . .

CMD node application.js --remote_ip "sdci-server.default.svc.cluster.local" --remote_port 8080 --device_name "dev1" --send_period 5000