import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { chatApi } from '../api'

export const useChatStore = defineStore('chat', () => {
  const conversations = ref([])
  const currentConversationId = ref(null)
  const messages = ref([])
  const isLoading = ref(false)
  const isStreaming = ref(false)
  const streamingContent = ref('')
  const error = ref(null)
  const userId = ref(1)

  const currentConversation = computed(() => {
    return conversations.value.find(c => c.id === currentConversationId.value)
  })

  async function loadConversations() {
    try {
      const response = await chatApi.getConversations(userId.value)
      if (response.success) {
        conversations.value = response.data
      }
    } catch (e) {
      error.value = e.message
    }
  }

  async function loadMessages(conversationId) {
    if (!conversationId) return

    isLoading.value = true
    try {
      const response = await chatApi.getMessages(conversationId)
      if (response.success) {
        messages.value = response.data.map(m => ({
          ...m,
          role: m.role.toLowerCase()
        }))
      }
    } catch (e) {
      error.value = e.message
    } finally {
      isLoading.value = false
    }
  }

  async function selectConversation(conversationId) {
    currentConversationId.value = conversationId
    await loadMessages(conversationId)
  }

  async function createConversation() {
    try {
      const response = await chatApi.createConversation(userId.value)
      if (response.success) {
        conversations.value.unshift(response.data)
        await selectConversation(response.data.id)
        return response.data
      }
    } catch (e) {
      error.value = e.message
    }
    return null
  }

  async function deleteConversation(conversationId) {
    try {
      const response = await chatApi.deleteConversation(conversationId)
      if (response.success) {
        conversations.value = conversations.value.filter(c => c.id !== conversationId)
        if (currentConversationId.value === conversationId) {
          currentConversationId.value = null
          messages.value = []
        }
      }
    } catch (e) {
      error.value = e.message
    }
  }

  async function sendMessage(content) {
    if (!content.trim() || isStreaming.value) return

    const userMessage = {
      id: Date.now(),
      role: 'user',
      content: content.trim(),
      createdAt: new Date().toISOString()
    }

    messages.value.push(userMessage)
    isStreaming.value = true
    streamingContent.value = ''
    error.value = null

    const assistantMessage = {
      id: Date.now() + 1,
      role: 'assistant',
      content: '',
      createdAt: new Date().toISOString(),
      isStreaming: true
    }
    messages.value.push(assistantMessage)

    chatApi.streamChat(
      content.trim(),
      currentConversationId.value,
      userId.value,
      (chunk) => {
        streamingContent.value += chunk
        const lastMessage = messages.value[messages.value.length - 1]
        if (lastMessage.isStreaming) {
          lastMessage.content = streamingContent.value
        }
      },
      async (newConversationId) => {
        isStreaming.value = false
        const lastMessage = messages.value[messages.value.length - 1]
        if (lastMessage.isStreaming) {
          lastMessage.isStreaming = false
        }

        if (!currentConversationId.value && newConversationId) {
          currentConversationId.value = newConversationId
          await loadConversations()
        }
      },
      (errorMsg) => {
        isStreaming.value = false
        error.value = errorMsg
        const lastMessage = messages.value[messages.value.length - 1]
        if (lastMessage.isStreaming) {
          lastMessage.content = '抱歉，发生了错误：' + errorMsg
          lastMessage.isStreaming = false
        }
      }
    )
  }

  function clearError() {
    error.value = null
  }

  return {
    conversations,
    currentConversationId,
    currentConversation,
    messages,
    isLoading,
    isStreaming,
    streamingContent,
    error,
    userId,
    loadConversations,
    loadMessages,
    selectConversation,
    createConversation,
    deleteConversation,
    sendMessage,
    clearError
  }
})
