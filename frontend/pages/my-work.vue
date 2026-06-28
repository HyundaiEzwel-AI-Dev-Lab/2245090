<template>
  <div class="pms-shell">
    <aside class="pms-sidebar">
      <div class="brand" @click="setMenu('my-work')">
        <div class="brand-mark">P</div>
        <div>
          <strong>PMS</strong>
          <span>Project Management</span>
        </div>
      </div>

      <nav v-for="group in menuGroups" :key="group.title" class="nav-section">
        <p class="nav-title">{{ group.title }}</p>
        <button
          v-for="item in group.items"
          :key="item.key"
          class="nav-item"
          :class="{ active: activeMenu === item.key }"
          @click="setMenu(item.key)"
        >
          {{ item.label }}
          <span v-if="item.count" class="nav-count">{{ item.count }}</span>
        </button>
      </nav>
    </aside>

    <main class="pms-main">
      <header class="topbar">
        <div>
          <p class="eyebrow">전사 프로젝트 관리 시스템 · Backend API 연결</p>
          <h1>{{ pageTitle }}</h1>
        </div>

        <div class="top-actions">
          <div class="search-box">
            <span>⌕</span>
            <input v-model="keyword" placeholder="프로젝트명 / 담당자 검색" @keyup.enter="loadProjects" />
          </div>

          <select v-model="selectedAssignee" class="assignee-select" @change="loadMyTasks">
            <option>김현대</option>
            <option>이지윤</option>
            <option>테스터</option>
          </select>

          <button class="icon-btn" @click="refreshAll">↻</button>
          <div class="user-chip">
            <span class="avatar">J</span>
            <span>{{ selectedAssignee }}</span>
          </div>
        </div>
      </header>

      <section class="primary-tabs">
        <button
          v-for="tab in primaryTabs"
          :key="tab.key"
          class="tab"
          :class="{ active: activePrimaryTab === tab.key }"
          @click="setPrimaryTab(tab.key)"
        >
          {{ tab.label }}
          <span v-if="tab.count" class="count">{{ tab.count }}</span>
          <span v-if="tab.badge" class="badge" :class="{ blue: tab.badge === '통합' }">{{ tab.badge }}</span>
        </button>
      </section>

      <section class="secondary-tabs">
        <button
          v-for="tab in secondaryTabs"
          :key="tab.key"
          class="tab-sm"
          :class="{ active: activeSubTab === tab.key }"
          @click="setSubTab(tab.key)"
        >
          {{ tab.label }}
        </button>
      </section>

      <div class="breadcrumb">&gt; {{ currentGroupTitle }} &gt; {{ pageTitle }}</div>

      <section v-if="apiError" class="error-box">
        <strong>Backend API 연결 실패</strong>
        <p>{{ apiError }}</p>
        <button class="soft-btn" @click="refreshAll">다시 조회</button>
      </section>

      <section v-if="message" class="toast">{{ message }}</section>

      <section v-if="isLoading" class="panel loading-panel">
        <strong>Backend API에서 데이터를 불러오는 중...</strong>
        <p>Spring Boot 8080, PostgreSQL Docker 상태를 확인 중입니다.</p>
      </section>

      <template v-else>
        <section v-if="activeMenu === 'dashboard'" class="dashboard-page">
          <section class="summary-grid">
            <article class="summary-card">
              <p>전체 프로젝트</p>
              <strong>{{ dashboard?.totalProjects ?? projects.length }}</strong>
              <span>Backend /api/dashboard/summary</span>
            </article>
            <article class="summary-card warning">
              <p>접수 프로젝트</p>
              <strong>{{ dashboard?.receivedProjects ?? receivedCount }}</strong>
              <span>신규 검토 대상</span>
            </article>
            <article class="summary-card">
              <p>진행 프로젝트</p>
              <strong>{{ dashboard?.progressingProjects ?? progressingCount }}</strong>
              <span>처리중/테스트 포함</span>
            </article>
            <article class="summary-card danger">
              <p>지연 WBS</p>
              <strong>{{ dashboard?.delayedTasks ?? delayedTaskCount }}</strong>
              <span>planEndDate 기준</span>
            </article>
          </section>

          <section class="content-grid">
            <article class="panel">
              <div class="panel-head">
                <div>
                  <h2>프로젝트 진척 현황</h2>
                  <p>Backend에서 조회한 프로젝트 목록입니다.</p>
                </div>
                <button class="soft-btn" @click="loadProjects">조회</button>
              </div>
              <div class="project-list">
                <button v-for="project in projects" :key="project.id" class="project-card" @click="selectProject(project.id)">
                  <div class="project-top">
                    <div>
                      <span class="project-type blue">{{ project.projectCode }}</span>
                      <h3>{{ project.name }}</h3>
                    </div>
                    <span :class="['status-badge', statusClass(project.status)]">{{ project.statusLabel }}</span>
                  </div>
                  <div class="progress-row">
                    <div class="progress-track">
                      <div class="progress-fill" :style="{ width: project.progressRate + '%' }"></div>
                    </div>
                    <strong>{{ project.progressRate }}%</strong>
                  </div>
                  <div class="project-meta">
                    <span>{{ project.department }}</span>
                    <span>요청자 {{ project.requester }}</span>
                    <span>오픈예정 {{ project.openDueDate ?? '-' }}</span>
                  </div>
                </button>
              </div>
            </article>

            <article class="panel">
              <div class="panel-head">
                <div>
                  <h2>내 업무 요약</h2>
                  <p>/api/my-work/wbs-tasks 결과입니다.</p>
                </div>
                <button class="soft-btn" @click="loadMyTasks">새로고침</button>
              </div>
              <div class="task-list">
                <button v-for="task in myTasks" :key="task.id" class="task-card" @click="openTask(task)">
                  <div class="task-main">
                    <span class="task-kind blue">{{ task.workTypeLabel }}</span>
                    <h3>{{ task.taskName }}</h3>
                    <p>{{ task.assignee }} · {{ task.priority }} · {{ task.difficulty }}</p>
                  </div>
                  <div class="task-side">
                    <span :class="['date-badge', task.delayed ? 'late' : 'soon']">{{ dueText(task) }}</span>
                    <span :class="['status-badge', statusClass(task.status)]">{{ task.statusLabel }}</span>
                  </div>
                </button>
              </div>
            </article>
          </section>
        </section>

        <section v-else-if="activeMenu === 'my-work'" class="my-work-page">
          <section class="summary-grid">
            <article class="summary-card">
              <p>진행 프로젝트</p>
              <strong>{{ projects.length }}</strong>
              <span>Backend 프로젝트 목록</span>
            </article>
            <article class="summary-card warning">
              <p>내 WBS</p>
              <strong>{{ myTasks.length }}</strong>
              <span>{{ selectedAssignee }} 배정 업무</span>
            </article>
            <article class="summary-card">
              <p>선택 프로젝트 테스트</p>
              <strong>{{ testCases.length }}</strong>
              <span>{{ selectedProjectName }}</span>
            </article>
            <article class="summary-card danger">
              <p>결함</p>
              <strong>{{ defects.length }}</strong>
              <span>선택 프로젝트 기준</span>
            </article>
          </section>

          <section class="content-grid">
            <article class="panel project-panel">
              <div class="panel-head">
                <div>
                  <h2>진행 프로젝트</h2>
                  <p>프로젝트 선택 시 요구사항/WBS/테스트/결함을 다시 조회합니다.</p>
                </div>
                <button class="soft-btn" @click="setMenu('project-status')">전체보기</button>
              </div>

              <div class="project-list">
                <button v-for="project in filteredProjects" :key="project.id" class="project-card" :class="{ selected: project.id === selectedProjectId }" @click="selectProject(project.id)">
                  <div class="project-top">
                    <div>
                      <span class="project-type blue">{{ project.projectCode }}</span>
                      <h3>{{ project.name }}</h3>
                    </div>
                    <span :class="['status-badge', statusClass(project.status)]">{{ project.statusLabel }}</span>
                  </div>
                  <div class="progress-row">
                    <div class="progress-track">
                      <div class="progress-fill" :style="{ width: project.progressRate + '%' }"></div>
                    </div>
                    <strong>{{ project.progressRate }}%</strong>
                  </div>
                  <div class="project-meta">
                    <span>{{ project.department }}</span>
                    <span>요청자 {{ project.requester }}</span>
                    <span>{{ project.openDueDate ?? '-' }}</span>
                  </div>
                </button>
              </div>
            </article>

            <article class="panel">
              <div class="panel-head">
                <div>
                  <h2>내 할 일</h2>
                  <p>실제 WBS API 조회 결과입니다.</p>
                </div>
                <div class="view-toggle">
                  <button :class="{ active: viewMode === 'card' }" @click="viewMode = 'card'">카드형</button>
                  <button :class="{ active: viewMode === 'calendar' }" @click="viewMode = 'calendar'">캘린더형</button>
                </div>
              </div>

              <div v-if="viewMode === 'card'" class="task-list">
                <button v-for="task in filteredMyTasks" :key="task.id" class="task-card" @click="openTask(task)">
                  <div class="task-main">
                    <span class="task-kind blue">{{ task.workTypeLabel }}</span>
                    <h3>{{ task.taskName }}</h3>
                    <p>{{ task.assignee }} · 진척 {{ task.progressRate }}%</p>
                  </div>
                  <div class="task-side">
                    <span :class="['date-badge', task.delayed ? 'late' : 'today']">{{ dueText(task) }}</span>
                    <span :class="['status-badge', statusClass(task.status)]">{{ task.statusLabel }}</span>
                  </div>
                </button>

                <div v-if="filteredMyTasks.length === 0" class="empty-mini">
                  조회된 내 업무가 없습니다.
                </div>
              </div>

              <div v-else class="calendar-view">
                <div class="calendar-head">
                  <strong>WBS 일정</strong>
                  <span>{{ selectedAssignee }} 기준</span>
                </div>
                <div class="calendar-grid">
                  <button v-for="task in myTasks" :key="task.id" class="calendar-cell" @click="openTask(task)">
                    <span class="day">{{ task.planEndDate ?? '미등록' }}</span>
                    <p class="calendar-item">{{ task.taskName }}</p>
                  </button>
                </div>
              </div>
            </article>
          </section>

          <WbsTable :tasks="filteredMyTasks" @open="openTask" @start="startWbs" @complete="completeWbs" />
        </section>

        <section v-else-if="activeMenu === 'project-register'" class="panel page-panel">
          <div class="panel-head">
            <div>
              <h2>프로젝트 등록</h2>
              <p>POST /api/projects 로 실제 등록합니다.</p>
            </div>
            <button class="primary-btn" @click="createProject">등록</button>
          </div>

          <div class="form-grid">
            <label>프로젝트 코드<input v-model="projectForm.projectCode" /></label>
            <label>프로젝트명<input v-model="projectForm.name" /></label>
            <label>담당부서<input v-model="projectForm.department" /></label>
            <label>요청자<input v-model="projectForm.requester" /></label>
            <label>상태
              <select v-model="projectForm.status">
                <option value="RECEIVED">접수</option>
                <option value="DISCUSSING">협의중</option>
                <option value="IN_PROGRESS">처리중</option>
                <option value="TESTING">테스트</option>
              </select>
            </label>
            <label>오픈 예정일<input v-model="projectForm.openDueDate" type="date" /></label>
            <label class="wide">설명<textarea v-model="projectForm.description"></textarea></label>
          </div>
        </section>

        <section v-else-if="activeMenu === 'project-status'" class="panel page-panel">
          <div class="panel-head">
            <div>
              <h2>프로젝트 현황</h2>
              <p>GET /api/projects 조회 결과입니다.</p>
            </div>
            <button class="soft-btn" @click="loadProjects">새로고침</button>
          </div>
          <ProjectTable :projects="filteredProjects" @open="selectProject" />
        </section>

        <section v-else-if="activeMenu === 'requirements'" class="panel page-panel">
          <div class="panel-head">
            <div>
              <h2>요구사항 관리</h2>
              <p>선택 프로젝트: {{ selectedProjectName }}</p>
            </div>
            <button class="primary-btn" @click="createRequirement">요구사항 등록</button>
          </div>

          <div class="form-grid compact">
            <label>제목<input v-model="requirementForm.title" /></label>
            <label>시스템<input v-model="requirementForm.systemType" /></label>
            <label>업무유형<input v-model="requirementForm.businessType" /></label>
            <label>메뉴명<input v-model="requirementForm.menuName" /></label>
            <label class="wide">설명<textarea v-model="requirementForm.description"></textarea></label>
          </div>

          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>요구사항명</th>
                  <th>시스템</th>
                  <th>업무유형</th>
                  <th>메뉴</th>
                  <th>상태</th>
                  <th>액션</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="req in requirements" :key="req.id">
                  <td>REQ-{{ req.id }}</td>
                  <td>{{ req.title }}</td>
                  <td>{{ req.systemType }}</td>
                  <td>{{ req.businessType }}</td>
                  <td>{{ req.menuName }}</td>
                  <td><span :class="['status-badge', statusClass(req.status)]">{{ req.statusLabel }}</span></td>
                  <td><button class="table-btn" @click="confirmRequirement(req.id)">확정</button></td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else-if="activeMenu === 'wbs'" class="panel page-panel">
          <div class="panel-head">
            <div>
              <h2>WBS 관리</h2>
              <p>GET /api/projects/{{ selectedProjectId }}/wbs-tasks 조회 결과입니다.</p>
            </div>
            <button class="soft-btn" @click="loadSelectedProjectChildren">새로고침</button>
          </div>
          <WbsTable :tasks="wbsTasks" @open="openTask" @start="startWbs" @complete="completeWbs" />
        </section>

        <section v-else-if="['unit-test', 'dev-test', 'ops-test'].includes(activeMenu)" class="panel page-panel">
          <div class="panel-head">
            <div>
              <h2>{{ pageTitle }}</h2>
              <p>GET /api/projects/{{ selectedProjectId }}/test-cases 조회 결과입니다.</p>
            </div>
            <button class="primary-btn" @click="createTestCase">테스트 케이스 등록</button>
          </div>

          <div class="test-grid">
            <button v-for="test in filteredTests" :key="test.id" class="test-card" @click="executeTest(test.id, test.result === 'PASS' ? 'FAIL' : 'PASS')">
              <span class="task-kind purple">{{ test.stageLabel }}</span>
              <h3>{{ test.caseName }}</h3>
              <p>{{ test.expectedResult }}</p>
              <span :class="['status-badge', statusClass(test.result)]">{{ test.resultLabel }}</span>
            </button>
          </div>
        </section>

        <section v-else-if="activeMenu === 'defects'" class="panel page-panel">
          <div class="panel-head">
            <div>
              <h2>결함관리</h2>
              <p>GET /api/projects/{{ selectedProjectId }}/defects 조회 결과입니다.</p>
            </div>
            <button class="soft-btn" @click="loadDefects">새로고침</button>
          </div>

          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>결함명</th>
                  <th>담당자</th>
                  <th>상태</th>
                  <th>내용</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="defect in defects" :key="defect.id">
                  <td>DEF-{{ defect.id }}</td>
                  <td>{{ defect.title }}</td>
                  <td>{{ defect.assignee }}</td>
                  <td><span :class="['status-badge', statusClass(defect.status)]">{{ defect.statusLabel }}</span></td>
                  <td>{{ defect.description }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else class="panel page-panel">
          <div class="empty-state">
            <strong>{{ pageTitle }}</strong>
            <p>POC 준비 영역입니다. 좌측 메뉴를 눌러 실제 API 연결 화면을 확인하세요.</p>
            <button class="primary-btn" @click="setMenu('my-work')">내업무로 이동</button>
          </div>
        </section>
      </template>
    </main>

    <div v-if="selectedTask" class="modal-backdrop" @click.self="selectedTask = null">
      <article class="task-modal">
        <button class="close-btn" @click="selectedTask = null">×</button>
        <span class="task-kind blue">{{ selectedTask.workTypeLabel }}</span>
        <h2>{{ selectedTask.taskName }}</h2>
        <p>{{ selectedTask.note ?? '등록된 메모가 없습니다.' }}</p>

        <dl>
          <div><dt>담당자</dt><dd>{{ selectedTask.assignee }}</dd></div>
          <div><dt>상태</dt><dd>{{ selectedTask.statusLabel }}</dd></div>
          <div><dt>계획일</dt><dd>{{ selectedTask.planStartDate ?? '-' }} ~ {{ selectedTask.planEndDate ?? '-' }}</dd></div>
          <div><dt>진척률</dt><dd>{{ selectedTask.progressRate }}%</dd></div>
        </dl>

        <div class="modal-actions">
          <button class="soft-btn" @click="startWbs(selectedTask.id)">착수</button>
          <button class="primary-btn" @click="completeWbs(selectedTask.id)">완료</button>
        </div>
      </article>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useProjectService, type ProjectCreatePayload } from '~/services/projectService'
