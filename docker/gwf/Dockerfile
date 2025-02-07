FROM node:23-alpine3.20

WORKDIR /app

RUN npm install express request systeminformation yargs

COPY . .

# Donner les bonnes permissions au fichier start.sh
RUN chmod +x /app/start.sh

EXPOSE 8080
# Exécuter le script start.sh avec sh
CMD ["/bin/sh", "/app/start.sh"]
#CMD node gateway.js --local_ip "127.0.0.1" --local_port 8080 --remote_ip "sdci-gwi.default.svc.cluster.local" --remote_port 8080 --remote_name "gwi" & sleep 10 && node device.js --local_ip "127.0.0.1" --local_port 9001 --local_name "dev" --remote_ip "127.0.0.1" --remote_port 8080 --send_period 3000
