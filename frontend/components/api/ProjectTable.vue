<template>
  <div class="table-wrap">
    <table>
      <thead>
        <tr>
          <th>코드</th>
          <th>프로젝트명</th>
          <th>부서</th>
          <th>요청자</th>
          <th>상태</th>
          <th>진척률</th>
          <th>오픈예정일</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="project in projects" :key="project.id" @click="$emit('open', project.id)">
          <td>{{ project.projectCode }}</td>
          <td>{{ project.name }}</td>
          <td>{{ project.department }}</td>
          <td>{{ project.requester }}</td>
          <td><span :class="['status-badge', statusClass(project.status)]">{{ project.statusLabel }}</span></td>
          <td>{{ project.progressRate }}%</td>
          <td>{{ project.openDueDate ?? '-' }}</td>
        </tr>
      </tbody>
    </table>

    <div v-if="projects.length === 0" class="empty-mini">조회된 프로젝트가 없습니다.</div>
  </div>
</template>

<script setup lang="ts">
import type { Project } from '~/types/pms'

defineProps<{
  projects: Project[]
}>()

defineEmits<{
  open: [projectId: number]
}>()

const statusClass = (status: string) => {
  if (['COMPLETED', 'PASS', 'CONFIRMED'].includes(status)) return 'working'
  if (['IN_PROGRESS', 'TESTING'].includes(status)) return 'review'
  if (['FAIL', 'REJECTED'].includes(status)) return 'danger'
  return 'hold'
}
</script>
