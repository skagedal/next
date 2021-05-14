use git_assistant::{analyze_repository, Analysis};
use std::path::{PathBuf, Path};
use std::env;
use std::process::Command;
use temp_testdir::TempDir;

#[test]
fn not_a_git_directory() {
    let result = analyze_repository(env::temp_dir());
    assert_eq!(Analysis::NotGitRepository, result.unwrap())
}

#[test]
fn has_no_upstream() {
    let tmpdir = TempDir::default();
    let result = analyze_repository(unpack_fixture("has-no-upstream", &tmpdir));
    assert_eq!(Analysis::HasNoUpstream, result.unwrap());
}

#[test]
fn identical_to_upstream() {
    let tmpdir = TempDir::default();
    let result = analyze_repository(unpack_fixture("identical-to-upstream", &tmpdir));
    assert_eq!(Analysis::IdenticalToUpstream, result.unwrap());
}

fn unpack_fixture(fixture_name: &str, path: &Path) -> PathBuf {
    let file_name = format!("{}.tar.gz", fixture_name);
    let tarball = env::current_dir().unwrap().join("fixtures").join(file_name);

    let exit_status = Command::new("tar")
        .arg("xzf")
        .arg(tarball.to_str().unwrap())
        .current_dir(path)
        .status()
        .unwrap();
    assert_eq!(0, exit_status.code().unwrap());
    path.join(fixture_name)
}


