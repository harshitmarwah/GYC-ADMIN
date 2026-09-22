// A plain module-level store, not React state: RecordApiConnector reads the
// token directly for every request, and components can't easily read
// something below them any other way without prop-drilling or a context.
// A window event lets App re-render when the token changes.
const TOKEN_KEY = 'admin.jwt'

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
  window.dispatchEvent(new Event('auth:changed'))
}

export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY)
  window.dispatchEvent(new Event('auth:changed'))
}
