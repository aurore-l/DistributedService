#!/bin/bash

basepath=$(pwd)

java -cp "$basepath"/serveurDeNom.jar:"$basepath"/shared.jar -Djava.rmi.server.hostname=$1 -Djava.security.policy="$basepath"/policy ServeurDeNom.ServeurDeNom $2

