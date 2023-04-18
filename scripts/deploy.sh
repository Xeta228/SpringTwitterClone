#!/usr/bin/env bash

mvn clean package

echo 'Copyting files...'

scp -i ~/.ssh/authorized_keys \
    target/webapp-0.0.1-SNAPSHOT.jar \
    baron_server@192.168.1.57:/home/baron_server/

echo 'Restarting Server...'

ssh -i ~/.ssh/id_ed25519 baron_server@192.168.1.57 << EOF
pgrep java | xargs kill -9
nohup java -jar webapp-0.0.1-SNAPSHOT.jar > log.txt &
EOF

echo 'Bye'






