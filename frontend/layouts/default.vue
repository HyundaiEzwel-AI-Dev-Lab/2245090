<template>
  <div class="shell">
    <aside class="sidebar" :class="{ 'is-open': menuOpen }">
      <div class="brand">
        <strong>PMS</strong>
        <span>전사 프로젝트관리</span>
      </div>
      <nav class="nav">
        <NuxtLink v-for="item in menus" :key="item.path" :to="item.path" @click="menuOpen = false">
          {{ item.label }}
        </NuxtLink>
      </nav>
    </aside>
    <div class="main">
      <header class="topbar">
        <button class="hamburger" @click="menuOpen = !menuOpen">☰</button>
        <div>
          <strong>전사 프로젝트 관리 시스템</strong>
          <p>SB 기반 풀스택 POC</p>
        </div>
        <div class="user-chip">이지윤 / 관리자</div>
      </header>
      <main class="content"><slot /></main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const menuOpen = ref(false)
const menus = [
  { label: '대시보드', path: '/' },
  { label: '내업무', path: '/my-work' },
  { label: '프로젝트 현황', path: '/projects' },
  { label: '시스템 관리', path: '/system/users' }
]
</script>

<style scoped>
.shell { min-height: 100vh; display: flex; }
.sidebar { width: 260px; background: #0f172a; color: white; padding: 24px 18px; position: sticky; top: 0; height: 100vh; }
.brand { display: flex; flex-direction: column; gap: 4px; margin-bottom: 28px; }
.brand strong { font-size: 25px; }
.brand span { color: #cbd5e1; font-size: 13px; }
.nav { display: flex; flex-direction: column; gap: 8px; }
.nav a { padding: 12px 14px; border-radius: 12px; color: #cbd5e1; font-weight: 700; }
.nav a.router-link-active, .nav a:hover { background: rgba(255,255,255,0.12); color: white; }
.main { flex: 1; min-width: 0; }
.topbar { height: 72px; display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 0 26px; background: rgba(255,255,255,0.9); border-bottom: 1px solid var(--line); position: sticky; top: 0; z-index: 5; backdrop-filter: blur(8px); }
.topbar p { margin: 2px 0 0; color: var(--muted); font-size: 13px; }
.content { padding: 26px; }
.hamburger { display: none; border: 1px solid var(--line); border-radius: 10px; background: white; padding: 8px 11px; }
.user-chip { border: 1px solid var(--line); border-radius: 999px; padding: 9px 12px; background: white; font-size: 13px; font-weight: 700; }
@media (max-width: 900px) {
  .sidebar { position: fixed; z-index: 10; transform: translateX(-100%); transition: 0.2s; }
  .sidebar.is-open { transform: translateX(0); }
  .hamburger { display: inline-flex; }
  .content { padding: 18px; }
}
</style>
