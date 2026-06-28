import type { ApiResponse } from '~/types/pms'

export const useApi = () => {
  const config = useRuntimeConfig()
  const baseURL = config.public.apiBase || '/api'

  const request = async <T>(url: string, options: Parameters<typeof $fetch>[1] = {}): Promise<T> => {
    const response = await $fetch<ApiResponse<T> | T>(url, { baseURL, ...options })

    if (response && typeof response === 'object' && 'success' in response) {
      const wrapped = response as ApiResponse<T>
      if (wrapped.success === false) {
        throw new Error(wrapped.message || 'API 요청에 실패했습니다.')
      }
      return wrapped.data as T
    }

    return response as T
  }

  return { request }
}