import { useRequirementService, type RequirementCreatePayload } from '~/services/requirementService'
import { useWbsService } from '~/services/wbsService'
import { useTestService } from '~/services/testService'
import type {
  DashboardSummary,
  Defect,
  Project,
  ProjectStatus,
  Requirement,
  TestCase,
  TestResult,
  TestStage,
  WbsTask
} from '~/types/pms'

type MenuKey =
  | 'dashboard'
  | 'my-work'
  | 'project-register'
  | 'project-status'
  | 'test-library'
  | 'system'
  | 'project-info'
  | 'requirements'
  | 'wbs'
  | 'unit-test'
  | 'dev-test'
  | 'ops-test'
  | 'defects'

const projectService = useProjectService()
const requirementService = useRequirementService()
const wbsService = useWbsService()
const testService = useTestService()

const activeMenu = ref<MenuKey>('my-work')
const activePrimaryTab = ref('my-work')
const activeSubTab = ref('summary')
const viewMode = ref<'card' | 'calendar'>('card')
const selectedTask = ref<WbsTask | null>(null)
const keyword = ref('')
const selectedAssignee = ref('김현대')
const message = ref('')
const apiError = ref('')
const isLoading = ref(false)

const dashboard = ref<DashboardSummary | null>(null)
const projects = ref<Project[]>([])
const requirements = ref<Requirement[]>([])
const wbsTasks = ref<WbsTask[]>([])
const myTasks = ref<WbsTask[]>([])
const testCases = ref<TestCase[]>([])
const defects = ref<Defect[]>([])
const selectedProjectId = ref<number | null>(null)

