function next () {
  simons-assistant next
  return_value=$?
  if [ $return_value -eq 10 ]; then
    cd "$(< ~/.simons-assistant/data/requested-directory)"
  fi
}

function work () {
    simons-assistant track-start
}

function wedit() {
    date +%H:%M | tr -d '\n' | pbcopy
    simons-assistant track-edit
}

function wreport() {
    simons-assistant track-report
}
