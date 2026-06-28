import type { DashboardSummary, Project, ProjectStatus } from '~/types/pms'

export type ProjectCreatePayload = {
  projectCode: string
  name: string
  department: string
  requester: string
  status: ProjectStatus
  openDueDate: string | null
  description: string
}

export const useProjectService = () => {
  const { request } = useApi()

  const getDashboardSummary = () => request<DashboardSummary>('/dashboard/summary')

  const searchProjects = (params: { keyword?: string; status?: string } = {}) =>
    request<Project[]>('/projects', {
      query: Object.fromEntries(Object.entries(params).filter(([, value]) => value))
    })

  const getProject = (projectId: number) => request<Project>(`/projects/${projectId}`)

  const createProject = (payload: ProjectCreatePayload) =>
    request<Project>('/projects', {
      method: 'POST',
      body: payload
    })

  return { getDashboardSummary, searchProjects, getProject, createProject }
}
