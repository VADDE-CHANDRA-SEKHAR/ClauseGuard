# ClauseGuard

**AI-powered contract renewal & risk radar.** Upload a vendor contract (PDF or pasted
text) and ClauseGuard uses an LLM to extract the terms that actually cost companies
money if missed: renewal dates, auto-renewal clauses, and cancellation notice windows —
then flags each contract's risk level so nothing silently auto-renews again.

## Business value

Auto-renewal clauses with short, easy-to-miss notice windows are a well-documented
source of wasted SaaS/vendor spend — companies routinely get locked into another
contract term because no one tracked a 30-day cancellation window buried on page 4 of
a PDF. ClauseGuard turns that into a solved problem: drop in a contract, get a plain-English
risk summary and a countdown to the deadline that matters, instead of paying someone to
re-read every vendor agreement by hand.

## How it works

1. **Upload** a contract (PDF or `.txt`) or paste its text directly.
2. The backend extracts raw text (via Apache PDFBox for PDFs) and sends it to an LLM
   (Groq / Llama 3.3 70B) with a strict JSON extraction prompt.
3. The model returns structured fields: vendor, effective date, renewal date,
   cancellation deadline, auto-renew flag, notice period, a risk level (LOW → CRITICAL),
   and a short human-readable risk summary.
4. The dashboard shows every tracked contract as a card with a live countdown to its
   cancellation deadline, color-coded by urgency, plus aggregate stats (how many
   contracts are critical/high risk, how many deadlines fall in the next 45 days).

## Architecture

```
clauseguard/
├── backend/     Spring Boot 3 (Java 17) REST API — H2 in-memory DB, PDFBox text
│                extraction, Groq LLM client, clause-extraction service
├── frontend/    React 18 + Vite dashboard — upload UI, risk-coded contract cards
└── .github/workflows/ci-cd.yml   CI (build+test both) + CD (deploy frontend to Vercel)
```

**Stack:** Java 17, Spring Boot 3, Spring Data JPA, H2, Apache PDFBox, React 18, Vite,
Groq API (Llama 3.3 70B).

## Running locally

### Backend
```bash
cd backend
export GROQ_API_KEY=your_key_here
mvn spring-boot:run
# API on http://localhost:8080
```

### Frontend
```bash
cd frontend
npm install
npm run dev
# App on http://localhost:5173, talking to the backend on :8080
```

## API

| Method | Endpoint                          | Description                                  |
|--------|------------------------------------|-----------------------------------------------|
| POST   | `/api/contracts/upload`            | multipart file upload (PDF/TXT) → analyzed contract |
| POST   | `/api/contracts/text`              | JSON `{title, vendorName, rawText}` → analyzed contract |
| GET    | `/api/contracts`                   | list all contracts, soonest deadline first    |
| GET    | `/api/contracts/{id}`              | fetch one contract                            |
| GET    | `/api/contracts/alerts?withinDays=45` | contracts with a deadline in the next N days |
| DELETE | `/api/contracts/{id}`              | remove a contract                             |

## Design

The UI leans into the subject instead of using a generic dashboard template: it's built
as a "case-file registry" — contracts become case files with docket numbers
(`CG-2026-0001`), risk levels render as rotated ink stamps, and the palette is aged
paper and navy ink rather than a stock dark-mode theme. Typography pairs a typewriter
display face (`Special Elite`) with an editorial serif body (`Newsreader`) and a mono
face (`IBM Plex Mono`) for docket data — see `frontend/src/index.css` for the full
token system.

## Developing in VS Code

See [`VSCODE.md`](./VSCODE.md) — one-command full-stack run, backend/frontend
debugging, and the recommended extension set.

## Deployment

See [`DEPLOYMENT.md`](./DEPLOYMENT.md) for step-by-step Vercel (frontend) + Render
(backend) deployment instructions and the CI/CD pipeline explanation.

## Notes on the AI-assisted build process

This project's CI/CD pipeline (`.github/workflows/ci-cd.yml`) and this documentation
were drafted with AI assistance and then reviewed/adjusted by hand, per the assessment
instructions. The core application logic (entity design, extraction prompt, API
contract, UI) was designed to solve a real, specific business problem rather than
wrap a generic CRUD demo in an AI feature.