const menuGroups = [
  {
    title: '통합관리',
    items: [
      { key: 'dashboard' as MenuKey, label: '대시보드' },
      { key: 'my-work' as MenuKey, label: '내업무' },
      { key: 'project-register' as MenuKey, label: '프로젝트 등록' },
      { key: 'project-status' as MenuKey, label: '프로젝트 현황' },
      { key: 'test-library' as MenuKey, label: '테스트 라이브러리' },
      { key: 'system' as MenuKey, label: '시스템 관리' }
    ]
  },
  {
    title: '프로젝트 관리',
    items: [
      { key: 'project-info' as MenuKey, label: '프로젝트정보' },
      { key: 'requirements' as MenuKey, label: '요구사항 관리' },
      { key: 'wbs' as MenuKey, label: 'WBS 관리' },
      { key: 'unit-test' as MenuKey, label: '단위테스트' },
      { key: 'dev-test' as MenuKey, label: 'DEV테스트' },
      { key: 'ops-test' as MenuKey, label: '운영테스트' },
      { key: 'defects' as MenuKey, label: '결함관리' }
    ]
  }
]

const primaryTabs = [
  { key: 'my-work', label: '내업무', count: computed(() => myTasks.value.length) },
  { key: 'project-status', label: '프로젝트 현황', badge: '통합' },
  { key: 'requirements', label: '요구사항 관리', badge: '개별' }
]

