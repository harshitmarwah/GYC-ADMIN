import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { RecordApiConnector } from './RecordApiConnector'
import { getToken, setToken } from './authStore'

function jsonResponse(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } })
}

describe('RecordApiConnector', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('attaches the Authorization header when a token is present', async () => {
    setToken('my-token')
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse([]))
    vi.stubGlobal('fetch', fetchMock)

    await RecordApiConnector.listRecords()

    const [, options] = fetchMock.mock.calls[0] as [string, RequestInit]
    expect((options.headers as Record<string, string>).Authorization).toBe('Bearer my-token')
  })

  it('omits the Authorization header when there is no token', async () => {
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse([]))
    vi.stubGlobal('fetch', fetchMock)

    await RecordApiConnector.listRecords()

    const [, options] = fetchMock.mock.calls[0] as [string, RequestInit]
    expect((options.headers as Record<string, string>).Authorization).toBeUndefined()
  })

  it('clears the token when the server responds 401', async () => {
    setToken('an-expired-token')
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(jsonResponse({}, 401)))

    await expect(RecordApiConnector.listRecords()).rejects.toThrow('401')
    expect(getToken()).toBeNull()
  })

  it('createRecord posts the title and body and returns the parsed record', async () => {
    setToken('my-token')
    const created = { id: 1, title: 'Q3 plan', body: 'draft', revision: 1, updatedAt: '2026-01-01T00:00:00Z' }
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse(created, 201))
    vi.stubGlobal('fetch', fetchMock)

    const result = await RecordApiConnector.createRecord('Q3 plan', 'draft')

    expect(result).toEqual(created)
    const [url, options] = fetchMock.mock.calls[0] as [string, RequestInit]
    expect(url).toBe('/api/records')
    expect(options.method).toBe('POST')
    expect(JSON.parse(options.body as string)).toEqual({ title: 'Q3 plan', body: 'draft' })
  })
})
