import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.response.use(
  response => response.data,
  error => {
    const message = error.response?.data?.message || '网络请求失败'
    return Promise.reject(new Error(message))
  }
)

export const chatApi = {
  getConversations(userId = 1) {
    return api.get('/chat/conversations', { params: { userId } })
  },

  getMessages(conversationId) {
    return api.get(`/chat/conversations/${conversationId}/messages`)
  },

  createConversation(userId = 1, title = '新对话') {
    return api.post('/chat/conversations', null, { params: { userId, title } })
  },

  deleteConversation(conversationId) {
    return api.delete(`/chat/conversations/${conversationId}`)
  },

  async streamChat(message, conversationId, userId, onChunk, onDone, onError) {
    try {
      const response = await fetch('/api/chat/stream', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          message,
          conversationId,
          userId
        })
      })

      if (!response.ok) {
        throw new Error('请求失败')
      }

      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''
      let newConversationId = conversationId

      while (true) {
        const { done, value } = await reader.read()

        if (done) {
          onDone(newConversationId)
          break
        }

        buffer += decoder.decode(value, { stream: true })
        const lines = buffer.split('\n')
        buffer = lines.pop() || ''

        for (const line of lines) {
          if (line.startsWith('data:')) {
            try {
              const data = JSON.parse(line.slice(5))
              if (data.conversationId) {
                newConversationId = data.conversationId
              }
              if (data.content) {
                onChunk(data.content)
              }
              if (data.error) {
                onError(data.error)
                return
              }
            } catch (e) {
              // Ignore parse errors
            }
          }
        }
      }
    } catch (error) {
      onError(error.message || '请求失败')
    }
  }
}

export const userApi = {
  login(username, password) {
    return api.post('/user/login', null, { params: { username, password } })
  },

  getUserInfo(userId) {
    return api.get(`/user/${userId}`)
  }
}

export const healthApi = {
  check() {
    return api.get('/health')
  }
}

export default api
