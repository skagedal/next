use std::error::Error;
use std::path::PathBuf;

use git2::{Branch, Branches, ErrorCode, Repository};
use git2::BranchType::Local;

#[derive(Debug, PartialEq)]
pub enum Analysis {
    NotGitRepository,
    HasNoUpstream,
    IdenticalToUpstream,

    UnknownState
}

pub fn analyze_repository(path: PathBuf) -> Result<Analysis, Box<dyn Error>> {
    match Repository::open(path) {
        Ok(repository) => analyze_first_branch(repository),
        Err(error) =>
            if error.code() == ErrorCode::NotFound {
                Ok(Analysis::NotGitRepository)
            } else {
                Err(Box::new(error))
            }
    }
}

pub fn analyze_branches(repo: Repository) -> Result<Analysis, Box<dyn Error>> {
    let branches: Branches = repo.branches(Some(Local)).expect("Failed to get branches");
    for branch in branches {
        let (branch, _) = branch.expect("Failed to get branch");
        let _ = analyze_branch(&repo, branch);
    }
    Ok(Analysis::UnknownState)
}

pub fn analyze_first_branch(repo: Repository) -> Result<Analysis, Box<dyn Error>> {
    let mut branches: Branches = repo.branches(Some(Local)).expect("Failed to get branches");
    let (branch, _) = branches.next().unwrap().expect("Failed to get branch");
    analyze_branch(&repo, branch)
}

fn analyze_branch(repo: &Repository, branch: Branch) -> Result<Analysis, Box<dyn Error>> {
    match branch.upstream() {
        Ok(upstream) => analyze_branch_with_upstream(repo, branch, upstream),
        Err(err) => {
            eprintln!("Hmm: {:?}", err);
            Ok(Analysis::HasNoUpstream)
        }
    }
}

fn analyze_branch_with_upstream(repo: &Repository, branch: Branch, upstream: Branch) -> Result<Analysis, Box<dyn Error>> {
    let branch_commit = branch.into_reference().target().unwrap();
    let upstream_commit = upstream.into_reference().target().unwrap();
    if branch_commit == upstream_commit {
        return Ok(Analysis::IdenticalToUpstream)
    }
    let base = repo.merge_base(branch_commit, upstream_commit);
    println!("commit: {}, upstream: {}, base: {:?}", branch_commit, upstream_commit, base);

    Ok(Analysis::UnknownState)
}