<template>
  <section class="panel table-panel">
    <div class="panel-head">
      <div>
        <h2>WBS 상세 목록</h2>
        <p>Backend WBS API 조회 결과입니다.</p>
      </div>
    </div>

    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>업무명</th>
            <th>유형</th>
            <th>담당자</th>
            <th>상태</th>
            <th>진척률</th>
            <th>계획종료일</th>
            <th>액션</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="task in tasks" :key="task.id">
            <td @click="$emit('open', task)">{{ task.taskName }}</td>
            <td>{{ task.workTypeLabel }}</td>
            <td>{{ task.assignee }}</td>
            <td><span :class="['status-badge', statusClass(task.status)]">{{ task.statusLabel }}</span></td>
            <td>{{ task.progressRate }}%</td>
            <td>{{ task.planEndDate ?? '-' }}</td>
            <td>
              <button class="table-btn" @click.stop="$emit('start', task.id)">착수</button>
              <button class="soft-btn" @click.stop="$emit('complete', task.id)">완료</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="tasks.length === 0" class="empty-mini">조회된 WBS가 없습니다.</div>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { WbsTask } from '~/types/pms'

defineProps<{
  tasks: WbsTask[]
}>()

defineEmits<{
  open: [task: WbsTask]
  start: [taskId: number]
  complete: [taskId: number]
}>()

const statusClass = (status: string) => {
  if (['COMPLETED', 'PASS', 'CONFIRMED'].includes(status)) return 'working'
  if (['IN_PROGRESS', 'TESTING'].includes(status)) return 'review'
  if (['FAIL', 'REJECTED'].includes(status)) return 'danger'
  return 'hold'
}
</script>
