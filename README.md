# simons-assistant 

**simons-assistant** is a command line application that tells you what to do next. It is a micro-project, can be seen mostly as a group of scripts I use to manage my computer.

## Configuration

Create a file called ~/.simons-assistant/tasks.yml. This should be an YML array of tasks that simons-assistant checks for you. Example:

```yaml
- task: custom
  id: check-github-notifications # Needs to be something unique
  shell: ./check-github-notifications.sh # Script to start
  directory: ~/kry/code/simon-kry/scripts 
  when: daily
- task: file-system-lint
- task: brew-upgrade
- task: gmail
  account: skagedal@gmail.com
- task: gmail
  account: other-account@gmail.com
```

The following are the existing task types.

### `custom`

Run a shell script.

* `id`: Identifier of this task.
* `shell`: Shell command line to run.
* `directory`: Where to run it.
* `when`: When to run it.  Can be `daily` and a couple of other specifications.  Check source code. 

### `brew-upgrade`

Runs brew upgrade.  Should perhaps be replaced by just using `custom`.

* `when`: Time interval specification. 

### `file-system-lint`

Not configurable at the moment.

### `git-repos`

* `directory`: Directory where there are git repos to update. 

### `gmail`

* `account`: Your e-mail address. 

Currently you need to go into the [Google APIs developers console](https://console.developers.google.com/apis/credentials/oauthclient/) and create a token. Download the `credentials.json` file and move it to ~/.simons-assistant/google-oauth-credentials.json.

### `establish-work-or-hobby`

* Work in progress.  The idea is that you should be able to have different tasks active depending on different modes, such as if you're working or not. 

## Git cleanup

The `simons-assistant git-clean` command cleans up branches in the git repository of the current working directory.

For each local branch, it compares to upstream and gives you a selection of options depending on current state.

Compare it to the "clean-up" action I do in another script I have:

```shell
git branch -r | awk '{print $1}' | egrep -v -f /dev/fd/0 <(git branch -vv | grep origin) | awk '{print $1}' | xargs git branch -d
```