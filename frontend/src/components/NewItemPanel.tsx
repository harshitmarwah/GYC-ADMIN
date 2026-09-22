import { useState } from 'react'
import { RecordApiConnector } from '../services/RecordApiConnector'

export function NewItemPanel({ onCreated }: { onCreated: () => void }) {
  const [title, setTitle] = useState('')
  const [body, setBody] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    setSubmitting(true)
    setError(null)
    try {
      await RecordApiConnector.createRecord(title, body)
      setTitle('')
      setBody('')
      onCreated()
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="panel">
      <h2>New Item</h2>
      <p className="panel-hint">Writes synchronously to Admin's own database, then publishes RecordUpdated.</p>
      <form onSubmit={handleSubmit}>
        <input placeholder="Title" value={title} onChange={(e) => setTitle(e.target.value)} required />
        <textarea placeholder="Body" value={body} onChange={(e) => setBody(e.target.value)} required />
        <button type="submit" disabled={submitting}>
          {submitting ? 'Submitting…' : 'Submit'}
        </button>
      </form>
      {error && <p className="panel-error">{error}</p>}
    </section>
  )
}
