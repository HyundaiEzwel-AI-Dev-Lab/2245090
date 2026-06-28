export const projectStatusOptions = [
  { label: '전체', value: '' },
  { label: '접수', value: 'RECEIVED' },
  { label: '협의중', value: 'DISCUSSING' },
  { label: '처리중', value: 'IN_PROGRESS' },
  { label: '테스트', value: 'TESTING' },
  { label: '완료', value: 'COMPLETED' },
  { label: '취소', value: 'CANCELED' }
] as const

export const getStatusVariant = (status: string) => {
  if (['COMPLETED', 'PASS', 'RESOLVED', 'CLOSED'].includes(status)) return 'success'
  if (['FAIL', 'OPEN', 'CANCELED'].includes(status)) return 'danger'
  if (['TESTING', 'DISCUSSING', 'IN_PROGRESS'].includes(status)) return 'warning'
  return 'default'
}
