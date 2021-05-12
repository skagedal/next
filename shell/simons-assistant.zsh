function work() {
  simons-assistant track-start
}

function wedit() {
  date +%H:%M | tr -d '\n' | pbcopy
  simons-assistant track-edit
}

function wreport() {
  simons-assistant track-report
}

function wstop() {
  simons-assistant track-stop
}
