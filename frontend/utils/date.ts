const DAY_MILLISECONDS = 1000 * 60 * 60 * 24

export const toDdayText = (date?: string | null) => {
  if (!date) return '일정 미등록'
  const today = new Date()
  const target = new Date(date)
  const diff = Math.ceil((target.setHours(0, 0, 0, 0) - today.setHours(0, 0, 0, 0)) / DAY_MILLISECONDS)
  if (diff === 0) return 'D-Day'
  return diff > 0 ? `D-${diff}` : `D+${Math.abs(diff)}`
}
