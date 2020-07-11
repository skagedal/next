#!/usr/bin/env bash

set -e

BIN=${HOME}/local/bin

./gradlew install
ln -fs `pwd`/build/install/next/bin/next ${BIN}/next
