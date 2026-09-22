import { useEffect, useState } from 'react'
import { ConsumerActivityPanel } from './components/ConsumerActivityPanel'
import { LoginPanel } from './components/LoginPanel'
import { MyItemsPanel } from './components/MyItemsPanel'
import { NewItemPanel } from './components/NewItemPanel'
import { SuspendUserPanel } from './components/SuspendUserPanel'
import { clearToken, getToken } from './services/authStore'
import './App.css'

export default function App() {
  const [refreshKey, setRefreshKey] = useState(0)
  const [token, setTokenState] = useState<string | null>(getToken())

  useEffect(() => {
    const onAuthChanged = () => setTokenState(getToken())
    window.addEventListener('auth:changed', onAuthChanged)
    return () => window.removeEventListener('auth:changed', onAuthChanged)
  }, [])

  if (!token) {
    return <LoginPanel />
  }

  return (
    <main className="app">
      <header className="app-header">
        <div>
          <p className="eyebrow">Admin · synchronous</p>
          <h1>Admin</h1>
        </div>
        <button onClick={() => clearToken()}>Sign out</button>
      </header>
      <div className="panel-grid">
        <NewItemPanel onCreated={() => setRefreshKey((k) => k + 1)} />
        <MyItemsPanel refreshKey={refreshKey} />
        <SuspendUserPanel />
        <ConsumerActivityPanel />
      </div>
    </main>
  )
}
