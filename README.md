# Ringside Alert

Android app that tells you when WWE, AEW, TNA, NJPW, AAA, and other wrestling shows are on, and where to watch them.
Show times are built by a scheduled GitHub job (`tools/update_schedule.py`, data from [TVmaze](https://www.tvmaze.com/api), CC BY-SA) into `schedule.json`. Add or remove promotions in `tools/shows.json`.

You never build this on your own computer. GitHub builds it for you. See TESTING.md for what to check once it is installed.

## Step 1: Upload and test build

1. Unzip the download. Open the folder and upload everything inside it to the repo root (repo page > Add file > Upload files > Commit).
   - Mac: press Cmd+Shift+. in Finder so hidden folders show, then select all.
   - Afterward, check the repo shows `.github/workflows` with three files. If not, use Add file > Create new file and type `.github/workflows/ci.yml`, then paste the contents.
2. Open the Actions tab. "Build check" runs automatically. When it turns green, open the run and download `RingsideAlert-test-build` to try the app on a phone. If it fails, send me the log.

## Step 2: Fill the schedule

Actions > "Update schedule" > Run workflow. Read the log to see which shows TVmaze matched or skipped.

## Step 3: Make a signing key (once; keep it forever)

Easiest, with nothing to install: on the repo page press Code > Codespaces > Create codespace, open its terminal, and run (pick your own password):

    keytool -genkeypair -keystore ringside.jks -alias ringside -keyalg RSA -keysize 2048 -validity 10000 -storepass YOURPASSWORD -keypass YOURPASSWORD -dname "CN=Ringside Alert"
    base64 -w0 ringside.jks

Copy the long text it prints. Also right-click `ringside.jks` in the file list and download it as a backup. Never commit it. Then delete the codespace.

## Step 4: Add two secrets

Repo Settings > Secrets and variables > Actions > New repository secret:
- `KEYSTORE_BASE64` = the long text
- `KEYSTORE_PASSWORD` = YOURPASSWORD

## Step 5: Release

Releases > Draft a new release > create tag `v1.0.0` > Publish. The "Release" action attaches `RingsideAlert.apk` and a `.sha256` file in a few minutes. Share the release link.
For each update, publish a higher tag (`v1.0.1`). Installed apps update themselves.

## Notes

Users allow "install unknown apps" for Ringside Alert once. The test build from Step 1 is signed with a throwaway key, so uninstall it before installing the real release.
