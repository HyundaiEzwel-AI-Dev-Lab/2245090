<template>
  <div class="page">
    <div class="page-title">
      <div>
        <h1>{{ project?.name ?? '프로젝트 상세' }}</h1>
        <p>{{ project?.projectCode }} · {{ project?.department }} · {{ project?.requester }}</p>
      </div>
      <StatusBadge v-if="project" :status="project.status" :label="project.statusLabel" />
    </div>

    <div class="grid grid-3">
      <BaseCard title="공정률"><div class="metric">{{ project?.progressRate ?? 0 }}%</div></BaseCard>
      <BaseCard title="오픈예정일"><div class="metric small">{{ project?.openDueDate ?? '미정' }}</div></BaseCard>
      <BaseCard title="설명"><p>{{ project?.description ?? '-' }}</p></BaseCard>
    </div>

    <BaseCard title="요구사항 등록" description="확정 시 WBS 작업단위가 자동 생성됩니다.">
      <div class="form-row">
        <input v-model="requirementForm.title" class="input" placeholder="요구사항명" />
        <input v-model="requirementForm.systemType" class="input" placeholder="시스템구분" />
        <input v-model="requirementForm.businessType" class="input" placeholder="업무구분" />
        <input v-model="requirementForm.menuName" class="input" placeholder="화면명" />
      </div>
      <textarea v-model="requirementForm.description" class="textarea" placeholder="요구사항 설명" />
      <div class="actions"><BaseButton variant="primary" @click="createRequirement">요구사항 저장</BaseButton></div>
    </BaseCard>

    <BaseCard title="요구사항 관리">
      <div class="table-wrap">
        <table>
          <thead><tr><th>요구사항</th><th>시스템</th><th>업무</th><th>화면</th><th>상태</th><th>작업</th></tr></thead>
          <tbody>
            <tr v-for="item in requirements" :key="item.id">
              <td><strong>{{ item.title }}</strong></td>
              <td>{{ item.systemType }}</td>
              <td>{{ item.businessType }}</td>
              <td>{{ item.menuName }}</td>
              <td><StatusBadge :status="item.status" :label="item.statusLabel" /></td>
              <td><BaseButton v-if="item.status !== 'CONFIRMED'" variant="primary" @click="confirmRequirement(item.id)">확정</BaseButton></td>
            </tr>
          </tbody>
        </table>
      </div>
    </BaseCard>

    <BaseCard title="WBS 관리" description="일정/착수/완료 처리 시 내업무에 반영됩니다.">
      <WbsTaskTable :tasks="wbsTasks" @schedule="openSchedule" @start="startTask" @complete="completeTask" />
    </BaseCard>

    <BaseCard title="테스트 케이스">
      <div class="table-wrap">
        <table>
          <thead><tr><th>단계</th><th>케이스</th><th>절차</th><th>예상결과</th><th>결과</th><th>작업</th></tr></thead>
          <tbody>
            <tr v-for="testCase in testCases" :key="testCase.id">
              <td>{{ testCase.stageLabel }}</td>
              <td><strong>{{ testCase.caseName }}</strong></td>
              <td>{{ testCase.procedure }}</td>
              <td>{{ testCase.expectedResult }}</td>
              <td><StatusBadge :status="testCase.result" :label="testCase.resultLabel" /></td>
              <td class="actions">
                <BaseButton variant="primary" @click="executeTest(testCase.id, 'PASS')">정상</BaseButton>
                <BaseButton variant="danger" @click="executeTest(testCase.id, 'FAIL')">오류</BaseButton>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </BaseCard>

    <BaseCard title="결함관리">
      <div class="table-wrap">
        <table>
          <thead><tr><th>결함</th><th>담당자</th><th>상태</th><th>설명</th></tr></thead>
          <tbody>
            <tr v-for="defect in defects" :key="defect.id">
              <td><strong>{{ defect.title }}</strong></td>
              <td>{{ defect.assignee }}</td>
              <td><StatusBadge :status="defect.status" :label="defect.statusLabel" /></td>
              <td>{{ defect.description }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </BaseCard>

    <div v-if="scheduleTarget" class="modal-backdrop" @click.self="scheduleTarget = null">
      <div class="modal">
        <h3>일정 관리</h3>
        <p>{{ scheduleTarget.taskName }}</p>
        <div class="form-row">
          <input v-model="scheduleForm.planStartDate" class="input" type="date" />
          <input v-model="scheduleForm.planEndDate" class="input" type="date" />
        </div>
        <textarea v-model="scheduleForm.note" class="textarea" placeholder="비고" />
        <div class="actions">
          <BaseButton variant="primary" @click="saveSchedule">저장</BaseButton>
          <BaseButton @click="scheduleTarget = null">취소</BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Defect, Project, Requirement, TestCase, TestResult, WbsTask } from '~/types/pms'

