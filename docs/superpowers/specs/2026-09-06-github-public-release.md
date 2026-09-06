# GitHub Public Release Design

## Goal

Preserve the current local development state as `old`, then create a separate
Git repository with a clean, single-commit public history tagged `new`.
The public repository must be suitable for GitHub and must not expose runtime
credentials, payment keys, local Maven caches, logs, or development tooling.

## Repository Layout

| Location | Purpose | Remote policy |
| --- | --- | --- |
| `D:\myCode\qiyu-live-app` | Existing working repository and local archive | Never push `old` or all refs/tags to a public remote |
| `D:\myCode\qiyu-live-app-public` | New repository containing the sanitized public release | May later be connected to GitHub |

## Archive Version: `old`

1. Update `.gitignore` before staging so generated artifacts are excluded.
2. Stage project source, documentation, migration scripts, Compose definitions,
   and current intended changes only.
3. Create one local archive commit and an annotated `old` tag.
4. Do not add a remote and do not run `git push --all` or `git push --tags` in
   the archive repository.

## Public Version: `new`

1. Export the local `old` snapshot into a separate directory without the
   original `.git` history.
2. Replace operational secrets in Nacos and Compose examples with environment
   variable placeholders. No Alipay private key, actual callback domain, or
   local database password may be present.
3. Keep `.env.docker.example` as a documented template; ignore `.env.docker`,
   `.env`, runtime homes, logs, and local Maven repositories.
4. Add a public README section covering configuration prerequisites and a
   GitHub Actions compile workflow.
5. Initialize a fresh Git repository, create a single initial commit, and tag
   it `new`.
6. Verify with secret-pattern checks, `git status`, and Maven compilation. Do
   not create a remote or push unless explicitly requested later.

## Explicit Exclusions

- `.mvn-local-repo/`
- `**/.runtime-logs/` and `*.log`
- `.runtime-home/`
- `.env`, `.env.*`, except committed `*.example` templates
- `.agents/` local agent skills and generated reports
- Docker volumes, IDE metadata, and build output

## Credential Handling

The existing Nacos example content has contained an Alipay sandbox private
key. The public release will contain placeholders only. The sandbox key must
be rotated in Alipay before any public push, because the local archive retains
the prior value by design.

## License

No license file is added automatically. Select a license before publishing so
GitHub users know whether reuse is permitted; MIT is the default recommendation
for this portfolio project.
