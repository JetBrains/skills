# Publishing to Maven Central

This guide documents the complete process for setting up Maven Central publishing for this library, including first-time setup and the release process.

## Prerequisites

- A GitHub account
- GPG installed locally (`brew install gnupg` on macOS)

## First-Time Setup

### 1. Create a Sonatype Account

1. Go to https://central.sonatype.com/
2. Click **Sign In** and authenticate with your GitHub account

### 2. Claim Your Namespace

Since this library uses `io.github.forketyfork`, Sonatype can verify ownership via GitHub.

1. Go to https://central.sonatype.com/publishing/namespaces
2. Click **Add Namespace**
3. Enter: `io.github.forketyfork`
4. Select **GitHub** as the verification method
5. Follow the verification steps (typically creating a temporary repository with a specific name)

### 3. Generate a Publishing Token

1. Go to https://central.sonatype.com/account
2. Click **Generate User Token**
3. Save both values securely:
   - **Username** → Will be used as `SONATYPE_USERNAME`
   - **Password** → Will be used as `SONATYPE_PASSWORD`

### 4. Generate a GPG Signing Key

```bash
# Generate a new GPG key
gpg --full-generate-key
```

When prompted:
- **Key type**: RSA and RSA (default)
- **Key size**: 4096
- **Expiration**: 0 (never expires) or your preference
- **Real name**: Your name or pseudonym
- **Email**: Your email
- **Passphrase**: Choose a secure passphrase → Save this as `SIGNING_PASSWORD`

```bash
# List your keys to get the key ID
gpg --list-secret-keys --keyid-format LONG
```

Example output:
```
sec   rsa4096/29C29385CD6AB534 2025-01-23 [SC]
      26C8D38D5560717A54554C9829C29385CD6AB534
uid                 [ultimate] Your Name <your@email.com>
ssb   rsa4096/3B24F74C86C67685 2025-01-23 [E]
```

The full key ID is `29C29385CD6AB534` (after `rsa4096/`).
The short key ID is the last 8 characters: `CD6AB534`.

### 5. Publish GPG Key to Keyservers

```bash
# Replace with your key ID
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

If one keyserver fails, try another - they sync with each other.

### 6. Export GPG Key for GitHub

The signing key must be exported in **raw ASCII-armored format** (with headers and newlines):

```bash
# Copy the raw armored key to clipboard (macOS)
gpg --armor --export-secret-keys YOUR_KEY_ID | pbcopy
```

The output should look like:
```
-----BEGIN PGP PRIVATE KEY BLOCK-----

lQdGBGlzQJQBEADE...
...many lines...
-----END PGP PRIVATE KEY BLOCK-----
```

**Important**: Do NOT base64 encode the key. Paste it directly into the GitHub secret with newlines preserved.

### 7. Add GitHub Secrets

Go to your repository: **Settings** → **Secrets and variables** → **Actions**

Add these repository secrets:

| Secret Name | Value | Notes |
|-------------|-------|-------|
| `SONATYPE_USERNAME` | Token username from step 3 | From Sonatype Central |
| `SONATYPE_PASSWORD` | Token password from step 3 | From Sonatype Central |
| `SIGNING_KEY_ID` | `CD6AB534` | **Short 8-character** key ID (last 8 chars) |
| `SIGNING_KEY` | Raw GPG key | ASCII-armored format with headers and newlines |
| `SIGNING_PASSWORD` | GPG passphrase | The passphrase you set when creating the key |

**Critical**: The `SIGNING_KEY_ID` must be the **short 8-character format**, not the full 16-character ID.

## Release Process

### 1. Update Version

Edit `build.gradle.kts` and update the version:

```kotlin
version = "0.2.0"  // New version
```

### 2. Commit and Push

```bash
git add build.gradle.kts
git commit -m "chore: bump version to 0.2.0"
git push
```

### 3. Create and Push Tag

```bash
git tag v0.2.0
git push origin v0.2.0
```

The GitHub Actions workflow will automatically:
1. Validate the tag version matches `build.gradle.kts`
2. Build the project
3. Sign and publish to Maven Central
4. Create a GitHub Release with auto-generated notes

### 4. Verify Publication

- Check the GitHub Actions workflow: https://github.com/forketyfork/compose-ui-test-server/actions
- After successful publication, the artifact will be available at:
  https://central.sonatype.com/artifact/io.github.forketyfork/compose-ui-test-server

Note: It may take 15-30 minutes for the artifact to appear in Maven Central search.

## Troubleshooting

### "Could not read PGP secret key"

This error usually means one of:

1. **Wrong key format**: The `SIGNING_KEY` must be raw ASCII-armored (with `-----BEGIN PGP PRIVATE KEY BLOCK-----` header), not base64 encoded or stripped.

2. **Wrong key ID format**: The `SIGNING_KEY_ID` must be the **short 8-character** format (e.g., `CD6AB534`), not the full 16-character ID.

3. **Wrong password**: Verify `SIGNING_PASSWORD` matches the passphrase you set when creating the GPG key.

### Debugging Key Format

The release workflow includes debug output showing:
- Key length
- Whether it contains PGP headers
- Whether it contains newlines

Check the "Debug signing key format" step in the workflow logs if signing fails.

### "Resource not accessible by integration"

The workflow needs `contents: write` permission to create GitHub releases. This is already configured in `.github/workflows/release.yml`:

```yaml
permissions:
  contents: write
```

### GPG Keyserver Connection Issues

If you can't connect to a keyserver:

```bash
# Try alternative keyservers
gpg --keyserver hkps://keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver hkps://keys.openpgp.org --send-keys YOUR_KEY_ID
gpg --keyserver hkps://pgp.mit.edu --send-keys YOUR_KEY_ID
```

## Build Configuration

This project uses the [vanniktech/gradle-maven-publish-plugin](https://github.com/vanniktech/gradle-maven-publish-plugin) for Maven Central publishing. Key configuration in `build.gradle.kts`:

```kotlin
mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(group.toString(), "compose-ui-test-server", version.toString())

    pom {
        // POM metadata...
    }
}
```

The plugin handles:
- Artifact signing with GPG
- Publishing to Maven Central via the Central Portal API
- Automatic staging and release
