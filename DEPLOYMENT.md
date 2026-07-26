# Deployment Guide

ClauseGuard is a two-part deploy: a static React frontend (Vercel) and a Spring Boot
backend (needs a real server/container host — Vercel does not run long-lived JVM
processes, so the backend goes to Render or Railway, both of which have free tiers).

## 1. Push to GitHub

```bash
cd clauseguard
git init
git add .
git commit -m "Initial ClauseGuard build"
git branch -M main
git remote add origin https://github.com/<your-username>/clauseguard.git
git push -u origin main
```

## 2. Deploy the backend (Render)

1. Go to render.com → New → Web Service → connect the `clauseguard` repo.
2. Root directory: `backend`
3. Build command: `mvn clean package -DskipTests`
4. Start command: `java -jar target/clauseguard-backend-1.0.0.jar`
5. Add environment variable `GROQ_API_KEY` = your Groq key.
6. Render auto-assigns a `PORT` env var — `application.properties` already reads
   `${PORT:8080}`, so no code change needed.
7. Deploy. Note the resulting URL, e.g. `https://clauseguard-backend.onrender.com`.

(Railway works the same way if you prefer it — same build/start commands.)

## 3. Deploy the frontend (Vercel)

### Option A — Vercel dashboard (fastest for a one-off deploy)
1. Import the `clauseguard` repo in Vercel, set **Root Directory** to `frontend`.
2. Add environment variable `VITE_API_BASE_URL` = your Render backend URL from step 2.
3. Deploy — Vercel auto-detects the Vite framework from `frontend/vercel.json`.

### Option B — via the CI/CD pipeline (what `.github/workflows/ci-cd.yml` automates)
The included GitHub Actions workflow builds and tests both apps on every push, and on
pushes to `main` it also deploys the frontend to Vercel automatically. To enable the
deploy job, add these repo secrets (GitHub repo → Settings → Secrets and variables →
Actions):

- `VERCEL_TOKEN` — from Vercel → Account Settings → Tokens
- `VITE_API_BASE_URL` — your deployed backend URL

Once those secrets exist, every push to `main` that passes the build/test jobs will
automatically redeploy the frontend — that's the CI/CD loop end to end.

## 4. Verify

1. Open the Vercel URL.
2. Paste a short sample contract (or upload a PDF) and confirm a risk-flagged card
   appears with a cancellation countdown.
3. Confirm `GET https://<render-url>/api/contracts` returns the stored contract.

## Notes

- The backend uses an in-memory H2 database by default so there's zero external DB
  setup for the assessment/demo. Data resets on backend restart — swap the 4
  `spring.datasource.*` lines in `application.properties` for a real MySQL/Postgres
  instance if persistence across restarts is needed.
- CORS is open (`@CrossOrigin(origins = "*")`) for demo simplicity — lock this to the
  actual Vercel domain before treating this as production-ready.
