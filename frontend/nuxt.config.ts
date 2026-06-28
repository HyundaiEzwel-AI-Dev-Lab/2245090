export default defineNuxtConfig({
  ssr: false,
  devtools: { enabled: true },
  css: ['~/assets/css/original-sb.css'],

  runtimeConfig: {
    public: {
      apiBase: '/api'
    }
  },

  app: {
    head: {
      title: '전사 프로젝트 관리 시스템 - Vue POC',
      meta: [{ name: 'viewport', content: 'width=device-width, initial-scale=1' }]
    }
  },

  typescript: {
    strict: false,
    typeCheck: false
  }
})
