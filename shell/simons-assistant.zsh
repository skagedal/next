function next () {
  simons-assistant next
  return_value=$?
  if [ $return_value -eq 10 ]; then
    cd "$(< ~/.simons-assistant/data/requested-directory)"
  fi
}

function work () {
    date +%H:%M | pbcopy
    simons-assistant track-edit
}
