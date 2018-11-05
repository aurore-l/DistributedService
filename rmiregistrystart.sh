#!/bin/bash

pushd $(dirname $0) > /dev/null
basepath=$(pwd)


cd $basepath/bin
rmiregistry
