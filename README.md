# simons-assistant

**simons-assistant** is a command line application to manage your git repositories. It is built to be used together with [assistant](https://github.com/skagedal/assistant) but can be used as a standalone program. 

## Git cleanup

The `simons-assistant git-clean` command cleans up branches in the git repository of the current working directory.

For each local branch, it compares to upstream and gives you a selection of options depending on current state.

Compare it to the "clean-up" action I do in another script I have:

```shell
git branch -r | awk '{print $1}' | egrep -v -f /dev/fd/0 <(git branch -vv | grep origin) | awk '{print $1}' | xargs git branch -d
```