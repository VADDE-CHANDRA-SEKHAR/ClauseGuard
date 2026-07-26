# Developing ClauseGuard in VS Code

## 1. Open the workspace

Open the `clauseguard` folder itself (not `backend` or `frontend` individually) — the
`.vscode/` config assumes both live under one workspace root.

```bash
code clauseguard
```

VS Code will prompt to install the recommended extensions (`.vscode/extensions.json`):
Java Extension Pack, Spring Boot Extension Pack, Spring Boot Dashboard, ESLint, Prettier.
Accept the prompt.

## 2. Set your Groq API key

Backend reads `GROQ_API_KEY` from the environment. Easiest options:

- **Per-session (terminal):** `export GROQ_API_KEY=your_key_here` before launching from
  the integrated terminal.
- **Persistent for debugging:** the key is already wired into `.vscode/launch.json` via
  `${env:GROQ_API_KEY}` — just make sure it's set in your shell profile
  (`~/.zshrc` / `~/.bashrc`) so VS Code inherits it on launch.

## 3. Run everything with one command

Open the Command Palette (`Cmd/Ctrl+Shift+P`) → **Tasks: Run Task** → **Run full stack
(backend + frontend)**. This runs both `mvn spring-boot:run` and `npm run dev` in
parallel dedicated terminal panels (`.vscode/tasks.json`).

Or run them individually the same way: **Backend: mvn spring-boot:run** /
**Frontend: npm run dev**.

First time only, also run **Frontend: npm install** once before starting the dev server.

## 4. Debugging

- **Backend breakpoints:** set a breakpoint in any Java file, then Run and Debug panel
  (`Cmd/Ctrl+Shift+D`) → **Debug ClauseGuard backend**. Requires the Java Extension Pack.
- **Frontend breakpoints:** set a breakpoint in a `.jsx` file, start the frontend dev
  server (task above), then Run and Debug → **Debug ClauseGuard frontend (Chrome)**.
  This attaches a debuggable Chrome instance to `localhost:5173`.
- **Both at once:** Run and Debug → **Debug full stack** (a compound launch config that
  starts both debuggers together).

## 5. Spring Boot Dashboard

The Spring Boot Dashboard extension adds a sidebar icon showing the `clauseguard-backend`
app — start/stop/restart it there instead of the terminal if you prefer a GUI, and it
surfaces the actuator/health state once running.

## 6. Recommended workflow for the assessment

1. `Tasks: Run Task` → **Run full stack** to confirm everything works locally.
2. Paste `sample-data/sample-contract.txt` into the "Paste text" tab in the UI and
   confirm a risk-flagged case file appears — that's your working demo.
3. Commit and push (VS Code's built-in Source Control panel, or the terminal).
4. Follow `DEPLOYMENT.md` to ship it, using the integrated terminal for the Vercel CLI /
   Render setup steps.