const secondaryTabs = [
  { key: 'summary', label: '업무요약' },
  { key: 'project-info', label: '프로젝트정보' },
  { key: 'requirements', label: '요구사항관리' },
  { key: 'wbs', label: 'WBS관리' },
  { key: 'unit-test', label: '단위테스트' },
  { key: 'dev-test', label: 'DEV테스트' }
]

const pageTitle = computed(() => {
  const found = menuGroups.flatMap(group => group.items).find(item => item.key === activeMenu.value)
  return found?.label ?? '내업무'
})

const currentGroupTitle = computed(() => {
  return menuGroups.find(group => group.items.some(item => item.key === activeMenu.value))?.title ?? '통합관리'
})

const selectedProjectName = computed(() => {
  return projects.value.find(project => project.id === selectedProjectId.value)?.name ?? '선택 프로젝트 없음'
})

const filteredProjects = computed(() => {
  if (!keyword.value.trim()) return projects.value
  return projects.value.filter(project =>
    project.name.includes(keyword.value) ||
    project.department.includes(keyword.value) ||
    project.requester.includes(keyword.value)
  )
})

const filteredMyTasks = computed(() => {
  if (!keyword.value.trim()) return myTasks.value
  return myTasks.value.filter(task =>
    task.taskName.includes(keyword.value) ||
    task.assignee.includes(keyword.value) ||
    task.workTypeLabel.includes(keyword.value)
  )
})

