#!/bin/bash

pushd $(dirname $0) > /dev/null
basepath=$(pwd)
popd > /dev/null

java -cp "$basepath"/repartiteur.jar:"$basepath"/shared.jar -Djava.security.policy="$basepath"/policy Repartiteur.Repartiteur $*
