import type {
  DashboardSummary,
  Defect,
  Project,
  ProjectCreatePayload,
  Requirement,
  RequirementCreatePayload,
  ScheduleUpdatePayload,
  TestCase,
  TestCaseCreatePayload,
  TestResult,
  TestStage,
  WbsTask
} from '~/types/pms'

export const usePmsService = () => {
  const { request } = useApi()

  return {
    summary: () => request<DashboardSummary>('/dashboard/summary'),

    projects: (keyword = '') => request<Project[]>('/projects', {
      query: keyword ? { keyword } : undefined
    }),

    createProject: (payload: ProjectCreatePayload) => request<Project>('/projects', {
      method: 'POST',
      body: payload
    }),

    requirements: (projectId: number) => request<Requirement[]>(`/projects/${projectId}/requirements`),

    createRequirement: (projectId: number, payload: RequirementCreatePayload) =>
      request<Requirement>(`/projects/${projectId}/requirements`, {
        method: 'POST',
        body: payload
      }),

    confirmRequirement: (requirementId: number) =>
      request<Requirement>(`/requirements/${requirementId}/confirm`, {
        method: 'PATCH'
      }),

    wbs: (projectId: number) => request<WbsTask[]>(`/projects/${projectId}/wbs-tasks`),

    myWbs: (assignee: string) => request<WbsTask[]>('/my-work/wbs-tasks', {
      query: { assignee }
    }),

    updateSchedule: (taskId: number, payload: ScheduleUpdatePayload) =>
      request<WbsTask>(`/wbs-tasks/${taskId}/schedule`, {
        method: 'PATCH',
        body: payload
      }),

    startTask: (taskId: number) =>
      request<WbsTask>(`/wbs-tasks/${taskId}/start`, {
        method: 'PATCH'
      }),

    completeTask: (taskId: number) =>
      request<WbsTask>(`/wbs-tasks/${taskId}/complete`, {
        method: 'PATCH'
      }),

    tests: (projectId: number, stage?: TestStage) =>
      request<TestCase[]>(`/projects/${projectId}/test-cases`, {
        query: stage ? { stage } : undefined
      }),

    createTest: (projectId: number, payload: TestCaseCreatePayload) =>
      request<TestCase>(`/projects/${projectId}/test-cases`, {
        method: 'POST',
        body: payload
      }),

    executeTest: (testCaseId: number, payload: { result: TestResult; actualResult: string; tester: string }) =>
      request<TestCase>(`/test-cases/${testCaseId}/execute`, {
        method: 'PATCH',
        body: payload
      }),

    defects: (projectId: number) => request<Defect[]>(`/projects/${projectId}/defects`)
  }
}
