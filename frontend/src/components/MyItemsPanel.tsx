import { useEffect, useState } from 'react'
import type { Item } from '../models/item'
import { RecordApiConnector } from '../services/RecordApiConnector'

export function MyItemsPanel({ refreshKey }: { refreshKey: number }) {
  const [items, setItems] = useState<Item[]>([])
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    RecordApiConnector.listRecords()
      .then(setItems)
      .catch((err) => setError(err instanceof Error ? err.message : String(err)))
  }, [refreshKey])

  return (
    <section className="panel">
      <h2>My Items</h2>
      <p className="panel-hint">Admin's own records — GET /api/records, read straight from SQLite.</p>
      {error && <p className="panel-error">{error}</p>}
      <ul className="item-list">
        {items.map((item) => (
          <li key={item.id}>
            <b>{item.title}</b> <span className="muted">rev {item.revision}</span>
            <div className="muted">{item.body}</div>
          </li>
        ))}
        {items.length === 0 && <li className="muted">No items yet.</li>}
      </ul>
    </section>
  )
}
