import type { Requirement } from '~/types/pms'

export type RequirementCreatePayload = {
  title: string
  systemType: string
  businessType: string
  menuName: string
  description: string
}

export const useRequirementService = () => {
  const { request } = useApi()

  const listRequirements = (projectId: number) => request<Requirement[]>(`/projects/${projectId}/requirements`)

  const createRequirement = (projectId: number, payload: RequirementCreatePayload) =>
    request<Requirement>(`/projects/${projectId}/requirements`, {
      method: 'POST',
      body: payload
    })

  const confirmRequirement = (requirementId: number) =>
    request<Requirement>(`/requirements/${requirementId}/confirm`, {
      method: 'PATCH'
    })

  return { listRequirements, createRequirement, confirmRequirement }
}
