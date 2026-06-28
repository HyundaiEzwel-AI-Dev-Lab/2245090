<template>
  <div class="page">
    <div class="page-title">
      <div>
        <h1>프로젝트 현황</h1>
        <p>프로젝트명, ID, 요청자, 상태, 오픈월 기준으로 조회합니다.</p>
      </div>
      <BaseButton variant="primary" @click="showCreate = !showCreate">프로젝트 등록</BaseButton>
    </div>

    <BaseCard v-if="showCreate" title="프로젝트 등록" description="POC 기준 최소 항목만 등록합니다.">
      <div class="form-row">
        <input v-model="form.projectCode" class="input" placeholder="프로젝트 ID" />
        <input v-model="form.name" class="input" placeholder="프로젝트명" />
        <input v-model="form.department" class="input" placeholder="요청부서" />
        <input v-model="form.requester" class="input" placeholder="요청자" />
        <select v-model="form.status" class="select">
          <option value="RECEIVED">접수</option>
          <option value="DISCUSSING">협의중</option>
          <option value="IN_PROGRESS">처리중</option>
          <option value="TESTING">테스트</option>
        </select>
        <input v-model="form.openDueDate" class="input" type="date" />
        <input v-model="form.description" class="input" placeholder="설명" />
        <BaseButton variant="primary" @click="createProject">저장</BaseButton>
      </div>
    </BaseCard>

    <BaseCard title="검색조건">
      <div class="form-row">
        <input v-model="search.keyword" class="input" placeholder="프로젝트명/ID/요청자" @keyup.enter="loadProjects" />
        <select v-model="search.status" class="select">
          <option v-for="option in projectStatusOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
        </select>
        <input v-model="search.openMonth" class="input" type="month" />
        <div class="actions">
          <BaseButton variant="primary" @click="loadProjects">검색</BaseButton>
          <BaseButton @click="resetSearch">초기화</BaseButton>
        </div>
      </div>
    </BaseCard>

    <BaseCard :title="`검색 결과 ${projects.length}건`">
      <ProjectTable :projects="projects" />
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import { projectStatusOptions } from '~/utils/status'
import type { Project, ProjectStatus } from '~/types/pms'

const projectService = useProjectService()
const projects = ref<Project[]>([])
const showCreate = ref(false)
const search = reactive({ keyword: '', status: '' as ProjectStatus | '', openMonth: '' })
const form = reactive({ projectCode: '', name: '', department: '', requester: '', status: 'RECEIVED' as ProjectStatus, openDueDate: '', description: '' })

const loadProjects = async () => {
  projects.value = await projectService.searchProjects(search)
}

const resetSearch = async () => {
  search.keyword = ''
  search.status = ''
  search.openMonth = ''
  await loadProjects()
}

const createProject = async () => {
  await projectService.createProject({ ...form, openDueDate: form.openDueDate || null })
  Object.assign(form, { projectCode: '', name: '', department: '', requester: '', status: 'RECEIVED', openDueDate: '', description: '' })
  showCreate.value = false
  await loadProjects()
}

onMounted(loadProjects)
</script>
