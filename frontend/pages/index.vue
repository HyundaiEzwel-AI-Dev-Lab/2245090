<template>
  <div class="app">
    <aside class="sidebar" :class="{ open: sidebarOpen }">
      <div class="brand">
        <div class="logo">P</div>
        <div>
          <h1>전사 프로젝트 관리 시스템</h1>
          <small>SB 전체 POC · Vue + API</small>
        </div>
      </div>

      <div v-for="group in navGroups" :key="group.title" class="nav-group">
        <div class="nav-title">{{ group.title }}</div>
        <button
          v-for="item in group.items"
          :key="item.key"
          class="nav-btn"
          :class="{ active: page === item.key, 'nav-sub': item.sub }"
          @click="go(item.key)"
        >
          <span>{{ item.icon }} {{ item.label }}</span>
          <b v-if="item.count" class="nav-count">{{ item.count }}</b>
        </button>
      </div>
    </aside>

    <main class="main">
      <header class="topbar">
        <div class="top-left">
          <button class="icon-btn hamb" @click="sidebarOpen = !sidebarOpen">☰</button>
          <button class="search-pill" @click="projectSearchOpen = true">🔎 프로젝트 검색 / 최근 조회</button>
        </div>

        <div class="top-actions">
          <button class="icon-btn" @click="myProjectOpen = true">📂 <span class="label">내 프로젝트</span></button>
          <select v-model="assignee" class="assignee-select" @change="loadMyWork">
            <option>이지윤</option>
            <option>김현대</option>
            <option>박현대</option>
            <option>테스터</option>
          </select>
          <button class="icon-btn" @click="noticeOpen = true">🔔 <span class="dot"></span></button>
          <div class="user-chip">
            <div class="avatar">이</div>
            <b>{{ assignee }}</b>
            <span class="muted">e커머스팀</span>
          </div>
        </div>
      </header>

      <section class="content">
        <div class="tabs">
          <button
            v-for="tab in openTabs"
            :key="tab.id"
            class="tab"
            :class="{ active: tab.id === page }"
            @click="go(tab.id)"
          >
            <span class="badge" :class="{ ind: tab.type === '개별' }">{{ tab.type }}</span>
            {{ tab.title }}
            <span class="muted">×</span>
          </button>
        </div>

        <div class="page-head">
          <div>
            <div class="crumb">{{ meta.crumb }}</div>
            <h2 class="page-title">{{ meta.title }}</h2>
            <p class="page-desc">{{ meta.desc }}</p>
          </div>

          <div class="actions">
            <template v-if="page === 'mywork'">
              <div class="toggle">
                <button :class="{ active: assigned }" @click="assigned = true">배정 후</button>
                <button :class="{ active: !assigned }" @click="assigned = false">배정 전</button>
              </div>
              <div class="toggle">
                <button :class="{ active: myworkView === 'card' }" @click="myworkView = 'card'">카드형</button>
                <button :class="{ active: myworkView === 'cal' }" @click="myworkView = 'cal'">캘린더형</button>
              </div>
            </template>

            <button v-if="page === 'projectRegister'" class="btn primary" @click="saveProject">등록</button>
            <button v-else-if="page === 'requirements'" class="btn primary" @click="saveRequirement">요구사항 등록</button>
            <button v-else-if="page === 'wbs'" class="btn primary" @click="openSchedule(firstWbs)">일정 관리</button>
            <button class="btn" @click="refreshAll">조회(F2)</button>
          </div>
        </div>

        <section v-if="toastMessage" class="inline-toast">{{ toastMessage }}</section>

        <section v-if="apiError" class="error-box">
          <strong>API 오류</strong>
          <span>{{ apiError }}</span>
          <button class="btn small" @click="refreshAll">다시 조회</button>
        </section>

        <section v-if="page === 'mywork'">
          <template v-if="!assigned">
            <div class="grid cols-5">
              <div v-for="item in emptyKpis" :key="item" class="card kpi">
                <div><div class="label">{{ item }}</div><div class="num">0</div></div>
              </div>
            </div>
            <div class="grid cols-3" style="margin-top:16px">
              <div class="notice">진행중인 프로젝트가 없습니다.</div>
              <div class="notice">진행중인 업무가 없습니다.</div>
              <div class="notice">접수된 프로젝트가 없습니다.</div>
            </div>
          </template>

          <template v-else>
            <div class="grid cols-5">
              <div class="card kpi">
                <div><div class="label">진행 프로젝트</div><div class="num">{{ progressProjects.length }}</div><div class="hint">건</div></div>
              </div>
              <div class="card kpi">
                <div><div class="label">내 할 일</div><div class="num">{{ visibleMyTasks.length }}</div><div class="hint">건</div></div>
              </div>
              <div class="card kpi">
                <div><div class="label">대기</div><div class="num">{{ waitingProjects.length }}</div><div class="hint">건</div></div>
              </div>
              <div class="card kpi">
                <div><div class="label">금주 마감</div><div class="num">{{ weekDueTasks.length }}</div><div class="hint">건</div></div>
              </div>
              <div class="card kpi">
                <div><div class="label">지연</div><div class="num">{{ delayedTasks.length }}</div><div class="hint">건</div></div>
              </div>
            </div>

            <div class="notice" style="margin:16px 0">
              프로젝트 관리 &gt; 요구사항 관리에서 요구사항이 ‘확정’되면 WBS 관리 메뉴에 작업범위가 자동 생성됩니다.
              WBS 관리 메뉴에서 일정이 등록된 업무만 내 할 일 업무에 일정이 표시됩니다.
              단, 캘린더는 일정 등록된 업무만 표시됩니다.
            </div>

            <section v-if="myworkView === 'card'" class="grid">
              <div>
                <div class="section-title">진행중 ({{ progressProjects.length }})</div>
                <div class="cards-row">
                  <button
                    v-for="project in progressProjects"
                    :key="project.id"
                    class="project-card"
                    @click="selectProject(project.id)"
                  >
                    <div class="muted">
                      {{ project.openDueDate || '오픈일 미정' }}
                      <b v-if="project.openDueDate">({{ dday(project.openDueDate) }})</b>
                    </div>
                    <div style="margin-top:8px">
                      <span class="badge" :class="statusClass(project.status)">{{ project.statusLabel || statusText(project.status) }}</span>
                    </div>
                    <h3>{{ project.name }}</h3>
                    <div class="progress-line">
                      <div class="bar">
                        <i :style="{ width: projectProgress(project) + '%' }"></i>
                      </div>
                      <b>{{ projectProgress(project) }}%</b>
                    </div>
                    <div class="meta-row">
                      <span>배정 -</span>
                      <span>업무 {{ projectTaskCount(project.id) }}</span>
                      <span>완료 {{ projectDoneCount(project.id) }}</span>
                    </div>
                  </button>
                </div>
              </div>

              <div>
                <div class="section-title">내 할 일 ({{ visibleMyTasks.length }})</div>
                <div class="table-wrap">
                  <table>
                    <thead>
                      <tr>
                        <th>업무명</th>
                        <th>마감일(D-day)</th>
                        <th>프로젝트명</th>
                        <th>공정률</th>
                        <th>더보기</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="task in sortedMyTasks" :key="task.id" :class="{ 'danger-row': isDelayed(task) }">
                        <td>
                          <button class="link-button" @click="routeTask(task)">{{ task.taskName }}</button>
                        </td>
                        <td>
                          <button class="link-button" @click="openSchedule(task)">
                            {{ task.planEndDate ? `${md(task.planEndDate)} 마감 (${dday(task.planEndDate)})` : '일정 미등록' }}
                            <span v-if="isDelayed(task)" class="badge red">지연</span>
                          </button>
                        </td>
                        <td>{{ projectName(task.projectId) }}</td>
                        <td>{{ task.progressRate }}%</td>
                        <td>
                          <button class="btn small" @click="openSchedule(task)">일정관리</button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <div>
                <div class="section-title">대기 ({{ waitingProjects.length }})</div>
                <div class="cards-row">
                  <button
                    v-for="project in waitingProjects"
                    :key="project.id"
                    class="project-card"
                    @click="selectProject(project.id)"
                  >
                    <span class="muted">{{ project.department }}</span>
                    <h3>{{ project.name }}</h3>
                    <span class="badge gray">{{ project.statusLabel || statusText(project.status) }}</span>
                    <div class="muted" style="margin-top:10px">{{ project.openDueDate || '오픈일 미정' }}</div>
                  </button>
                </div>
              </div>
            </section>

            <section v-else class="calendar-layout">
              <div>
                <div class="calendar-toolbar">
                  <div class="month-control">
                    <button class="btn small" @click="moveMonth(-1)">&lt;</button>
                    <b>{{ calendarDate.getFullYear() }}년 {{ calendarDate.getMonth() + 1 }}월</b>
                    <button class="btn small" @click="moveMonth(1)">&gt;</button>
                  </div>
                  <div class="pill-row">
                    <button class="btn small primary" @click="openSchedule(registerableTasks[0] || firstWbs)">+ 일정 등록</button>
                    <button class="btn small" @click="goToday">오늘</button>
                  </div>
                </div>

                <div class="calendar sb-range-calendar">
                  <div class="cal-head">
                    <div v-for="d in ['일','월','화','수','목','금','토']" :key="d">{{ d }}</div>
                  </div>

                  <div class="cal-body">
                    <div class="cal-grid">
                      <div
                        v-for="cell in calendarCells"
                        :key="cell.iso"
                        class="day"
                        :class="{ mutedDay: !cell.current }"
                      >
                        <span class="date" :class="{ today: cell.today }">{{ cell.date.getDate() }}</span>
                      </div>
                    </div>

                    <div class="range-layer">
                      <button
                        v-for="segment in calendarSegments"
                        :key="segment.key"
                        class="range-event"
                        :class="{ red: isDelayed(segment.task), gray: segment.task.status === 'COMPLETED', paused: segment.task.status === 'PAUSED' }"
                        :style="segment.style"
                        :disabled="segment.task.status === 'COMPLETED'"
                        @click="segment.task.status !== 'COMPLETED' && openSchedule(segment.task)"
                      >
                        <b>{{ segment.task.taskName }} (~ {{ md(segment.task.planEndDate) }})</b>
                        <span>{{ projectName(segment.task.projectId) }}</span>
                        <em v-if="isDelayed(segment.task)">지연</em>
                        <em v-if="segment.task.status === 'PAUSED'">일시중단</em>
                      </button>

                      <button
                        v-for="more in calendarMoreButtons"
                        :key="more.key"
                        class="more range-more"
                        :style="more.style"
                        @click="openMore(more.iso, more.tasks)"
                      >
                        +{{ more.count }} 더보기
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <aside class="card unscheduled">
                <h3 class="card-title">일정 미등록 업무 ({{ unscheduledTasks.length }})</h3>
                <p v-if="unscheduledTasks.length === 0" class="muted">일정 미등록 업무가 없습니다.</p>

                <article v-for="task in unscheduledTasks" :key="task.id" class="mini-card unregistered-card">
                  <span class="muted">{{ projectName(task.projectId) }}</span>
                  <strong>{{ task.taskName }}</strong>
                  <button class="btn small primary" @click="openSchedule(task)">일정 등록</button>
                </article>

                <div class="section-title" style="margin-top:18px">일정 변경 가능 업무</div>
                <article
                  v-for="task in scheduledTasks.filter(item => item.status !== 'COMPLETED')"
                  :key="'scheduled-' + task.id"
                  class="mini-card unregistered-card"
                >
                  <span class="muted">{{ projectName(task.projectId) }}</span>
                  <strong>{{ task.taskName }}</strong>
                  <span class="muted">{{ md(task.planStartDate) }} ~ {{ md(task.planEndDate) }}</span>
                  <button class="btn small" @click="openSchedule(task)">일정 관리</button>
                </article>
              </aside>
            </section>
          </template>
        </section>

        <section v-else-if="page === 'projectRegister'">
          <div class="form-section">
            <h3 class="section-title">기본 정보 <span class="badge">JIRA 연계/독립관리</span></h3>
            <div class="form-grid">
              <div class="field"><label>프로젝트 코드*</label><input v-model="projectForm.projectCode" /></div>
              <div class="field"><label>프로젝트명*</label><input v-model="projectForm.name" /></div>
              <div class="field">
                <label>처리상태*</label>
                <select v-model="projectForm.status">
                  <option value="RECEIVED">접수</option>
                  <option value="DISCUSSING">협의중</option>
                  <option value="IN_PROGRESS">처리중</option>
                  <option value="TESTING">테스트</option>
                  <option value="COMPLETED">완료</option>
                </select>
              </div>
              <div class="field"><label>오픈예정일</label><input v-model="projectForm.openDueDate" type="date" /></div>
              <div class="field"><label>요청부서*</label><input v-model="projectForm.department" /></div>
              <div class="field"><label>요청자*</label><input v-model="projectForm.requester" /></div>
              <div class="field full-span"><label>설명</label><textarea v-model="projectForm.description"></textarea></div>
            </div>
          </div>
          <button class="btn primary" @click="saveProject">프로젝트 등록</button>
        </section>

        <section v-else-if="page === 'projectStatus'">
          <div class="filters">
            <div class="field"><label>프로젝트</label><input v-model="keyword" placeholder="프로젝트명 입력" /></div>
            <div class="field"><label>처리상태</label><select><option>전체</option></select></div>
            <div class="field"><label>담당부서</label><input placeholder="부서 입력" /></div>
            <div class="field"><label>요청자</label><input placeholder="요청자 입력" /></div>
            <div class="field"><label>&nbsp;</label><button class="btn primary" @click="loadProjects">조회</button></div>
          </div>
          <ProjectTable :projects="filteredProjects" @select="selectProject" />
        </section>

        <section v-else-if="page === 'projectInfo'">
          <template v-if="selectedProject">
            <div class="grid cols-3">
              <div class="card kpi"><div><div class="label">프로젝트 상태</div><div class="num" style="font-size:24px">{{ selectedProject.statusLabel || statusText(selectedProject.status) }}</div></div></div>
              <div class="card kpi"><div><div class="label">공정률</div><div class="num">{{ projectProgress(selectedProject) }}%</div></div></div>
              <div class="card kpi"><div><div class="label">오픈예정</div><div class="num" style="font-size:24px">{{ dday(selectedProject.openDueDate) }}</div></div></div>
            </div>
            <div class="form-section" style="margin-top:16px">
              <h3 class="section-title">프로젝트 기본정보</h3>
              <div class="form-grid">
                <div class="field"><label>프로젝트명</label><input :value="selectedProject.name" readonly /></div>
                <div class="field"><label>요청부서</label><input :value="selectedProject.department" readonly /></div>
                <div class="field"><label>요청자</label><input :value="selectedProject.requester" readonly /></div>
                <div class="field"><label>오픈예정일</label><input :value="selectedProject.openDueDate || '미정'" readonly /></div>
              </div>
            </div>
          </template>
          <div v-else class="empty"><strong>프로젝트가 선택되지 않았습니다.</strong></div>
        </section>

        <section v-else-if="page === 'requirements'">
          <div class="form-section">
            <h3 class="section-title">요구사항 등록</h3>
            <div class="form-grid">
              <div class="field"><label>요구사항명</label><input v-model="requirementForm.title" /></div>
              <div class="field"><label>시스템</label><input v-model="requirementForm.systemType" /></div>
              <div class="field"><label>업무구분</label><input v-model="requirementForm.businessType" /></div>
              <div class="field"><label>화면명</label><input v-model="requirementForm.menuName" /></div>
              <div class="field full-span"><label>설명</label><textarea v-model="requirementForm.description"></textarea></div>
            </div>
          </div>
          <button class="btn primary" @click="saveRequirement">요구사항 등록</button>
          <div style="height:14px"></div>
          <div class="table-wrap">
            <table>
              <thead><tr><th>요구사항ID</th><th>요구사항명</th><th>시스템</th><th>업무구분</th><th>화면명</th><th>상태</th><th>WBS</th></tr></thead>
              <tbody>
                <tr v-for="req in requirements" :key="req.id">
                  <td>REQ-{{ req.id }}</td>
                  <td>{{ req.title }}</td>
                  <td>{{ req.systemType }}</td>
                  <td>{{ req.businessType }}</td>
                  <td>{{ req.menuName }}</td>
                  <td><span class="badge" :class="statusClass(req.status)">{{ req.statusLabel || statusText(req.status) }}</span></td>
                  <td><button class="btn small primary" @click="confirmRequirement(req.id)">확정</button></td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else-if="page === 'wbs'">
          <WbsTable
            :tasks="visibleWbsTasks"
            @open="openTask"
            @schedule="openSchedule"
            @start="startTask"
            @complete="completeTask"
          />
        </section>

        <section v-else-if="['unitTest','devScenario','opsScenario'].includes(page)">
          <div class="grid cols-3">
            <button
              v-for="test in filteredTests"
              :key="test.id"
              class="project-card"
              @click="executeTest(test.id, test.result === 'PASS' ? 'FAIL' : 'PASS')"
            >
              <span class="badge purple">{{ test.stageLabel || test.stage }}</span>
              <h3>{{ test.caseName }}</h3>
              <p class="muted">{{ test.expectedResult }}</p>
              <span class="badge" :class="statusClass(test.result)">{{ test.resultLabel || statusText(test.result) }}</span>
            </button>
          </div>
        </section>

        <section v-else-if="['devDefect','opsDefect'].includes(page)">
          <div class="table-wrap">
            <table>
              <thead><tr><th>결함ID</th><th>결함명</th><th>담당자</th><th>상태</th><th>내용</th></tr></thead>
              <tbody>
                <tr v-for="defect in defects" :key="defect.id">
                  <td>DEF-{{ defect.id }}</td>
                  <td>{{ defect.title }}</td>
                  <td>{{ defect.assignee }}</td>
                  <td><span class="badge" :class="statusClass(defect.status)">{{ defect.statusLabel || statusText(defect.status) }}</span></td>
                  <td>{{ defect.description }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else-if="['globalHistory','projectHistory'].includes(page)">
          <div class="timeline card">
            <div v-for="req in requirements" :key="'r'+req.id" class="tl-item"><b>요구사항 변경</b><p class="muted">{{ req.title }} · {{ req.statusLabel }}</p></div>
            <div v-for="task in visibleWbsTasks" :key="'w'+task.id" class="tl-item"><b>WBS 변경</b><p class="muted">{{ task.taskName }} · {{ task.statusLabel }}</p></div>
            <div v-for="test in testCases" :key="'t'+test.id" class="tl-item"><b>테스트 변경</b><p class="muted">{{ test.caseName }} · {{ test.resultLabel }}</p></div>
          </div>
        </section>

        <section v-else>
          <div class="grid cols-2">
            <div class="card">
              <h3 class="card-title">{{ meta.title }}</h3>
              <p class="muted">{{ meta.desc }}</p>
              <div class="notice">이 화면은 원본 HTML POC 메뉴 구조를 Vue로 변환한 화면입니다. 주요 CRUD는 프로젝트 등록, 요구사항, WBS, 테스트 화면에 연결되어 있습니다.</div>
            </div>
            <div class="card">
              <h3 class="card-title">프로젝트 목록</h3>
              <div v-for="project in projects.slice(0, 4)" :key="project.id" class="mini-card">
                <strong>{{ project.name }}</strong>
                <p class="muted">{{ project.department }} · {{ project.requester }}</p>
              </div>
            </div>
          </div>
        </section>
      </section>
    </main>

    <div class="modal-backdrop" :class="{ show: projectSearchOpen }" @click.self="projectSearchOpen = false">
      <div class="modal" :class="{ show: projectSearchOpen }">
        <div class="modal-head"><h3>프로젝트 검색</h3><button class="close" @click="projectSearchOpen = false">×</button></div>
        <div class="modal-body">
          <div class="field"><input v-model="searchKeyword" placeholder="프로젝트명, 프로젝트ID, 요청자명을 입력하세요." /></div>
          <div v-if="!searchKeyword" class="notice" style="margin-top:14px">최근 조회 프로젝트</div>
          <button
            v-for="project in searchKeyword ? searchedProjects : projects.slice(0, 5)"
            :key="project.id"
            class="mini-card block-btn"
            @click="selectFromModal(project.id)"
          >
            <strong>{{ project.projectCode }}</strong>
            <span>{{ project.name }} ({{ project.requester }})</span>
          </button>
        </div>
      </div>
    </div>

    <div class="modal-backdrop" :class="{ show: myProjectOpen }" @click.self="myProjectOpen = false">
      <div class="modal" :class="{ show: myProjectOpen }">
        <div class="modal-head"><h3>내 프로젝트</h3><button class="close" @click="myProjectOpen = false">×</button></div>
        <div class="modal-body">
          <button class="btn primary" @click="go('projectRegister'); myProjectOpen = false">+ 프로젝트 등록</button>
          <button
            v-for="project in progressProjects"
            :key="project.id"
            class="mini-card block-btn"
            @click="selectFromMyProject(project.id)"
          >
            <strong>{{ project.name }}</strong>
            <span class="muted">{{ project.projectCode }} · {{ project.statusLabel || statusText(project.status) }}</span>
          </button>
          <button class="btn" style="width:100%;margin-top:12px" @click="go('projectStatus'); myProjectOpen = false">모든 프로젝트 보기</button>
        </div>
      </div>
    </div>

    <div class="modal-backdrop" :class="{ show: noticeOpen }" @click.self="noticeOpen = false">
      <div class="modal" :class="{ show: noticeOpen }">
        <div class="modal-head"><h3>프로젝트 알림</h3><button class="close" @click="noticeOpen = false">×</button></div>
        <div class="modal-body">
          <div class="tabs"><button class="tab active">프로젝트 알림</button><button class="tab">변경 알림</button><button class="tab">승인 알림</button></div>
          <article class="mini-card"><strong>[마감안내]</strong><p>WBS 작업의 마감일이 2일 남았습니다.</p><button class="btn small" @click="go('wbs'); noticeOpen = false">바로가기</button></article>
          <article class="mini-card"><strong>[배정안내]</strong><p>프로젝트의 개발 담당자로 배정되었습니다.</p><button class="btn small" @click="go('wbs'); noticeOpen = false">바로가기</button></article>
        </div>
      </div>
    </div>

    <div class="modal-backdrop" :class="{ show: scheduleOpen && !!scheduleTask }" @click.self="closeSchedule">
      <div class="modal schedule-modal" :class="{ show: scheduleOpen && !!scheduleTask }">
        <template v-if="scheduleTask">
          <div class="modal-head">
            <h3>일정 관리</h3>
            <button class="close" @click="closeSchedule">×</button>
          </div>

          <div class="modal-body">
            <div class="schedule-table-wrap">
              <table class="schedule-info-table">
                <thead>
                  <tr>
                    <th>시스템/업무구분</th>
                    <th>화면/메뉴명</th>
                    <th>업무 ID</th>
                    <th>화면(메뉴)명</th>
                    <th>업무유형 / 담당자</th>
                    <th>공정률</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>HIMS &gt; 공통/API</td>
                    <td>프로젝트 관리</td>
                    <td>WBS-{{ scheduleTask.id }}</td>
                    <td>{{ scheduleTask.taskName }}</td>
                    <td>{{ scheduleTask.workTypeLabel }} / {{ scheduleTask.assignee }}</td>
                    <td>{{ scheduleTask.progressRate }}%</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="form-section">
              <h3 class="section-title">
                일정 관리
                <button
                  v-if="hasPlanSchedule(scheduleTask)"
                  class="btn small primary"
                  @click="scheduleChangeMode = !scheduleChangeMode"
                >
                  일정변경 요청
                </button>
              </h3>

              <div class="notice schedule-notice">
                <ul>
                  <li>계획일 등록되어야 실행일정(착수/완료)을 체크할 수 있습니다.</li>
                  <li>계획일 도래 전에는 계획일정을 수정할 수 있고, 계획일 도래 후에는 일정변경 요청을 통해 변경할 수 있습니다.</li>
                  <li>완료 처리 후에는 일정변경 요청 버튼이 비활성화됩니다.</li>
                </ul>
              </div>

              <template v-if="!hasPlanSchedule(scheduleTask)">
                <div class="form-grid schedule-register-grid">
                  <div class="field"><label>계획일정</label><input v-model="scheduleForm.planStartDate" type="date" /></div>
                  <div class="field"><label>&nbsp;</label><input v-model="scheduleForm.planEndDate" type="date" /></div>
                </div>
                <div class="modal-actions center">
                  <button class="btn primary" @click="saveSchedule">저장</button>
                </div>
              </template>

              <template v-else>
                <div class="schedule-plan-line">
                  <span class="label">계획일정</span>
                  <input v-model="scheduleForm.planStartDate" type="date" :readonly="isPlanLocked(scheduleTask)" />
                  <span>~</span>
                  <input v-model="scheduleForm.planEndDate" type="date" :readonly="isPlanLocked(scheduleTask)" />
                  <span class="label">실행일정</span>
                  <input :value="scheduleTask.actualStartDate || ''" readonly placeholder="미착수" />
                  <span>~</span>
                  <input :value="scheduleTask.actualEndDate || ''" readonly placeholder="미완료" />
                  <button
                    v-if="!scheduleTask.actualStartDate"
                    class="btn small"
                    @click="startTask(scheduleTask.id)"
                  >
                    착수
                  </button>
                  <button
                    v-else
                    class="btn small primary"
                    @click="completeTask(scheduleTask.id)"
                  >
                    완료
                  </button>
                </div>

                <div v-if="scheduleChangeMode" class="form-section schedule-change-area">
                  <h3 class="section-title">일정 변경</h3>
                  <div class="notice">
                    일정 변경 요청을 등록하면 결재/승인 후 계획일정이 변경됩니다.
                  </div>
                  <div class="form-grid">
                    <div class="field"><label>변경 일정</label><input v-model="scheduleExtra.changeStartDate" type="date" /></div>
                    <div class="field"><label>&nbsp;</label><input v-model="scheduleExtra.changeEndDate" type="date" /></div>
                    <div class="field full-span">
                      <label>변경 사유</label>
                      <textarea v-model="scheduleExtra.changeReason" maxlength="500" placeholder="일정 변경 사유를 입력하세요."></textarea>
                    </div>
                    <div class="field"><label>결재선</label><select v-model="scheduleExtra.approverType"><option>담당자협의중</option><option>허기진책임</option></select></div>
                    <div class="field"><label>승인자</label><select v-model="scheduleExtra.approver"><option>허기진책임</option><option>김현대</option><option>이지윤</option></select></div>
                  </div>
                  <div class="modal-actions center">
                    <button class="btn primary" @click="requestScheduleChange">승인요청</button>
                  </div>
                </div>
              </template>
            </div>

            <div v-if="hasPlanSchedule(scheduleTask)" class="form-section">
              <h3 class="section-title">추가 정보</h3>
              <div class="form-grid">
                <div class="field">
                  <label>단위테스트 사용</label>
                  <select v-model="scheduleExtra.unitTestEnabled">
                    <option :value="true">사용</option>
                    <option :value="false">미사용</option>
                  </select>
                </div>
                <div class="field">
                  <label>확정여부</label>
                  <select v-model="scheduleExtra.confirmed">
                    <option :value="false">OFF</option>
                    <option :value="true">ON</option>
                  </select>
                </div>
                <div class="field">
                  <label>우선순위</label>
                  <select v-model="scheduleExtra.priority">
                    <option>상</option>
                    <option>중</option>
                    <option>하</option>
                  </select>
                </div>
                <div class="field">
                  <label>난이도</label>
                  <select v-model="scheduleExtra.difficulty">
                    <option>높음</option>
                    <option>중</option>
                    <option>낮음</option>
                  </select>
                </div>
                <div class="field full-span">
                  <label>비고</label>
                  <textarea v-model="scheduleForm.note" maxlength="500" placeholder="개발내용/작업이슈"></textarea>
                </div>
              </div>
              <div class="modal-actions center">
                <button class="btn primary" @click="saveSchedule">저장</button>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>

    <div class="modal-backdrop confirm-backdrop" :class="{ show: scheduleConfirmOpen }" @click.self="scheduleConfirmOpen = false">
      <div class="modal confirm-modal" :class="{ show: scheduleConfirmOpen }">
        <div class="modal-body">
          <p>{{ scheduleConfirmMessage }}</p>
          <div class="modal-actions center">
            <button class="btn primary" @click="confirmScheduleChange">확인</button>
            <button class="btn" @click="scheduleConfirmOpen = false">취소</button>
          </div>
        </div>
      </div>
    </div>

    <div class="modal-backdrop" :class="{ show: taskModalOpen && !!selectedTask }" @click.self="taskModalOpen = false">
      <div class="modal" :class="{ show: taskModalOpen && !!selectedTask }">
        <template v-if="selectedTask">
          <div class="modal-head"><h3>WBS 상세</h3><button class="close" @click="taskModalOpen = false">×</button></div>
          <div class="modal-body">
            <h2>{{ selectedTask.taskName }}</h2>
            <p class="muted">{{ projectName(selectedTask.projectId) }}</p>
            <div class="grid cols-2">
              <div class="notice">담당자: {{ selectedTask.assignee }}</div>
              <div class="notice">상태: {{ selectedTask.statusLabel || statusText(selectedTask.status) }}</div>
              <div class="notice">계획일: {{ selectedTask.planStartDate || '-' }} ~ {{ selectedTask.planEndDate || '-' }}</div>
              <div class="notice">공정률: {{ selectedTask.progressRate }}%</div>
            </div>
            <div class="modal-actions">
              <button class="btn" @click="openSchedule(selectedTask)">일정관리</button>
              <button class="btn" @click="startTask(selectedTask.id)">착수</button>
              <button class="btn primary" @click="completeTask(selectedTask.id)">완료</button>
            </div>
          </div>
        </template>
      </div>
    </div>

    <div class="modal-backdrop" :class="{ show: moreOpen }" @click.self="moreOpen = false">
      <div class="modal" :class="{ show: moreOpen }">
        <div class="modal-head"><h3>{{ moreDateLabel }} 내 업무 ({{ moreTasks.length }}건)</h3><button class="close" @click="moreOpen = false">×</button></div>
        <div class="modal-body">
          <button
            v-for="task in moreTasks"
            :key="task.id"
            class="mini-card block-btn"
            @click="openTask(task)"
          >
            <strong>{{ task.taskName }} (~ {{ md(task.planEndDate) }})</strong>
            <span class="muted">{{ projectName(task.projectId) }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type {
  Defect,
  Project,
  ProjectCreatePayload,
  ProjectStatus,
  Requirement,
  RequirementCreatePayload,
  ScheduleUpdatePayload,
  TestCase,
  TestResult,
  WbsTask
} from '~/types/pms'
import { usePmsService } from '~/services/pmsService'
import { defineComponent, h, type PropType } from 'vue'

type PageKey =
  | 'overview' | 'login' | 'reset' | 'dashboard' | 'resource' | 'performance' | 'mywork'
  | 'projectRegister' | 'projectStatus' | 'globalHistory' | 'testLibrary' | 'userAdmin'
  | 'approval' | 'programAdmin' | 'codeAdmin' | 'projectInfo' | 'projectHistory'
  | 'requirements' | 'wbs' | 'unitTest' | 'devScenario' | 'devDefect' | 'devProgress'
  | 'opsScenario' | 'opsDefect' | 'opsProgress' | 'projectDashboard'

const service = usePmsService()

const navGroups = [
  {
    title: '공통',
    items: [
      { key: 'overview' as PageKey, label: 'POC 개요', icon: '🏠', count: 'Vue' },
      { key: 'login' as PageKey, label: '로그인', icon: '🔐' },
      { key: 'reset' as PageKey, label: '비밀번호 재설정', icon: '🔑' }
    ]
  },
  {
    title: '대시보드',
    items: [
      { key: 'dashboard' as PageKey, label: '전체 현황', icon: '📊' },
      { key: 'resource' as PageKey, label: '테크 리소스', icon: '👥' },
      { key: 'performance' as PageKey, label: '실적 관리', icon: '📈' },
      { key: 'mywork' as PageKey, label: '내업무', icon: '🗓️', count: 'POC' }
    ]
  },
  {
    title: '통합관리',
    items: [
      { key: 'projectRegister' as PageKey, label: '프로젝트 등록', icon: '➕' },
      { key: 'projectStatus' as PageKey, label: '프로젝트 현황', icon: '📁' },
      { key: 'globalHistory' as PageKey, label: '프로젝트 변경이력', icon: '🧾' },
      { key: 'testLibrary' as PageKey, label: '테스트 라이브러리', icon: '🧪' },
      { key: 'userAdmin' as PageKey, label: '사용자 관리', icon: '🙋' },
      { key: 'approval' as PageKey, label: '신청 승인 관리', icon: '✅' },
      { key: 'programAdmin' as PageKey, label: '화면/프로그램 관리', icon: '🧭' },
      { key: 'codeAdmin' as PageKey, label: '공통코드 관리', icon: '🏷️' }
    ]
  },
  {
    title: '프로젝트 관리',
    items: [
      { key: 'projectInfo' as PageKey, label: '프로젝트 정보', icon: 'ℹ️' },
      { key: 'projectHistory' as PageKey, label: '프로젝트 변경이력', icon: '🔁' },
      { key: 'requirements' as PageKey, label: '요구사항 관리', icon: '📝' },
      { key: 'wbs' as PageKey, label: 'WBS 관리', icon: '🧩' },
      { key: 'unitTest' as PageKey, label: '단위테스트', icon: '🧪' },
      { key: 'devScenario' as PageKey, label: 'DEV 시나리오', icon: '↳', sub: true },
      { key: 'devDefect' as PageKey, label: 'DEV 결함관리', icon: '↳', sub: true },
      { key: 'devProgress' as PageKey, label: 'DEV 진척관리', icon: '↳', sub: true },
      { key: 'opsScenario' as PageKey, label: '운영 시나리오', icon: '↳', sub: true },
      { key: 'opsDefect' as PageKey, label: '운영 결함관리', icon: '↳', sub: true },
      { key: 'opsProgress' as PageKey, label: '운영 진척관리', icon: '↳', sub: true },
      { key: 'projectDashboard' as PageKey, label: '프로젝트 진척 대시보드', icon: '📌' }
    ]
  }
]

const pages: Record<PageKey, { title: string; crumb: string; desc: string; type: '통합' | '개별' }> = {
  overview: { title: 'POC 개요', crumb: '전사 프로젝트 관리 시스템 > 전체 SB POC', desc: '기존 HTML SB POC UI를 Vue/Nuxt 구조로 변환하고 Spring Boot API와 연결했습니다.', type: '통합' },
  login: { title: '로그인', crumb: '로그인/진입화면', desc: '임직원 사번 및 외주 ID 로그인을 가정한 화면입니다.', type: '통합' },
  reset: { title: '비밀번호 재설정', crumb: '로그인 > 비밀번호 재설정', desc: '인증번호 확인 후 신규 비밀번호 입력 영역이 열리는 구조입니다.', type: '통합' },
  dashboard: { title: '전체 프로젝트 현황', crumb: '대시보드 > 전체 현황', desc: '연간 전체 프로젝트 진행현황을 KPI와 상태별 차트로 확인합니다.', type: '통합' },
  resource: { title: '테크 리소스 관리', crumb: '대시보드 > 테크 리소스 관리', desc: '부서/담당자/프로젝트별 투입 현황과 지연 리스크를 확인합니다.', type: '통합' },
  performance: { title: '테크 실적관리', crumb: '대시보드 > 실적관리', desc: '완료 프로젝트, 담당자별 처리 건수, 테스트/결함 실적을 집계합니다.', type: '통합' },
  mywork: { title: '내업무', crumb: '통합관리 > 내업무', desc: '배정된 프로젝트와 WBS 업무를 카드형/캘린더형으로 확인합니다.', type: '통합' },
  projectRegister: { title: '프로젝트 등록', crumb: '통합관리 > 프로젝트 등록', desc: 'JIRA 연계 또는 PMS 독립관리 기반으로 프로젝트 기본정보와 담당자를 등록합니다.', type: '통합' },
  projectStatus: { title: '프로젝트 현황', crumb: '통합관리 > 프로젝트 현황', desc: '전체 프로젝트 목록과 진행상태, 공정률, 오픈예정일을 조회합니다.', type: '통합' },
  globalHistory: { title: '프로젝트 변경이력', crumb: '통합관리 > 프로젝트 현황 > 프로젝트 변경이력', desc: '전체 프로젝트의 요구사항/WBS/테스트 변경사항을 통합 조회합니다.', type: '통합' },
  testLibrary: { title: '테스트 라이브러리', crumb: '통합관리 > 테스트 라이브러리', desc: '반복 사용 가능한 테스트 케이스를 시스템/업무/화면 단위로 관리합니다.', type: '통합' },
  userAdmin: { title: '사용자 관리', crumb: '통합관리 > 시스템 관리 > 사용자 관리', desc: '임직원/외주 사용자 상태와 권한을 관리합니다.', type: '통합' },
  approval: { title: '신청 승인 관리', crumb: '통합관리 > 시스템 관리 > 신청 승인 관리', desc: 'WBS 일정변경, 담당자 변경 등 승인요청을 처리합니다.', type: '통합' },
  programAdmin: { title: '화면/프로그램 관리', crumb: '통합관리 > 시스템 관리 > 화면(메뉴) 관리', desc: '시스템, 업무구분, 메뉴경로, 화면명을 기준정보로 관리합니다.', type: '통합' },
  codeAdmin: { title: '공통코드 관리', crumb: '통합관리 > 시스템 관리 > 공통코드 관리', desc: '업무유형, 처리상태, 테스트상태 등 공통코드를 관리합니다.', type: '통합' },
  projectInfo: { title: '프로젝트 정보', crumb: '프로젝트 관리 > 프로젝트 정보', desc: '선택 프로젝트의 기본정보, 담당자, 테스트 사용여부를 확인/수정합니다.', type: '개별' },
  projectHistory: { title: '프로젝트 변경이력', crumb: '프로젝트 관리 > 프로젝트 변경이력', desc: '개별 프로젝트의 요구사항/WBS/테스트 변경사항을 확인합니다.', type: '개별' },
  requirements: { title: '요구사항 관리', crumb: '프로젝트 관리 > 요구사항 관리', desc: '요구사항에 프로그램과 업무유형을 매핑하고 확정 시 WBS 생성을 유도합니다.', type: '개별' },
  wbs: { title: 'WBS 관리', crumb: '프로젝트 관리 > WBS 관리', desc: '요구사항 기반 WBS 작업단위, 담당자, 계획/실행 일정, 공정률을 관리합니다.', type: '개별' },
  unitTest: { title: '단위테스트', crumb: '프로젝트 관리 > 단위테스트', desc: 'WBS 기준 단위테스트 케이스와 수행 결과를 관리합니다.', type: '개별' },
  devScenario: { title: 'DEV 테스트 시나리오', crumb: '프로젝트 관리 > DEV테스트 > 시나리오 관리', desc: '테스트 대상 불러오기/개별등록/일괄등록 방식으로 DEV 테스트케이스를 구성합니다.', type: '개별' },
  devDefect: { title: 'DEV 테스트 결함관리', crumb: '프로젝트 관리 > DEV테스트 > 결함관리', desc: '오류 발생 케이스의 결함 상태와 조치 결과를 관리합니다.', type: '개별' },
  devProgress: { title: 'DEV 테스트 진척관리', crumb: '프로젝트 관리 > DEV테스트 > 진척관리', desc: '차수별 테스트 정상/오류/미수행 현황을 집계합니다.', type: '개별' },
  opsScenario: { title: '운영 테스트 시나리오', crumb: '프로젝트 관리 > 운영테스트 > 시나리오 관리', desc: '운영 테스트 시나리오와 참고사항을 관리합니다.', type: '개별' },
  opsDefect: { title: '운영 테스트 결함관리', crumb: '프로젝트 관리 > 운영테스트 > 결함관리', desc: '운영 테스트 결함 접수, 조치, 재처리 요청 상태를 관리합니다.', type: '개별' },
  opsProgress: { title: '운영 테스트 진척관리', crumb: '프로젝트 관리 > 운영테스트 > 진척관리', desc: '운영테스트 차수별 진척률과 결함 추이를 확인합니다.', type: '개별' },
  projectDashboard: { title: '프로젝트 진척 대시보드', crumb: '프로젝트 관리 > 대시보드', desc: '개별 프로젝트의 요구사항, WBS, 테스트, 결함 진행률을 한 화면에서 확인합니다.', type: '개별' }
}

const page = ref<PageKey>('mywork')
const meta = computed(() => pages[page.value])
const sidebarOpen = ref(false)
const assigned = ref(true)
const myworkView = ref<'card' | 'cal'>('card')
const assignee = ref('이지윤')
const keyword = ref('')
const searchKeyword = ref('')
const openTabs = ref([
  { id: 'mywork' as PageKey, title: '내업무', type: '통합' as const }
])

const projects = ref<Project[]>([])
const requirements = ref<Requirement[]>([])
const wbsTasks = ref<WbsTask[]>([])
const myTasks = ref<WbsTask[]>([])
const testCases = ref<TestCase[]>([])
const defects = ref<Defect[]>([])

const selectedProjectId = ref<number | null>(null)
const selectedTask = ref<WbsTask | null>(null)
const calendarDate = ref(new Date())
const today = new Date()
today.setHours(0, 0, 0, 0)

const projectSearchOpen = ref(false)
const myProjectOpen = ref(false)
const noticeOpen = ref(false)
const scheduleOpen = ref(false)
const taskModalOpen = ref(false)
const moreOpen = ref(false)
const moreTasks = ref<WbsTask[]>([])
const moreDateLabel = ref('')
const toastMessage = ref('')
const apiError = ref('')

const projectForm = ref<ProjectCreatePayload>({
  projectCode: `PJ-${Date.now().toString().slice(-6)}`,
  name: '신규 PMS 개선 프로젝트',
  department: '웹기획팀',
  requester: '이지윤',
  status: 'RECEIVED',
  openDueDate: new Date(Date.now() + 1000 * 60 * 60 * 24 * 14).toISOString().slice(0, 10),
  description: 'Vue 화면에서 등록한 프로젝트입니다.'
})

const requirementForm = ref<RequirementCreatePayload>({
  title: 'Vue 전환 요구사항',
  systemType: 'HIMS',
  businessType: '공통/API',
  menuName: '내업무',
  description: '기존 HTML POC를 Vue/Nuxt 구조로 전환합니다.'
})

const scheduleForm = ref<ScheduleUpdatePayload>({
  planStartDate: '',
  planEndDate: '',
  note: ''
})

const scheduleChangeMode = ref(false)
const scheduleConfirmOpen = ref(false)
const scheduleConfirmMessage = ref('')
const scheduleExtra = ref({
  unitTestEnabled: true,
  confirmed: false,
  priority: '상',
  difficulty: '중',
  changeStartDate: '',
  changeEndDate: '',
  changeReason: '',
  approverType: '담당자협의중',
  approver: '허기진책임'
})

const emptyKpis = ['진행 프로젝트', '내 할 일', '대기', '금주 마감', '지연']

const selectedProject = computed(() => projects.value.find(project => project.id === selectedProjectId.value) || projects.value[0] || null)

const isMine = (task: WbsTask) => {
  return String(task.assignee || '').trim() === assignee.value.trim()
}

const mergeTasks = (a: WbsTask[], b: WbsTask[]) => {
  const map = new Map<number, WbsTask>()
  ;[...a, ...b].forEach(task => map.set(task.id, task))
  return Array.from(map.values())
}

/**
 * WBS 관리 화면에서는 프로젝트의 전체 WBS를 보여줘도 되지만,
 * 내업무 화면에서는 반드시 로그인/선택 사용자 담당 업무만 보여야 한다.
 */
const visibleWbsTasks = computed(() => {
  return wbsTasks.value
})

const visibleMyTasks = computed(() => {
  return mergeTasks(myTasks.value, wbsTasks.value.filter(isMine)).filter(isMine)
})

const myProjectIds = computed(() => {
  return new Set(visibleMyTasks.value.map(task => task.projectId))
})

const progressProjects = computed(() => {
  return projects.value.filter(project =>
    myProjectIds.value.has(project.id) &&
    ['DISCUSSING', 'IN_PROGRESS', 'TESTING'].includes(project.status)
  )
})

const waitingProjects = computed(() => {
  return projects.value.filter(project =>
    myProjectIds.value.has(project.id) &&
    project.status === 'RECEIVED'
  )
})

const sortedMyTasks = computed(() => {
  return [...visibleMyTasks.value].sort((a, b) => {
    if (!a.planEndDate) return 1
    if (!b.planEndDate) return -1
    return a.planEndDate.localeCompare(b.planEndDate)
  })
})

const scheduledTasks = computed(() => visibleMyTasks.value.filter(task => task.planStartDate && task.planEndDate))
const unscheduledTasks = computed(() => visibleMyTasks.value.filter(task => !task.planStartDate || !task.planEndDate))
const registerableTasks = computed(() => {
  const unscheduled = unscheduledTasks.value.filter(task => task.status !== 'COMPLETED')
  if (unscheduled.length) return unscheduled

  return visibleMyTasks.value.filter(task => task.status !== 'COMPLETED')
})
const delayedTasks = computed(() => visibleMyTasks.value.filter(task => isDelayed(task)))
const weekDueTasks = computed(() => visibleMyTasks.value.filter(task => isThisWeek(task.planEndDate)))

const filteredProjects = computed(() => {
  const word = keyword.value.trim()
  if (!word) return projects.value
  return projects.value.filter(project =>
    project.name.includes(word) ||
    project.projectCode.includes(word) ||
    project.department.includes(word) ||
    project.requester.includes(word)
  )
})

const searchedProjects = computed(() => {
  const word = searchKeyword.value.trim()
  if (!word) return []
  return projects.value.filter(project =>
    project.name.includes(word) ||
    project.projectCode.includes(word) ||
    project.requester.includes(word)
  )
})

const filteredTests = computed(() => {
  if (page.value === 'unitTest') return testCases.value.filter(test => test.stage === 'UNIT')
  if (page.value === 'devScenario') return testCases.value.filter(test => test.stage === 'DEV')
  if (page.value === 'opsScenario') return testCases.value.filter(test => test.stage === 'OPERATION')
  return testCases.value
})

const firstWbs = computed(() => visibleWbsTasks.value[0] || null)
const scheduleTask = computed(() => selectedTask.value || firstWbs.value)

const calendarCells = computed(() => {
  const year = calendarDate.value.getFullYear()
  const month = calendarDate.value.getMonth()
  const first = new Date(year, month, 1)
  const start = new Date(year, month, 1 - first.getDay())

  return Array.from({ length: 42 }, (_, index) => {
    const date = new Date(start)
    date.setDate(start.getDate() + index)
    const iso = toIso(date)

    const tasks = scheduledTasks.value.filter(task => isTaskOnDate(task, date))

    return {
      iso,
      date,
      tasks,
      current: date.getMonth() === month,
      today: iso === toIso(today)
    }
  })
})

const calendarSegments = computed(() => {
  const cells = calendarCells.value
  if (!cells.length) return []

  const segments: any[] = []
  const lineByWeek: Record<number, number> = {}

  scheduledTasks.value.forEach(task => {
    const range = taskDateRange(task)
    if (!range) return

    for (let week = 0; week < 6; week += 1) {
      const weekStart = cells[week * 7].date
      const weekEnd = cells[week * 7 + 6].date

      if (range.end < weekStart || range.start > weekEnd) continue

      const start = range.start > weekStart ? range.start : weekStart
      const end = range.end < weekEnd ? range.end : weekEnd
      const startCol = start.getDay() + 1
      const span = dayDiff(start, end) + 1

      const line = lineByWeek[week] || 0
      lineByWeek[week] = line + 1

      if (line < 3) {
        segments.push({
          key: `${task.id}-${week}-${startCol}`,
          task,
          style: {
            gridColumn: `${startCol} / span ${span}`,
            gridRow: `${week + 1}`,
            marginTop: `${30 + line * 25}px`
          }
        })
      }
    }
  })

  return segments
})

const calendarMoreButtons = computed(() => {
  return calendarCells.value
    .map((cell, index) => {
      if (cell.tasks.length <= 3) return null
      const week = Math.floor(index / 7)
      const col = (index % 7) + 1
      return {
        key: `more-${cell.iso}`,
        iso: cell.iso,
        tasks: cell.tasks,
        count: cell.tasks.length - 3,
        style: {
          gridColumn: `${col}`,
          gridRow: `${week + 1}`,
          marginTop: '104px'
        }
      }
    })
    .filter(Boolean)
})

const go = async (target: PageKey) => {
  page.value = target
  addTab(target)
  sidebarOpen.value = false

  if (isProjectPage(target) && !selectedProjectId.value && projects.value[0]) {
    selectedProjectId.value = projects.value[0].id
  }

  if (isProjectPage(target)) {
    await loadProjectChildren()
  }
}

const addTab = (target: PageKey) => {
  if (!openTabs.value.some(tab => tab.id === target)) {
    openTabs.value.push({ id: target, title: pages[target].title, type: pages[target].type })
  }
}

const isProjectPage = (target: PageKey) => {
  return ['projectInfo','projectHistory','requirements','wbs','unitTest','devScenario','devDefect','devProgress','opsScenario','opsDefect','opsProgress','projectDashboard'].includes(target)
}

const refreshAll = async () => {
  apiError.value = ''
  try {
    await loadProjects()
    await loadMyWork()
    await loadProjectChildren()
    toast('조회 완료')
  } catch (error: any) {
    apiError.value = error?.message || '조회 중 오류가 발생했습니다.'
  }
}

const loadProjects = async () => {
  projects.value = await service.projects(keyword.value).catch(() => [])
  if (!selectedProjectId.value && projects.value.length) {
    selectedProjectId.value = projects.value[0].id
  }
}

const loadMyWork = async () => {
  const list = await service.myWbs(assignee.value).catch(() => [])
  myTasks.value = list.filter(isMine)
  syncCalendarMonth()
}

const loadProjectChildren = async () => {
  if (!selectedProjectId.value) return

  const projectId = selectedProjectId.value
  const [reqList, wbsList, testList, defectList] = await Promise.all([
    service.requirements(projectId).catch(() => []),
    service.wbs(projectId).catch(() => []),
    service.tests(projectId).catch(() => []),
    service.defects(projectId).catch(() => [])
  ])

  requirements.value = reqList
  wbsTasks.value = wbsList
  testCases.value = testList
  defects.value = defectList

  if (!myTasks.value.length && wbsList.length) {
    myTasks.value = wbsList.filter(isMine)
  }

  syncCalendarMonth()
}

const selectProject = async (projectId: number) => {
  selectedProjectId.value = projectId
  await loadProjectChildren()
  await go('projectInfo')
}

const selectFromModal = async (projectId: number) => {
  projectSearchOpen.value = false
  await selectProject(projectId)
}

const selectFromMyProject = async (projectId: number) => {
  myProjectOpen.value = false
  await selectProject(projectId)
}

const saveProject = async () => {
  apiError.value = ''
  try {
    const created = await service.createProject(projectForm.value)
    projects.value.unshift(created)
    selectedProjectId.value = created.id
    projectForm.value.projectCode = `PJ-${Date.now().toString().slice(-6)}`
    toast('프로젝트가 등록되었습니다.')
    await go('projectStatus')
  } catch (error: any) {
    apiError.value = error?.message || '프로젝트 등록 실패'
  }
}

const saveRequirement = async () => {
  if (!selectedProjectId.value) {
    toast('프로젝트를 먼저 선택하세요.')
    return
  }

  apiError.value = ''
  try {
    const created = await service.createRequirement(selectedProjectId.value, requirementForm.value)
    requirements.value.unshift(created)
    toast('요구사항이 등록되었습니다.')
  } catch (error: any) {
    apiError.value = error?.message || '요구사항 등록 실패'
  }
}

const confirmRequirement = async (requirementId: number) => {
  apiError.value = ''
  try {
    const updated = await service.confirmRequirement(requirementId)
    requirements.value = requirements.value.map(item => item.id === updated.id ? updated : item)
    await loadProjectChildren()
    toast('요구사항이 확정되었습니다.')
  } catch (error: any) {
    apiError.value = error?.message || '요구사항 확정 실패'
  }
}

const openTask = (task: WbsTask) => {
  selectedTask.value = task
  taskModalOpen.value = true
}

const openSchedule = (task: WbsTask | null | undefined) => {
  if (!task) {
    toast('일정 관리할 WBS가 없습니다.')
    return
  }

  selectedTask.value = task
  scheduleForm.value = {
    planStartDate: task.planStartDate || '',
    planEndDate: task.planEndDate || '',
    note: task.note || ''
  }
  scheduleExtra.value.changeStartDate = task.planStartDate || ''
  scheduleExtra.value.changeEndDate = task.planEndDate || ''
  scheduleExtra.value.changeReason = ''
  scheduleChangeMode.value = false
  taskModalOpen.value = false
  scheduleOpen.value = true
}

const closeSchedule = () => {
  scheduleOpen.value = false
}

const hasPlanSchedule = (task: WbsTask | null | undefined) => {
  return !!(task?.planStartDate && task?.planEndDate)
}

const isPlanLocked = (task: WbsTask | null | undefined) => {
  const start = parseDate(task?.planStartDate || null)
  if (!start) return false
  return today >= start
}

const requestScheduleChange = () => {
  if (!scheduleExtra.value.changeStartDate || !scheduleExtra.value.changeEndDate) {
    toast('변경 시작일 또는 변경 종료일을 입력하세요.')
    return
  }

  scheduleConfirmMessage.value = `${md(scheduleExtra.value.changeStartDate)} ~ ${md(scheduleExtra.value.changeEndDate)}로 수정하시겠습니까?`
  scheduleConfirmOpen.value = true
}

const confirmScheduleChange = async () => {
  if (!scheduleTask.value) return

  scheduleForm.value.planStartDate = scheduleExtra.value.changeStartDate
  scheduleForm.value.planEndDate = scheduleExtra.value.changeEndDate
  scheduleForm.value.note = scheduleExtra.value.changeReason || scheduleForm.value.note

  scheduleConfirmOpen.value = false
  await saveSchedule()
  scheduleChangeMode.value = false
}


const saveSchedule = async () => {
  if (!scheduleTask.value) return

  if (!scheduleForm.value.planStartDate || !scheduleForm.value.planEndDate) {
    toast('계획 시작일 또는 계획 종료일을 입력하세요.')
    return
  }

  apiError.value = ''
  try {
    const updated = await service.updateSchedule(scheduleTask.value.id, scheduleForm.value)
    replaceTask(updated)
    scheduleOpen.value = false
    toast('일정이 저장되었습니다.')
  } catch (error: any) {
    apiError.value = error?.message || '일정 저장 실패'
  }
}

const startTask = async (taskId: number) => {
  apiError.value = ''
  try {
    const updated = await service.startTask(taskId)
    replaceTask(updated)
    toast('착수 처리되었습니다.')
  } catch (error: any) {
    apiError.value = error?.message || '착수 처리 실패'
  }
}

const completeTask = async (taskId: number) => {
  apiError.value = ''
  try {
    const updated = await service.completeTask(taskId)
    replaceTask(updated)
    toast('완료 처리되었습니다.')
  } catch (error: any) {
    apiError.value = error?.message || '완료 처리 실패'
  }
}

const executeTest = async (testCaseId: number, result: TestResult) => {
  apiError.value = ''
  try {
    const updated = await service.executeTest(testCaseId, {
      result,
      actualResult: result === 'PASS' ? '정상 처리 확인' : '오류 발생 확인',
      tester: assignee.value
    })
    testCases.value = testCases.value.map(item => item.id === updated.id ? updated : item)
    toast(`테스트 결과가 ${updated.resultLabel || statusText(updated.result)}로 저장되었습니다.`)
  } catch (error: any) {
    apiError.value = error?.message || '테스트 결과 저장 실패'
  }
}

const replaceTask = (updated: WbsTask) => {
  myTasks.value = myTasks.value.map(task => task.id === updated.id ? updated : task)
  wbsTasks.value = wbsTasks.value.map(task => task.id === updated.id ? updated : task)
  if (!myTasks.value.some(task => task.id === updated.id)) myTasks.value.unshift(updated)
  if (!wbsTasks.value.some(task => task.id === updated.id)) wbsTasks.value.unshift(updated)
  if (selectedTask.value?.id === updated.id) selectedTask.value = updated
  syncCalendarMonth()
}

const routeTask = async (task: WbsTask) => {
  if (task.workType === 'PLANNING') await go('requirements')
  else if (['DESIGN', 'PUBLISHING', 'DEVELOPMENT'].includes(task.workType)) await go('wbs')
  else if (task.workType === 'UNIT_TEST') await go('unitTest')
  else if (task.workType === 'DEV_TEST') await go('devScenario')
  else if (task.workType === 'OPERATION_TEST') await go('opsScenario')
}

const moveMonth = (offset: number) => {
  calendarDate.value = new Date(calendarDate.value.getFullYear(), calendarDate.value.getMonth() + offset, 1)
}

const goToday = () => {
  calendarDate.value = new Date()
}

const openMore = (dateIso: string, tasks: WbsTask[]) => {
  moreDateLabel.value = md(dateIso)
  moreTasks.value = tasks
  moreOpen.value = true
}

const syncCalendarMonth = () => {
  const first = visibleMyTasks.value.find(task => task.planStartDate || task.planEndDate)
  const date = parseDate(first?.planStartDate || first?.planEndDate || null)
  if (date) {
    calendarDate.value = new Date(date.getFullYear(), date.getMonth(), 1)
  }
}

const projectName = (projectId: number) => {
  return projects.value.find(project => project.id === projectId)?.name || `프로젝트 ${projectId}`
}

const projectTasks = (projectId: number) => {
  return visibleMyTasks.value.filter(task => task.projectId === projectId)
}

const projectProgress = (project: Project) => {
  const tasks = projectTasks(project.id)

  if (!tasks.length) {
    return project.progressRate || 0
  }

  const total = tasks.reduce((sum, task) => sum + Number(task.progressRate || 0), 0)
  return Math.round(total / tasks.length)
}

const projectTaskCount = (projectId: number) => {
  return projectTasks(projectId).length
}

const projectDoneCount = (projectId: number) => {
  return projectTasks(projectId).filter(task => task.status === 'COMPLETED' || Number(task.progressRate || 0) >= 100).length
}

const isDelayed = (task: WbsTask) => {
  const diff = diffDay(task.planEndDate)
  return task.delayed || (diff !== null && diff < 0 && task.status !== 'COMPLETED')
}

const isThisWeek = (dateValue: string | null) => {
  const target = parseDate(dateValue)
  if (!target) return false
  const start = new Date(today)
  start.setDate(today.getDate() - today.getDay())
  const end = new Date(start)
  end.setDate(start.getDate() + 6)
  return target >= start && target <= end
}

const taskDateRange = (task: WbsTask) => {
  const start = parseDate(task.actualStartDate || task.planStartDate)
  const end = parseDate(task.actualEndDate || task.planEndDate)

  if (!start || !end) return null

  return start <= end ? { start, end } : { start: end, end: start }
}

const isTaskOnDate = (task: WbsTask, date: Date) => {
  const range = taskDateRange(task)
  if (!range) return false
  return date >= range.start && date <= range.end
}

const dayDiff = (start: Date, end: Date) => {
  const s = new Date(start.getFullYear(), start.getMonth(), start.getDate()).getTime()
  const e = new Date(end.getFullYear(), end.getMonth(), end.getDate()).getTime()
  return Math.round((e - s) / 86400000)
}

const parseDate = (dateValue: string | null) => {
  if (!dateValue) return null
  return new Date(`${dateValue}T00:00:00`)
}

const toIso = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const diffDay = (dateValue: string | null) => {
  const date = parseDate(dateValue)
  if (!date) return null
  return Math.ceil((date.getTime() - today.getTime()) / 86400000)
}

const dday = (dateValue: string | null) => {
  const diff = diffDay(dateValue)
  if (diff === null) return '일정 미등록'
  if (diff === 0) return 'D-Day'
  return diff > 0 ? `D-${diff}` : `D+${Math.abs(diff)}`
}

const md = (dateValue: string | null) => {
  const date = parseDate(dateValue)
  if (!date) return ''
  return `${date.getMonth() + 1}/${date.getDate()}`
}

const statusText = (status: string) => {
  const map: Record<string, string> = {
    RECEIVED: '접수',
    DISCUSSING: '협의중',
    IN_PROGRESS: '처리중',
    TESTING: '테스트',
    COMPLETED: '완료',
    CANCELED: '취소',
    READY: '대기',
    PAUSED: '일시중단',
    DRAFT: '검토중',
    CONFIRMED: '확정',
    REJECTED: '반려',
    NOT_RUN: '미수행',
    PASS: '정상',
    FAIL: '오류'
  }
  return map[status] || status
}

const statusClass = (status: string) => {
  if (['IN_PROGRESS', 'DISCUSSING', 'TESTING', 'PASS', 'CONFIRMED', 'COMPLETED'].includes(status)) return 'green'
  if (['RECEIVED', 'READY', 'DRAFT', 'NOT_RUN', 'PAUSED'].includes(status)) return 'gray'
  if (['FAIL', 'REJECTED', 'CANCELED'].includes(status)) return 'red'
  return 'amber'
}

const toast = (message: string) => {
  toastMessage.value = message
  window.setTimeout(() => {
    if (toastMessage.value === message) toastMessage.value = ''
  }, 2200)
}

onMounted(refreshAll)

const ProjectTable = defineComponent({
  props: {
    projects: { type: Array as PropType<Project[]>, required: true }
  },
  emits: ['select'],
  setup(props, { emit }) {
    return () => h('div', { class: 'table-wrap' }, [
      h('table', [
        h('thead', [
          h('tr', ['프로젝트ID','프로젝트명','요청자','처리상태','오픈예정일','공정률','상세'].map(head => h('th', head)))
        ]),
        h('tbody', props.projects.map(project => h('tr', { key: project.id }, [
          h('td', project.projectCode),
          h('td', [h('b', project.name)]),
          h('td', project.requester),
          h('td', [h('span', { class: ['badge', statusClass(project.status)] }, project.statusLabel || statusText(project.status))]),
          h('td', project.openDueDate || '미정'),
          h('td', [
            h('div', { class: 'progress-line' }, [
              h('div', { class: 'bar' }, [
                h('i', { style: { width: `${project.progressRate}%` } })
              ]),
              h('b', `${project.progressRate}%`)
            ])
          ]),
          h('td', [h('button', { class: 'btn small', onClick: () => emit('select', project.id) }, '상세')])
        ])))
      ])
    ])
  }
})

const WbsTable = defineComponent({
  props: {
    tasks: { type: Array as PropType<WbsTask[]>, required: true }
  },
  emits: ['open', 'schedule', 'start', 'complete'],
  setup(props, { emit }) {
    return () => h('div', { class: 'table-wrap' }, [
      h('table', [
        h('thead', [
          h('tr', ['WBS ID','업무명','업무유형','담당자','계획일','실행일','공정률','상태','처리'].map(head => h('th', head)))
        ]),
        h('tbody', props.tasks.map(task => h('tr', { key: task.id }, [
          h('td', `WBS-${task.id}`),
          h('td', [h('button', { class: 'link-button', onClick: () => emit('open', task) }, task.taskName)]),
          h('td', task.workTypeLabel),
          h('td', task.assignee),
          h('td', `${task.planStartDate || '-'} ~ ${task.planEndDate || '-'}`),
          h('td', `${task.actualStartDate || '-'} ~ ${task.actualEndDate || '-'}`),
          h('td', `${task.progressRate}%`),
          h('td', [h('span', { class: ['badge', isDelayed(task) ? 'red' : statusClass(task.status)] }, isDelayed(task) ? '지연' : (task.statusLabel || statusText(task.status)))]),
          h('td', { class: 'cell-actions' }, [
            h('button', { class: 'btn small', onClick: () => emit('schedule', task) }, '일정관리'),
            h('button', { class: 'btn small', onClick: () => emit('start', task.id) }, '착수'),
            h('button', { class: 'btn small primary', onClick: () => emit('complete', task.id) }, '완료')
          ])
        ])))
      ])
    ])
  }
})
</script>
