<template>
  <span class="badge" :class="variantClass">{{ label }}</span>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { getStatusVariant } from '~/utils/status'

const props = defineProps<{
  status: string
  label: string
}>()

const variantClass = computed(() => getStatusVariant(props.status))
</script>
