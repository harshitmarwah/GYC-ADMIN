import { useState } from 'react'
import { RecordApiConnector } from '../services/RecordApiConnector'

/**
 * Exercises the Admin -> Consumer direction of the bridge on a live user:
 * suspending here publishes UserSuspended, which Consumer folds into its
 * own mirror and checks the next time that user submits a request.
 */
export function SuspendUserPanel() {
  const [userId, setUserId] = useState('')
  const [reason, setReason] = useState('')
  const [status, setStatus] = useState<string | null>(null)

  async function setSuspended(suspended: boolean) {
    setStatus(null)
    try {
      await RecordApiConnector.suspendUser(userId, suspended, reason)
      setStatus(`userId=${userId} suspended=${suspended}`)
    } catch (err) {
      setStatus(err instanceof Error ? err.message : String(err))
    }
  }

  return (
    <section className="panel">
      <h2>Suspend User</h2>
      <p className="panel-hint">Admin owns no user table — this only publishes UserSuspended for Consumer to apply.</p>
      <div className="row">
        <input placeholder="userId" value={userId} onChange={(e) => setUserId(e.target.value)} />
        <input placeholder="reason (optional)" value={reason} onChange={(e) => setReason(e.target.value)} />
        <button onClick={() => setSuspended(true)} disabled={!userId}>
          Suspend
        </button>
        <button onClick={() => setSuspended(false)} disabled={!userId}>
          Reinstate
        </button>
      </div>
      {status && <p className="muted">{status}</p>}
    </section>
  )
}
