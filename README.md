# simons-assistant 

**simons-assistant** is a command line application that tells you what to do next. It is a micro-project, can be seen mostly as a group of scripts I use to manage my computer.

## Configuration

Create a file called ~/.simons-assistant/tasks.yml. This should be an YML array of tasks that simons-assistant checks for you. Example:

```yaml
- task: file-system-lint
- task: brew-upgrade
- task: gmail
  account: skagedal@gmail.com
- task: gmail
  account: other-account@gmail.com
```

Those are the kinds of tasks that exist at the moment.

### Gmail integration

Currently you need to go into the [Google APIs developers console](https://console.developers.google.com/apis/credentials/oauthclient/) and create a token. Download the `credentials.json` file and move it to ~/.simons-assistant/google-oauth-credentials.json.