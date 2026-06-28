<template>
  <div class="table-wrap">
    <table>
      <thead>
        <tr>
          <th>프로젝트 ID</th>
          <th>프로젝트명</th>
          <th>요청부서</th>
          <th>요청자</th>
          <th>상태</th>
          <th>오픈예정일</th>
          <th>공정률</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="project in projects" :key="project.id" @click="navigateTo(`/projects/${project.id}`)">
          <td>{{ project.projectCode }}</td>
          <td><strong>{{ project.name }}</strong></td>
          <td>{{ project.department }}</td>
          <td>{{ project.requester }}</td>
          <td><StatusBadge :status="project.status" :label="project.statusLabel" /></td>
          <td>{{ project.openDueDate ?? '오픈일 미정' }}</td>
          <td>{{ project.progressRate }}%</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import type { Project } from '~/types/pms'

defineProps<{ projects: Project[] }>()
</script>

<style scoped>
tr { cursor: pointer; }
tbody tr:hover { background: var(--primary-soft); }
</style>
