export type ApiResponse<T> = {
  success?: boolean
  data?: T
  message?: string
}

export type ProjectStatus =
  | 'RECEIVED'
  | 'DISCUSSING'
  | 'IN_PROGRESS'
  | 'TESTING'
  | 'COMPLETED'
  | 'CANCELED'

export type RequirementStatus = 'DRAFT' | 'CONFIRMED' | 'REJECTED'
export type WbsStatus = 'READY' | 'IN_PROGRESS' | 'COMPLETED' | 'PAUSED'
export type WorkType =
  | 'PLANNING'
  | 'DESIGN'
  | 'PUBLISHING'
  | 'DEVELOPMENT'
  | 'UNIT_TEST'
  | 'DEV_TEST'
  | 'OPERATION_TEST'

export type TestStage = 'UNIT' | 'DEV' | 'OPERATION'
export type TestResult = 'NOT_RUN' | 'PASS' | 'FAIL'

export type Project = {
  id: number
  projectCode: string
  name: string
  department: string
  requester: string
  status: ProjectStatus
  statusLabel: string
  openDueDate: string | null
  progressRate: number
  description: string | null
}

export type Requirement = {
  id: number
  projectId: number
  title: string
  systemType: string
  businessType: string
  menuName: string
  description: string | null
  status: RequirementStatus
  statusLabel: string
}

export type WbsTask = {
  id: number
  projectId: number
  requirementId: number | null
  taskName: string
  workType: WorkType
  workTypeLabel: string
  assignee: string
  planStartDate: string | null
  planEndDate: string | null
  actualStartDate: string | null
  actualEndDate: string | null
  progressRate: number
  status: WbsStatus
  statusLabel: string
  delayed: boolean
  priority: string
  difficulty: string
  unitTestEnabled: boolean
  note: string | null
}

export type TestCase = {
  id: number
  projectId: number
  wbsTaskId: number | null
  stage: TestStage
  stageLabel: string
  caseName: string
  procedure: string
  expectedResult: string
  result: TestResult
  resultLabel: string
  tester: string | null
  actualResult: string | null
}

export type Defect = {
  id: number
  projectId: number
  testCaseId: number | null
  title: string
  description: string
  assignee: string
  status: string
  statusLabel: string
  resolution: string | null
}

export type DashboardSummary = {
  totalProjects: number
  receivedProjects: number
  progressingProjects: number
  completedProjects: number
  delayedTasks: number
}

export type ProjectCreatePayload = {
  projectCode: string
  name: string
  department: string
  requester: string
  status: ProjectStatus
  openDueDate: string | null
  description: string
}

export type RequirementCreatePayload = {
  title: string
  systemType: string
  businessType: string
  menuName: string
  description: string
}

export type ScheduleUpdatePayload = {
  planStartDate: string
  planEndDate: string
  note?: string
}

export type TestCaseCreatePayload = {
  wbsTaskId?: number | null
  stage: TestStage
  caseName: string
  procedure: string
  expectedResult: string
}
