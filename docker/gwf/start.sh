#!/bin/bash

ls &
node gateway.js --local_ip "127.0.0.1" --local_port 8080 --remote_ip "sdci-gwi.default.svc.cluster.local" --remote_port 8080 --remote_name "gwi" &

until nc -z localhost 8080; do
  echo "En attente que gateway.js soit prêt..."
  sleep 1
done

echo "Gateway.js est prêt, lancement de device.js..."

node device.js --local_ip "127.0.0.1" --local_port 9001 --remote_ip "127.0.0.1" --remote_port 8080 --send_period 3000