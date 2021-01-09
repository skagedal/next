#!/usr/bin/env bash

set -e

TOOL=simons-assistant
BIN=${HOME}/local/bin
BUILT_BINARY=`pwd`/build/install/${TOOL}/bin/${TOOL}

./gradlew install
ln -fs ${BUILT_BINARY} ${BIN}/${TOOL}

if [ -d ~/.oh-my-zsh ]; then
    _SIMONS_ASSISTANT_COMPLETE=zsh ${BUILT_BINARY} > ~/.oh-my-zsh/custom/simons-assistant.zsh
    ln -fs `pwd`/shell/simons-assistant.zsh ~/.oh-my-zsh/custom/simons-assistant-function.zsh
fi