const filteredTests = computed(() => {
  const stageMap: Record<string, TestStage | null> = {
    'unit-test': 'UNIT',
    'dev-test': 'DEV',
    'ops-test': 'OPERATION'
  }
  const stage = stageMap[activeMenu.value] ?? null
  return stage ? testCases.value.filter(test => test.stage === stage) : testCases.value
})

const receivedCount = computed(() => projects.value.filter(project => project.status === 'RECEIVED').length)
const progressingCount = computed(() => projects.value.filter(project => ['IN_PROGRESS', 'TESTING'].includes(project.status)).length)
const delayedTaskCount = computed(() => [...myTasks.value, ...wbsTasks.value].filter(task => task.delayed).length)

const projectForm = ref<ProjectCreatePayload>({
  projectCode: `PJ-${Date.now().toString().slice(-6)}`,
  name: '신규 PMS 개선 프로젝트',
  department: '웹기획팀',
  requester: '이지윤',
  status: 'RECEIVED',
  openDueDate: new Date(Date.now() + 1000 * 60 * 60 * 24 * 14).toISOString().slice(0, 10),
  description: '프론트에서 등록한 프로젝트입니다.'
})

const requirementForm = ref<RequirementCreatePayload>({
  title: '프론트 연동 요구사항',
  systemType: 'HIMS',
  businessType: '공통/API',
  menuName: '내업무',
  description: '프론트에서 등록한 요구사항입니다.'
})

