<script setup>
import { computed } from 'vue'
import { marked } from 'marked'

const props = defineProps({
  message: {
    type: Object,
    required: true
  }
})

const isUser = computed(() => props.message.role === 'user')

const formattedContent = computed(() => {
  if (isUser.value) {
    return props.message.content
  }
  return marked.parse(props.message.content || '', { breaks: true })
})

const formattedTime = computed(() => {
  if (!props.message.createdAt) return ''
  const date = new Date(props.message.createdAt)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
})
</script>

<template>
  <div :class="['flex gap-4', isUser ? 'flex-row-reverse' : '']">
    <!-- Avatar -->
    <div :class="[
      'flex-shrink-0 w-9 h-9 rounded-xl flex items-center justify-center text-white font-medium text-sm',
      isUser
        ? 'bg-gradient-to-br from-purple-500 to-pink-500'
        : 'bg-gradient-to-br from-primary-500 to-cyan-500'
    ]">
      {{ isUser ? 'U' : 'AI' }}
    </div>

    <!-- Message content -->
    <div :class="['flex-1 max-w-[85%]', isUser ? 'flex flex-col items-end' : '']">
      <div :class="[
        'relative px-4 py-3 rounded-2xl',
        isUser
          ? 'bg-gradient-to-br from-primary-500 to-primary-600 text-white rounded-tr-md'
          : 'bg-white border border-gray-200 text-gray-700 rounded-tl-md shadow-sm'
      ]">
        <!-- User message -->
        <p v-if="isUser" class="whitespace-pre-wrap break-words">
          {{ formattedContent }}
        </p>

        <!-- Assistant message with markdown -->
        <div
          v-else
          class="message-content prose prose-sm max-w-none"
          v-html="formattedContent"
        />

        <!-- Streaming indicator -->
        <span
          v-if="message.isStreaming"
          class="inline-block w-2 h-4 ml-1 bg-primary-500 animate-pulse rounded-sm"
        />
      </div>

      <!-- Time -->
      <span class="text-xs text-gray-400 mt-1.5 px-1">
        {{ formattedTime }}
      </span>
    </div>
  </div>
</template>
