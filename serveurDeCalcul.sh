#!/bin/bash

basepath=$(pwd)

java -cp "$basepath"/fileserver.jar:"$basepath"/shared.jar -Djava.security.policy="$basepath"/policy polymtl.TP1.FileServer.FileServer