const setMenu = async (key: MenuKey) => {
  activeMenu.value = key
  activeSubTab.value = key === 'requirements' || key === 'wbs' || key === 'unit-test' || key === 'dev-test' ? key : 'summary'

  if (key === 'dashboard') await loadDashboard()
  if (key === 'project-status') await loadProjects()
  if (key === 'my-work') await loadMyTasks()
  if (['requirements', 'wbs', 'unit-test', 'dev-test', 'ops-test', 'defects'].includes(key)) await loadSelectedProjectChildren()
}

const setPrimaryTab = async (key: string) => {
  activePrimaryTab.value = key
  if (key === 'my-work') await setMenu('my-work')
  if (key === 'project-status') await setMenu('project-status')
  if (key === 'requirements') await setMenu('requirements')
}

const setSubTab = async (key: string) => {
  activeSubTab.value = key
  if (key === 'summary') await setMenu('my-work')
  if (key === 'project-info') await setMenu('project-info')
  if (key === 'requirements') await setMenu('requirements')
  if (key === 'wbs') await setMenu('wbs')
  if (key === 'unit-test') await setMenu('unit-test')
  if (key === 'dev-test') await setMenu('dev-test')
}

const handleApi = async (action: () => Promise<void>) => {
  try {
    apiError.value = ''
    await action()
  } catch (error: any) {
    apiError.value = error?.message ?? 'API 요청 중 오류가 발생했습니다.'
  }
}

const refreshAll = async () => {
  isLoading.value = true
  await handleApi(async () => {
    await Promise.all([loadDashboard(), loadProjects(), loadMyTasks()])
    if (projects.value.length > 0 && !selectedProjectId.value) {
      selectedProjectId.value = projects.value[0].id
    }
    await loadSelectedProjectChildren()
  })
  isLoading.value = false
}

const loadDashboard = async () => {
  await handleApi(async () => {
    dashboard.value = await projectService.getDashboardSummary()
  })
}

const loadProjects = async () => {
  await handleApi(async () => {
    projects.value = await projectService.searchProjects({ keyword: keyword.value })
    if (projects.value.length > 0 && !selectedProjectId.value) {
      selectedProjectId.value = projects.value[0].id
    }
  })
}

const loadMyTasks = async () => {
  await handleApi(async () => {
    myTasks.value = await wbsService.listMyTasks(selectedAssignee.value)
  })
}

const loadSelectedProjectChildren = async () => {
  if (!selectedProjectId.value) return
  await handleApi(async () => {
    const projectId = selectedProjectId.value!
    const [reqs, wbs, tests, defectList] = await Promise.all([
      requirementService.listRequirements(projectId),
      wbsService.listWbsTasks(projectId),
      testService.listTestCases(projectId),
      testService.listDefects(projectId)
    ])

    requirements.value = reqs
    wbsTasks.value = wbs
    testCases.value = tests
    defects.value = defectList
  })
}

