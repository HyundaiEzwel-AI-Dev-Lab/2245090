export default defineEventHandler(async (event) => {
  const rawPath = event.context.params?.path
  const path = Array.isArray(rawPath) ? rawPath.join('/') : rawPath || ''
  const method = event.node.req.method || 'GET'
  const query = getQuery(event)

  let body: unknown = undefined
  if (!['GET', 'HEAD'].includes(method)) {
    body = await readBody(event).catch(() => undefined)
  }

  try {
    return await $fetch(`http://localhost:8080/api/${path}`, {
      method,
      query,
      body,
      headers: {
        accept: 'application/json',
        'content-type': 'application/json'
      }
    })
  } catch (error: any) {
    const statusCode = error?.response?.status || error?.statusCode || 500
    const data = error?.data || error?.response?._data || {
      message: error?.message || 'Backend proxy failed'
    }

    throw createError({
      statusCode,
      statusMessage: data?.message || error?.message || 'Backend proxy failed',
      data
    })
  }
})
