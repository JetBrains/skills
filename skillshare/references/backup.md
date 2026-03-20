# Backup & Restore

**Global mode only.** Not available in project mode (project targets are reproducible via `install -p && sync`).

## backup

Create backups of target skill directories.

```bash
skillshare backup                # All targets
skillshare backup claude         # Specific target
skillshare backup --list         # List existing backups
skillshare backup --cleanup      # Remove old backups
```

**Location:** `~/.config/skillshare/backups/<timestamp>/`

## restore

Restore target from backup.

```bash
skillshare restore claude                            # Latest backup
skillshare restore claude --from 2026-01-14_21-22   # Specific backup
```

## Best Practices

- Run `backup` before major changes
- Use `--dry-run` with restore to preview
- Keep backups with `--cleanup` to save disk space
