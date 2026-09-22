import { beforeEach, describe, expect, it, vi } from 'vitest'
import { clearToken, getToken, setToken } from './authStore'

describe('authStore', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('returns null when nothing is stored', () => {
    expect(getToken()).toBeNull()
  })

  it('round-trips a token through setToken/getToken', () => {
    setToken('abc.def.ghi')

    expect(getToken()).toBe('abc.def.ghi')
  })

  it('removes the token on clearToken', () => {
    setToken('abc.def.ghi')
    clearToken()

    expect(getToken()).toBeNull()
  })

  it('dispatches auth:changed on both set and clear', () => {
    const listener = vi.fn()
    window.addEventListener('auth:changed', listener)

    setToken('abc.def.ghi')
    clearToken()

    expect(listener).toHaveBeenCalledTimes(2)
    window.removeEventListener('auth:changed', listener)
  })
})
