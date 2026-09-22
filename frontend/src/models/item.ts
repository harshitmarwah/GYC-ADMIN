// Wire shapes returned by the Admin backend. Kept separate from view code
// so panels never guess at response shape.

export interface Item {
  id: number
  title: string
  body: string
  revision: number
  updatedAt: string
}

export interface ConsumerActivityRow {
  id: number
  requestId: string
  userId: string
  kind: string
  status: 'SUBMITTED' | 'COMPLETED' | 'FLAGGED'
  detail: string
  recordedAt: string
}
