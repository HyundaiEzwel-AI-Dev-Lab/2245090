import type { ScheduleUpdatePayload, WbsTask } from '~/types/pms'

export const useWbsService = () => {
  const { request } = useApi()

  const listWbsTasks = (projectId: number) => request<WbsTask[]>(`/projects/${projectId}/wbs-tasks`)

  const listMyTasks = (assignee = '이지윤') =>
    request<WbsTask[]>('/my-work/wbs-tasks', {
      query: { assignee }
    })

  const updateSchedule = (taskId: number, payload: ScheduleUpdatePayload) =>
    request<WbsTask>(`/wbs-tasks/${taskId}/schedule`, {
      method: 'PATCH',
      body: payload
    })

  const startTask = (taskId: number) =>
    request<WbsTask>(`/wbs-tasks/${taskId}/start`, { method: 'PATCH' })

  const completeTask = (taskId: number) =>
    request<WbsTask>(`/wbs-tasks/${taskId}/complete`, { method: 'PATCH' })

  return { listWbsTasks, listMyTasks, updateSchedule, startTask, completeTask }
}
