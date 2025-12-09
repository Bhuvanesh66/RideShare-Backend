# RideShare-Backend — Project Summary

Short summary:

- **Name:** RideShare-Backend
- **Owner:** Bhuvanesh66
- **Description:** A Spring Boot backend for a simple ride-sharing application that supports user/driver registration, JWT-based authentication, ride creation, accepting and completing rides, and basic role-based access.

Status & tests:

- The project builds with Maven and runs on Java 17.
- Unit tests in `src/test` pass locally (contextLoads etc.).
- The included PowerShell end-to-end script `tests/api-tests.ps1` has been fixed and validated: it reports passing runs (registration, login, create/accept/complete flows).

How to run locally:

1. Ensure MongoDB is running and reachable (default URI in `src/main/resources/application.properties` is `mongodb://localhost:27017/rideshare_db`).
2. Build and run with the Maven wrapper from the repository root or `rideshare` folder:

```powershell
Set-Location -Path "./rideshare"
.\mvnw clean package
.\mvnw spring-boot:run
```

3. By default the API runs on `http://localhost:8081` (confirm `application.properties`).

Tests:

- Unit tests: `.\mvnw test` (JUnit tests included).
- End-to-end API script (PowerShell): `tests\api-tests.ps1` — use PowerShell 5.1+ to run.

Notes & recommendations:

- `JwtUtil` uses JJWT APIs that may show deprecation warnings; consider upgrading usage to the latest JJWT builder patterns in future maintenance.
- The `tests/api-tests.ps1` was modified to avoid PowerShell splat/color issues and to generate unique usernames for idempotent runs.
- Keep `.github` and `.mvn` directories in the repository (do not add them to `.gitignore`) — they contain CI workflows and the Maven wrapper which are generally committed.

Contact / next steps:

If you want, I can:

- Open a PR instead of pushing directly.
- Update `JwtUtil` to remove deprecation warnings.
- Add a small `Makefile` or PowerShell helper to run tests and start the app.

Generated on: 2025-12-09
