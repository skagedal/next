#!/usr/bin/env bash

set -e

BIN=${HOME}/local/bin
SHELL_SCRIPTS=${HOME}/.oh-my-zsh/custom

./gradlew install
ln -fs `pwd`/build/install/simons-assistant/bin/simons-assistant ${BIN}/simons-assistant
ln -fs `pwd`/shell/simons-assistant.zsh ${SHELL_SCRIPTS}/simons-assistant.zsh