const selectProject = async (projectId: number) => {
  selectedProjectId.value = projectId
  await loadSelectedProjectChildren()
  toast(`${selectedProjectName.value} 데이터를 조회했습니다.`)
}

const createProject = async () => {
  await handleApi(async () => {
    const created = await projectService.createProject(projectForm.value)
    projects.value.unshift(created)
    selectedProjectId.value = created.id
    toast('프로젝트가 등록되었습니다.')
  })
}

const createRequirement = async () => {
  if (!selectedProjectId.value) return
  await handleApi(async () => {
    const created = await requirementService.createRequirement(selectedProjectId.value!, requirementForm.value)
    requirements.value.unshift(created)
    toast('요구사항이 등록되었습니다.')
  })
}

const confirmRequirement = async (requirementId: number) => {
  await handleApi(async () => {
    const updated = await requirementService.confirmRequirement(requirementId)
    requirements.value = requirements.value.map(item => item.id === updated.id ? updated : item)
    await loadSelectedProjectChildren()
    toast('요구사항이 확정되었습니다. WBS가 갱신되었습니다.')
  })
}

const startWbs = async (taskId: number) => {
  await handleApi(async () => {
    const updated = await wbsService.startTask(taskId)
    updateTaskInLists(updated)
    toast('WBS 작업을 착수 처리했습니다.')
  })
}

const completeWbs = async (taskId: number) => {
  await handleApi(async () => {
    const updated = await wbsService.completeTask(taskId)
    updateTaskInLists(updated)
    toast('WBS 작업을 완료 처리했습니다.')
  })
}

const createTestCase = async () => {
  if (!selectedProjectId.value) return
  await handleApi(async () => {
    const created = await testService.createTestCase(selectedProjectId.value!, {
      wbsTaskId: wbsTasks.value[0]?.id ?? null,
      stage: activeMenu.value === 'dev-test' ? 'DEV' : activeMenu.value === 'ops-test' ? 'OPERATION' : 'UNIT',
      caseName: '프론트 등록 테스트 케이스',
      procedure: '화면에서 테스트 케이스를 등록한다.',
      expectedResult: '테스트 케이스가 정상 등록된다.'
    })
    testCases.value.unshift(created)
    toast('테스트 케이스가 등록되었습니다.')
  })
}

const executeTest = async (testCaseId: number, result: TestResult) => {
  await handleApi(async () => {
    const updated = await testService.executeTestCase(testCaseId, {
      result,
      actualResult: result === 'PASS' ? '정상 처리 확인' : '오류 발생 확인',
      tester: selectedAssignee.value
    })
    testCases.value = testCases.value.map(item => item.id === updated.id ? updated : item)
    await loadDefects()
    toast(`테스트 결과가 ${updated.resultLabel}로 저장되었습니다.`)
  })
}

const loadDefects = async () => {
  if (!selectedProjectId.value) return
  await handleApi(async () => {
    defects.value = await testService.listDefects(selectedProjectId.value!)
  })
}

const updateTaskInLists = (updated: WbsTask) => {
  wbsTasks.value = wbsTasks.value.map(task => task.id === updated.id ? updated : task)
  myTasks.value = myTasks.value.map(task => task.id === updated.id ? updated : task)
  if (selectedTask.value?.id === updated.id) selectedTask.value = updated
}

const openTask = (task: WbsTask) => {
  selectedTask.value = task
}

const toast = (text: string) => {
  message.value = text
  window.setTimeout(() => {
    if (message.value === text) message.value = ''
  }, 2300)
}

const statusClass = (status: string) => {
  if (['COMPLETED', 'PASS', 'CONFIRMED'].includes(status)) return 'working'
  if (['IN_PROGRESS', 'TESTING'].includes(status)) return 'review'
  if (['FAIL', 'REJECTED'].includes(status)) return 'danger'
  if (['READY', 'RECEIVED', 'DRAFT', 'NOT_RUN'].includes(status)) return 'hold'
  return 'ready'
}

const dueText = (task: WbsTask) => {
  if (!task.planEndDate) return '미등록'
  if (task.delayed) return '지연'
  return task.planEndDate
}

onMounted(refreshAll)
</script>
