# pizza-process

Axon Ivy 14 pizza ordering process (customer → clerk/chef → delivery) with the Axon Ivy Portal.

## Run everything on Docker

Requirements: Docker Desktop (or Docker Engine + compose plugin). Nothing else, Maven and Java run inside Docker.

```bash
# Linux / macOS / Git Bash / WSL
scripts/start.sh

# Windows PowerShell
.\scripts\start.ps1
```

Or in VS Code: **Terminal → Run Task → "Docker: deploy & open Portal"**.

The script builds the project, starts all containers, waits until the Portal answers and opens it in your browser.
The first build takes a while (Maven downloads); later builds are faster.

| URL | What |
|---|---|
| http://localhost/ | Portal (redirects to `/pizza/`) |
| http://localhost:8080/system | Engine Cockpit, login `admin` / `admin` |
| localhost:5433 | Postgres (`admin` / `1234`, db `pizza-process-db`) |

Users (from `main/config/users.yaml`, password `1234`): `Mustafa`, `Tamara` (customers), `Ayesha` (clerk),
`Annesa` (chef), `Sophia` (clerk + chef), `Diana` / `Marco` / `Elena` (delivery), `Admin`.

Stop: `docker compose down` · Reset all data: `docker compose down -v`

## Develop in VS Code

Start only the database: `docker compose -f main/docker-compose.yaml up -d` and run the project in the designer as before.

More details (how the deployment works, licence, troubleshooting): [docs/setup.md](docs/setup.md)
