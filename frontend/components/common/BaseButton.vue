<template>
  <button class="base-button" :class="[`base-button--${variant}`]" :type="type">
    <slot />
  </button>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  variant?: 'primary' | 'secondary' | 'danger'
  type?: 'button' | 'submit'
}>(), {
  variant: 'secondary',
  type: 'button'
})
</script>

<style scoped>
.base-button { border: 1px solid var(--line); border-radius: 12px; padding: 10px 14px; background: white; color: var(--text); font-weight: 700; transition: 0.15s; }
.base-button:hover { transform: translateY(-1px); box-shadow: 0 8px 20px rgba(15, 23, 42, 0.08); }
.base-button--primary { background: var(--primary); color: white; border-color: var(--primary); }
.base-button--danger { background: var(--danger); color: white; border-color: var(--danger); }
</style>
