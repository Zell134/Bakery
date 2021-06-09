#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -i ./.ssh/london-key.pem \
    target/Bakery-1.0-SNAPSHOT.jar \
    ubuntu@18.168.254.73:/home/ubuntu

echo 'restart server..'

ssh -i ./.ssh/london-key.pem ubuntu@18.168.254.73 <<EOF

pgrep java | xargs kill -9
nohup java -jar Bakery-1.0-SNAPSHOT.jar > log.txt &

EOF

echo 'Bye'