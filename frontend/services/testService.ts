import type { Defect, TestCase, TestResult, TestStage } from '~/types/pms'

export type TestCaseCreatePayload = {
  wbsTaskId?: number | null
  stage: TestStage
  caseName: string
  procedure: string
  expectedResult: string
}

export const useTestService = () => {
  const { request } = useApi()

  const listTestCases = (projectId: number, stage?: TestStage) =>
    request<TestCase[]>(`/projects/${projectId}/test-cases`, {
      query: stage ? { stage } : undefined
    })

  const createTestCase = (projectId: number, payload: TestCaseCreatePayload) =>
    request<TestCase>(`/projects/${projectId}/test-cases`, {
      method: 'POST',
      body: payload
    })

  const executeTestCase = (testCaseId: number, payload: { result: TestResult; actualResult: string; tester: string }) =>
    request<TestCase>(`/test-cases/${testCaseId}/execute`, {
      method: 'PATCH',
      body: payload
    })

  const listDefects = (projectId: number) => request<Defect[]>(`/projects/${projectId}/defects`)

  return { listTestCases, createTestCase, executeTestCase, listDefects }
}
