import { useState, useEffect, useCallback } from 'react'
import './index.css'

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const STAMP_LABEL = {
  CRITICAL: 'Critical',
  HIGH: 'High Risk',
  MEDIUM: 'Medium',
  LOW: 'Cleared',
  UNKNOWN: 'Unread',
}

function stampClass(level) {
  return `ink-stamp stamp-${(level || 'unknown').toLowerCase()}`
}

function docketNumber(contract) {
  const year = contract.uploadedAt ? contract.uploadedAt.slice(0, 4) : new Date().getFullYear()
  return `CG-${year}-${String(contract.id).padStart(4, '0')}`
}

function daysUntil(dateStr) {
  if (!dateStr) return null
  const target = new Date(dateStr)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return Math.ceil((target - today) / (1000 * 60 * 60 * 24))
}

function CountdownText({ deadline }) {
  const days = daysUntil(deadline)
  if (days === null) return <span className="ledger-value countdown-past">not on file</span>
  if (days < 0) return <span className="ledger-value countdown-past">lapsed {Math.abs(days)}d ago</span>
  let cls = 'countdown-ok'
  if (days <= 14) cls = 'countdown-urgent'
  else if (days <= 45) cls = 'countdown-soon'
  return <span className={`ledger-value ${cls}`}>{days}d remaining</span>
}

function IntakePanel({ onAnalyzed }) {
  const [mode, setMode] = useState('file')
  const [file, setFile] = useState(null)
  const [rawText, setRawText] = useState('')
  const [title, setTitle] = useState('')
  const [vendorName, setVendorName] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const submit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      let res
      if (mode === 'file' && file) {
        const formData = new FormData()
        formData.append('file', file)
        if (vendorName) formData.append('vendorName', vendorName)
        if (title) formData.append('title', title)
        res = await fetch(`${API_BASE}/api/contracts/upload`, { method: 'POST', body: formData })
      } else {
        res = await fetch(`${API_BASE}/api/contracts/text`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ title: title || 'Untitled contract', vendorName, rawText }),
        })
      }
      if (!res.ok) throw new Error(`Registry rejected the submission (${res.status})`)
      const contract = await res.json()
      onAnalyzed(contract)
      setFile(null); setRawText(''); setTitle(''); setVendorName('')
    } catch (err) {
      setError(err.message || 'Could not process this file.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="intake-panel">
      <h2 className="intake-heading">New Intake</h2>
      <p className="intake-caption">Attach a contract for review. The registrar reads it and opens a case file below.</p>

      <div className="mode-toggle">
        <button type="button" className={`mode-btn ${mode === 'file' ? 'active' : ''}`} onClick={() => setMode('file')}>
          Attach file
        </button>
        <button type="button" className={`mode-btn ${mode === 'paste' ? 'active' : ''}`} onClick={() => setMode('paste')}>
          Paste text
        </button>
      </div>

      <form className="intake-form" onSubmit={submit}>
        <div className="intake-row">
          <div style={{ flex: 1, minWidth: 200 }}>
            <label className="field-label">Case title</label>
            <input className="docket-input" placeholder="e.g. Northwind MSA" value={title} onChange={e => setTitle(e.target.value)} />
          </div>
          <div style={{ flex: 1, minWidth: 200 }}>
            <label className="field-label">Counterparty</label>
            <input className="docket-input" placeholder="Vendor / other party" value={vendorName} onChange={e => setVendorName(e.target.value)} />
          </div>
        </div>

        {mode === 'file' ? (
          <div className="evidence-drop">
            <input type="file" accept=".pdf,.txt" onChange={e => setFile(e.target.files[0])} />
          </div>
        ) : (
          <div>
            <label className="field-label">Contract text</label>
            <textarea className="docket-textarea" rows={6} placeholder="Paste the contract body here..." value={rawText} onChange={e => setRawText(e.target.value)} />
          </div>
        )}

        {error && <div className="form-error">⚠ {error}</div>}

        <button type="submit" className="submit-stamp" disabled={loading || (mode === 'file' ? !file : !rawText.trim())}>
          {loading ? 'Reading contract…' : 'Open case file'}
        </button>
      </form>
    </section>
  )
}

