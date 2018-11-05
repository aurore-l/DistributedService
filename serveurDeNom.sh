#!/bin/bash

basepath=$(pwd)

java -cp "$basepath"/authserver.jar:"$basepath"/shared.jar -Djava.security.policy="$basepath"/policy polymtl.TP1.AuthServer.AuthServer

