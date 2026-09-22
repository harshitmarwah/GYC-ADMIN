import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import App from './App'

/**
 * Integration test: exercises App + LoginPanel + NewItemPanel + MyItemsPanel
 * + RecordApiConnector + authStore together, against a stubbed fetch —
 * proves the login gate and the create-then-see-it-listed flow work as a
 * whole, not just each unit in isolation.
 */
describe('App (integration)', () => {
  let records: Array<{ id: number; title: string; body: string; revision: number; updatedAt: string }>

  beforeEach(() => {
    localStorage.clear()
    records = []

    vi.stubGlobal(
      'fetch',
      vi.fn(async (url: string, options?: RequestInit) => {
        const method = options?.method ?? 'GET'

        if (url === '/api/auth/login' && method === 'POST') {
          return jsonResponse({ token: 'test-token', username: 'admin' })
        }
        if (url === '/api/records' && method === 'GET') {
          return jsonResponse(records)
        }
        if (url === '/api/records' && method === 'POST') {
          const body = JSON.parse(options!.body as string) as { title: string; body: string }
          const created = { id: records.length + 1, title: body.title, body: body.body, revision: 1, updatedAt: new Date().toISOString() }
          records.push(created)
          return jsonResponse(created, 201)
        }
        if (url === '/api/dashboard/consumer-activity' && method === 'GET') {
          return jsonResponse([])
        }
        throw new Error(`Unhandled request in test: ${method} ${url}`)
      }),
    )
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('shows the login screen first, then the panels, then a newly created item', async () => {
    const user = userEvent.setup()
    render(<App />)

    expect(screen.getByRole('heading', { name: 'Sign in' })).toBeInTheDocument()

    await user.type(screen.getByPlaceholderText('Password'), 'admin123')
    await user.click(screen.getByRole('button', { name: /sign in/i }))

    await waitFor(() => expect(screen.getByRole('heading', { name: 'New Item' })).toBeInTheDocument())

    await user.type(screen.getByPlaceholderText('Title'), 'Q3 plan')
    await user.type(screen.getByPlaceholderText('Body'), 'draft')
    await user.click(screen.getByRole('button', { name: /submit/i }))

    await waitFor(() => expect(screen.getByText('Q3 plan')).toBeInTheDocument())
  })
})

function jsonResponse(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } })
}