function CaseFile({ contract, onDismiss }) {
  return (
    <article className="case-file">
      <span className={stampClass(contract.riskLevel)}>{STAMP_LABEL[contract.riskLevel] || 'Unread'}</span>

      <div className="case-docket-no">{docketNumber(contract)}</div>
      <h3 className="case-title">{contract.contractTitle}</h3>
      <div className="case-vendor">{contract.vendorName}</div>

      <div className="ledger">
        <div className="ledger-field">
          <span className="field-label">Cancel by</span>
          <CountdownText deadline={contract.cancellationDeadline} />
        </div>
        <div className="ledger-field">
          <span className="field-label">Auto-renews</span>
          <span className="ledger-value">{contract.autoRenews === null ? 'unknown' : contract.autoRenews ? 'yes' : 'no'}</span>
        </div>
        <div className="ledger-field">
          <span className="field-label">Notice period</span>
          <span className="ledger-value">{contract.noticePeriodDays != null ? `${contract.noticePeriodDays} days` : '—'}</span>
        </div>
        <div className="ledger-field">
          <span className="field-label">Renews on</span>
          <span className="ledger-value">{contract.renewalDate || '—'}</span>
        </div>
      </div>

      {contract.riskSummary && <p className="risk-note">{contract.riskSummary}</p>}

      {contract.keyClausesJson && (
        <div className="clause-list">
          {contract.keyClausesJson.split('|').map((c, i) => (
            <div key={i}>· {c.trim()}</div>
          ))}
        </div>
      )}

      <button className="dismiss-btn" onClick={() => onDismiss(contract.id)}>Close case</button>
    </article>
  )
}

export default function App() {
  const [contracts, setContracts] = useState([])
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState(null)

  const loadContracts = useCallback(async () => {
    setLoading(true)
    setLoadError(null)
    try {
      const res = await fetch(`${API_BASE}/api/contracts`)
      if (!res.ok) throw new Error(`Server responded ${res.status}`)
      setContracts(await res.json())
    } catch {
      setLoadError('Registrar unreachable — confirm the backend is running.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { loadContracts() }, [loadContracts])

  const handleAnalyzed = (contract) => setContracts(prev => [contract, ...prev])
  const handleDismiss = async (id) => {
    await fetch(`${API_BASE}/api/contracts/${id}`, { method: 'DELETE' })
    setContracts(prev => prev.filter(c => c.id !== id))
  }

  const criticalCount = contracts.filter(c => c.riskLevel === 'CRITICAL').length
  const highCount = contracts.filter(c => c.riskLevel === 'HIGH').length
  const upcoming45 = contracts.filter(c => {
    const d = daysUntil(c.cancellationDeadline)
    return d !== null && d >= 0 && d <= 45
  }).length

  return (
    <div className="docket-board">
      <div className="docket-inner">
        <header className="docket-header">
          <div className="docket-tab">Case Registry</div>
          <h1 className="docket-title">ClauseGuard</h1>
          <p className="docket-subtitle">Renewal &amp; Cancellation Docket — AI Registrar on Duty</p>
        </header>

        <div className="stat-strip">
          <div className="stat-cell stat-critical">
            <div className="stat-value">{criticalCount}</div>
            <div className="stat-label">Critical cases</div>
          </div>
          <div className="stat-cell stat-high">
            <div className="stat-value">{highCount}</div>
            <div className="stat-label">High risk cases</div>
          </div>
          <div className="stat-cell stat-info">
            <div className="stat-value">{upcoming45}</div>
            <div className="stat-label">Due within 45 days</div>
          </div>
        </div>

        <IntakePanel onAnalyzed={handleAnalyzed} />

        <h2 className="section-heading">Open Case Files</h2>

        {loading && <p className="state-msg">Pulling the registry…</p>}
        {loadError && <p className="state-msg error">{loadError}</p>}
        {!loading && !loadError && contracts.length === 0 && (
          <p className="state-msg">No cases on file yet. Submit an intake above to open one.</p>
        )}

        <div className="case-grid">
          {contracts.map(c => (
            <CaseFile key={c.id} contract={c} onDismiss={handleDismiss} />
          ))}
        </div>
      </div>
    </div>
  )
}
