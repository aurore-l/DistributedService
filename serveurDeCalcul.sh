#!/bin/bash

basepath=$(pwd)

java -cp "$basepath"/serveurDeCalcul.jar:"$basepath"/shared.jar -Djava.rmi.server.hostname=$1 -Djava.security.policy="$basepath"/policy ServeurDeCalcul.ServeurDeCalcul $2 $3 $4 $5 $6 $7

