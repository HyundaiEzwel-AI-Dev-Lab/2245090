<template>
  <div class="table-wrap">
    <table>
      <thead>
        <tr>
          <th>업무명</th>
          <th>업무유형</th>
          <th>담당자</th>
          <th>계획일정</th>
          <th>상태</th>
          <th>공정률</th>
          <th>작업</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="task in tasks" :key="task.id">
          <td><strong>{{ task.taskName }}</strong></td>
          <td>{{ task.workTypeLabel }}</td>
          <td>{{ task.assignee }}</td>
          <td>{{ task.planStartDate ?? '-' }} ~ {{ task.planEndDate ?? '-' }}</td>
          <td><StatusBadge :status="task.delayed ? 'FAIL' : task.status" :label="task.delayed ? '지연' : task.statusLabel" /></td>
          <td>{{ task.progressRate }}%</td>
          <td class="actions">
            <BaseButton @click="$emit('schedule', task)">일정</BaseButton>
            <BaseButton @click="$emit('start', task)">착수</BaseButton>
            <BaseButton variant="primary" @click="$emit('complete', task)">완료</BaseButton>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import type { WbsTask } from '~/types/pms'

defineProps<{ tasks: WbsTask[] }>()
defineEmits<{
  schedule: [task: WbsTask]
  start: [task: WbsTask]
  complete: [task: WbsTask]
}>()
</script>
