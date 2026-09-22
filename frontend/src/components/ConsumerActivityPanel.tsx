import { useEffect, useState } from 'react'
import type { ConsumerActivityRow } from '../models/item'
import { RecordApiConnector } from '../services/RecordApiConnector'

export function ConsumerActivityPanel() {
  const [rows, setRows] = useState<ConsumerActivityRow[]>([])
  const [error, setError] = useState<string | null>(null)

  function refresh() {
    RecordApiConnector.consumerActivity()
      .then(setRows)
      .catch((err) => setError(err instanceof Error ? err.message : String(err)))
  }

  useEffect(refresh, [])

  return (
    <section className="panel">
      <h2>Consumer Activity</h2>
      <p className="panel-hint">
        Folded from consumer.events at Admin's own pace — this is eventually consistent, not live.
      </p>
      <button onClick={refresh}>Refresh</button>
      {error && <p className="panel-error">{error}</p>}
      <ul className="item-list">
        {rows.map((row) => (
          <li key={row.id}>
            <b>{row.status}</b> {row.kind} for {row.userId}
            {row.detail && <span className="muted"> — {row.detail}</span>}
          </li>
        ))}
        {rows.length === 0 && <li className="muted">No activity folded in yet.</li>}
      </ul>
    </section>
  )
}
