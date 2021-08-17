#!/usr/bin/env bash

host=18.133.29.188

mvn clean package

echo 'Copy files...'

scp -i ./.ssh/london-key.pem \
    target/Bakery-1.0-SNAPSHOT.jar \
    ubuntu@$host:/home/ubuntu

echo 'restart server..'

ssh -i ./.ssh/london-key.pem ubuntu@$host <<EOF

./start

EOF

echo 'Bye'