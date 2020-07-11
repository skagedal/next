#!/usr/bin/env bash

set -e

BIN=${HOME}/local/bin

./gradlew install
ln -fs `pwd`/build/install/simons-assistant/bin/simons-assistant ${BIN}/simons-assistant
