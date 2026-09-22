// The only place in this app that calls fetch. Panels go through here so
// view code never talks to the network directly.
import type { ConsumerActivityRow, Item } from '../models/item'
import { clearToken, getToken } from './authStore'

function authHeaders(extra?: Record<string, string>): Record<string, string> {
  const token = getToken()
  return { ...(extra ?? {}), ...(token ? { Authorization: `Bearer ${token}` } : {}) }
}

async function ensureOk(response: Response): Promise<Response> {
  if (response.status === 401) {
    clearToken() // token missing/expired/rejected — drop back to the login screen
  }
  if (!response.ok) {
    throw new Error(`${response.status} ${response.statusText}`)
  }
  return response
}

async function asJson<T>(response: Response): Promise<T> {
  return (await ensureOk(response)).json() as Promise<T>
}

export const RecordApiConnector = {
  async listRecords(): Promise<Item[]> {
    return asJson<Item[]>(await fetch('/api/records', { headers: authHeaders() }))
  },

  async createRecord(title: string, body: string): Promise<Item> {
    return asJson<Item>(
      await fetch('/api/records', {
        method: 'POST',
        headers: authHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify({ title, body }),
      }),
    )
  },

  async suspendUser(userId: string, suspended: boolean, reason: string): Promise<void> {
    await ensureOk(
      await fetch(`/api/users/${encodeURIComponent(userId)}/suspend`, {
        method: 'POST',
        headers: authHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify({ suspended, reason }),
      }),
    )
  },

  async updateConfig(key: string, value: string): Promise<void> {
    await ensureOk(
      await fetch('/api/config', {
        method: 'PUT',
        headers: authHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify({ key, value }),
      }),
    )
  },

  async consumerActivity(): Promise<ConsumerActivityRow[]> {
    return asJson<ConsumerActivityRow[]>(await fetch('/api/dashboard/consumer-activity', { headers: authHeaders() }))
  },
}
