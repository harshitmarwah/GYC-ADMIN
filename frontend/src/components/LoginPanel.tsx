import { useState } from 'react'
import { setToken } from '../services/authStore'

export function LoginPanel() {
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    setSubmitting(true)
    setError(null)
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      })
      if (!response.ok) {
        throw new Error('Invalid credentials')
      }
      const body = (await response.json()) as { token: string }
      setToken(body.token)
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="app">
      <header>
        <p className="eyebrow">Admin · synchronous</p>
        <h1>Admin</h1>
      </header>
      <section className="panel login-panel">
        <h2>Sign in</h2>
        <p className="panel-hint">Seeded user: admin / admin123</p>
        <form onSubmit={handleSubmit}>
          <input placeholder="Username" value={username} onChange={(e) => setUsername(e.target.value)} required />
          <input
            placeholder="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button type="submit" disabled={submitting}>
            {submitting ? 'Signing in…' : 'Sign in'}
          </button>
        </form>
        {error && <p className="panel-error">{error}</p>}
      </section>
    </main>
  )
}
