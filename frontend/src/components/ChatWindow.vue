<script setup>
import { ref, computed, nextTick, watch } from 'vue'
import { useChatStore } from '../store/chat'
import MessageItem from './MessageItem.vue'
import LoadingDots from './LoadingDots.vue'

const chatStore = useChatStore()
const inputMessage = ref('')
const messagesContainer = ref(null)

const messages = computed(() => chatStore.messages)
const isStreaming = computed(() => chatStore.isStreaming)
const isLoading = computed(() => chatStore.isLoading)
const currentConversation = computed(() => chatStore.currentConversation)

watch(messages, async () => {
  await nextTick()
  scrollToBottom()
}, { deep: true })

function scrollToBottom() {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

async function handleSend() {
  if (!inputMessage.value.trim() || isStreaming.value) return

  const message = inputMessage.value
  inputMessage.value = ''
  await chatStore.sendMessage(message)
}

function handleKeydown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    handleSend()
  }
}
</script>

<template>
  <div class="flex-1 flex flex-col min-h-0">
    <!-- Chat header -->
    <div class="hidden lg:flex items-center justify-between px-6 py-4 bg-white/80 backdrop-blur-sm border-b border-gray-200">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-primary-500 to-purple-500 flex items-center justify-center">
          <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
          </svg>
        </div>
        <div>
          <h2 class="font-semibold text-gray-800">
            {{ currentConversation?.title || 'AI 助手' }}
          </h2>
          <p class="text-xs text-gray-500">基于 DeepSeek 模型</p>
        </div>
      </div>
      <div class="flex items-center gap-2">
        <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-green-50 text-green-600 text-xs font-medium">
          <span class="w-1.5 h-1.5 rounded-full bg-green-500"></span>
          在线
        </span>
      </div>
    </div>

    <!-- Messages area -->
    <div
      ref="messagesContainer"
      class="flex-1 overflow-y-auto px-4 md:px-6 py-6"
    >
      <!-- Empty state -->
      <div v-if="messages.length === 0 && !isLoading" class="h-full flex flex-col items-center justify-center text-center">
        <div class="w-20 h-20 rounded-2xl bg-gradient-to-br from-primary-500 to-purple-500 flex items-center justify-center mb-6 shadow-lg shadow-primary-500/20">
          <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-gray-800 mb-2">开始新对话</h3>
        <p class="text-gray-500 max-w-md mb-8">
          我是您的 AI 助手，可以帮助您回答问题、编写代码、分析数据等。请在下方输入您的问题。
        </p>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 max-w-lg w-full">
          <button
            @click="inputMessage = '你好，请介绍一下你自己'"
            class="p-4 text-left rounded-xl bg-white border border-gray-200 hover:border-primary-300 hover:bg-primary-50/50 transition-all group"
          >
            <p class="text-sm font-medium text-gray-700 group-hover:text-primary-700">👋 自我介绍</p>
            <p class="text-xs text-gray-400 mt-1">了解 AI 助手的能力</p>
          </button>
          <button
            @click="inputMessage = '帮我写一段 Python 代码'"
            class="p-4 text-left rounded-xl bg-white border border-gray-200 hover:border-primary-300 hover:bg-primary-50/50 transition-all group"
          >
            <p class="text-sm font-medium text-gray-700 group-hover:text-primary-700">💻 代码帮助</p>
            <p class="text-xs text-gray-400 mt-1">获取编程支持</p>
          </button>
          <button
            @click="inputMessage = '解释一下什么是机器学习'"
            class="p-4 text-left rounded-xl bg-white border border-gray-200 hover:border-primary-300 hover:bg-primary-50/50 transition-all group"
          >
            <p class="text-sm font-medium text-gray-700 group-hover:text-primary-700">📚 知识问答</p>
            <p class="text-xs text-gray-400 mt-1">获取知识解答</p>
          </button>
          <button
            @click="inputMessage = '帮我分析这个问题的解决方案'"
            class="p-4 text-left rounded-xl bg-white border border-gray-200 hover:border-primary-300 hover:bg-primary-50/50 transition-all group"
          >
            <p class="text-sm font-medium text-gray-700 group-hover:text-primary-700">🔍 问题分析</p>
            <p class="text-xs text-gray-400 mt-1">获取解决方案</p>
          </button>
        </div>
      </div>

      <!-- Loading state -->
      <div v-else-if="isLoading" class="h-full flex items-center justify-center">
        <LoadingDots />
      </div>

      <!-- Messages list -->
      <div v-else class="space-y-6 max-w-4xl mx-auto">
        <MessageItem
          v-for="message in messages"
          :key="message.id"
          :message="message"
        />
      </div>
    </div>

    <!-- Input area -->
    <div class="px-4 md:px-6 pb-4 md:pb-6">
      <div class="max-w-4xl mx-auto">
        <div class="relative bg-white rounded-2xl shadow-lg shadow-gray-200/50 border border-gray-200 focus-within:border-primary-300 focus-within:ring-2 focus-within:ring-primary-100 transition-all">
          <textarea
            v-model="inputMessage"
            @keydown="handleKeydown"
            :disabled="isStreaming"
            placeholder="输入您的问题..."
            rows="1"
            class="w-full px-4 py-4 pr-14 resize-none bg-transparent rounded-2xl focus:outline-none text-gray-700 placeholder-gray-400 disabled:opacity-50"
            style="min-height: 56px; max-height: 200px;"
          />
          <button
            @click="handleSend"
            :disabled="!inputMessage.trim() || isStreaming"
            :class="[
              'absolute right-3 bottom-3 p-2.5 rounded-xl transition-all',
              inputMessage.trim() && !isStreaming
                ? 'bg-gradient-to-r from-primary-500 to-primary-600 text-white hover:from-primary-600 hover:to-primary-700 shadow-md hover:shadow-lg'
                : 'bg-gray-100 text-gray-400 cursor-not-allowed'
            ]"
          >
            <svg v-if="!isStreaming" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
            </svg>
            <svg v-else class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </button>
        </div>
        <p class="text-center text-xs text-gray-400 mt-3">
          按 Enter 发送，Shift + Enter 换行
        </p>
      </div>
    </div>
  </div>
</template>