const route = useRoute()
const projectId = Number(route.params.id)
const projectService = useProjectService()
const requirementService = useRequirementService()
const wbsService = useWbsService()
const testService = useTestService()

const project = ref<Project | null>(null)
const requirements = ref<Requirement[]>([])
const wbsTasks = ref<WbsTask[]>([])
const testCases = ref<TestCase[]>([])
const defects = ref<Defect[]>([])
const scheduleTarget = ref<WbsTask | null>(null)

const requirementForm = reactive({ title: '', systemType: 'HIMS', businessType: '공통/API', menuName: '', description: '' })
const scheduleForm = reactive({ planStartDate: '', planEndDate: '', note: '' })

const refreshAll = async () => {
  project.value = await projectService.getProject(projectId)
  requirements.value = await requirementService.listRequirements(projectId)
  wbsTasks.value = await wbsService.listWbsTasks(projectId)
  testCases.value = await testService.listTestCases(projectId)
  defects.value = await testService.listDefects(projectId)
}

const createRequirement = async () => {
  await requirementService.createRequirement(projectId, requirementForm)
  Object.assign(requirementForm, { title: '', systemType: 'HIMS', businessType: '공통/API', menuName: '', description: '' })
  await refreshAll()
}

const confirmRequirement = async (requirementId: number) => {
  await requirementService.confirmRequirement(requirementId)
  await refreshAll()
}

const openSchedule = (task: WbsTask) => {
  scheduleTarget.value = task
  scheduleForm.planStartDate = task.planStartDate ?? new Date().toISOString().slice(0, 10)
  scheduleForm.planEndDate = task.planEndDate ?? new Date().toISOString().slice(0, 10)
  scheduleForm.note = task.note ?? ''
}

const saveSchedule = async () => {
  if (!scheduleTarget.value) return
  await wbsService.updateSchedule(scheduleTarget.value.id, scheduleForm)
  scheduleTarget.value = null
  await refreshAll()
}

const startTask = async (task: WbsTask) => {
  await wbsService.startTask(task.id)
  await refreshAll()
}

const completeTask = async (task: WbsTask) => {
  await wbsService.completeTask(task.id)
  await refreshAll()
}

const executeTest = async (testCaseId: number, result: TestResult) => {
  await testService.executeTestCase(testCaseId, {
    result,
    actualResult: result === 'PASS' ? '정상 처리되었습니다.' : '실제 결과가 예상 결과와 다릅니다.',
    tester: '이지윤'
  })
  await refreshAll()
}

onMounted(refreshAll)
</script>

<style scoped>
.metric { font-size: 30px; font-weight: 900; }
.metric.small { font-size: 18px; }
.modal-backdrop { position: fixed; inset: 0; background: rgba(15, 23, 42, 0.45); display: grid; place-items: center; z-index: 20; padding: 20px; }
.modal { width: min(620px, 100%); background: white; border-radius: 20px; padding: 22px; box-shadow: var(--shadow); }
.modal h3 { margin-top: 0; }
</style>
