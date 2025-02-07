FROM node:23-alpine3.20

WORKDIR /app

RUN npm install express yargs systeminformation 

COPY . .

EXPOSE 8080

CMD node server.js --local_ip "127.0.0.1" --local_port 8080 --local_name "srv"
