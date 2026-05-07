<script setup>
import { computed, ref } from 'vue'
import { useChatStore } from '../store/chat'

const emit = defineEmits(['close'])
const chatStore = useChatStore()

const conversations = computed(() => chatStore.conversations)
const currentId = computed(() => chatStore.currentConversationId)

const showDeleteModal = ref(false)
const deleteTargetId = ref(null)
const deleteTargetTitle = ref('')

async function handleNewChat() {
  await chatStore.createConversation()
  emit('close')
}

async function handleSelect(id) {
  await chatStore.selectConversation(id)
  emit('close')
}

function openDeleteModal(id, title, event) {
  event.stopPropagation()
  deleteTargetId.value = id
  deleteTargetTitle.value = title
  showDeleteModal.value = true
}

function closeDeleteModal() {
  showDeleteModal.value = false
  deleteTargetId.value = null
  deleteTargetTitle.value = ''
}

async function confirmDelete() {
  if (deleteTargetId.value) {
    await chatStore.deleteConversation(deleteTargetId.value)
  }
  closeDeleteModal()
}

function formatTime(dateStr) {
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'

  return date.toLocaleDateString('zh-CN')
}
</script>

<template>
  <div class="h-full flex flex-col bg-white border-r border-gray-200">
    <!-- Header -->
    <div class="p-4 border-b border-gray-100">
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-primary-500 to-purple-500 flex items-center justify-center">
            <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
            </svg>
          </div>
          <div>
            <h1 class="text-lg font-bold text-gray-800">AI Chat</h1>
            <p class="text-xs text-gray-500">智能对话助手</p>
          </div>
        </div>
        <button
          @click="emit('close')"
          class="lg:hidden p-2 rounded-lg hover:bg-gray-100 transition-colors"
        >
          <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- New chat button -->
      <button
        @click="handleNewChat"
        class="w-full flex items-center justify-center gap-2 px-4 py-3 bg-gradient-to-r from-primary-500 to-primary-600 text-white rounded-xl hover:from-primary-600 hover:to-primary-700 transition-all shadow-sm hover:shadow-md"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        <span class="font-medium">新建对话</span>
      </button>
    </div>

    <!-- Conversations list -->
    <div class="flex-1 overflow-y-auto p-3">
      <div v-if="conversations.length === 0" class="text-center py-12 text-gray-400">
        <svg class="w-12 h-12 mx-auto mb-3 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
        </svg>
        <p class="text-sm">暂无对话记录</p>
        <p class="text-xs mt-1">点击上方按钮开始新对话</p>
      </div>

      <div v-else class="space-y-2">
        <div
          v-for="conv in conversations"
          :key="conv.id"
          @click="handleSelect(conv.id)"
          :class="[
            'group flex items-center gap-3 p-3 rounded-xl cursor-pointer transition-all',
            currentId === conv.id
              ? 'bg-primary-50 border border-primary-200'
              : 'hover:bg-gray-50 border border-transparent'
          ]"
        >
          <div :class="[
            'w-9 h-9 rounded-lg flex items-center justify-center flex-shrink-0',
            currentId === conv.id ? 'bg-primary-100' : 'bg-gray-100'
          ]">
            <svg :class="[
              'w-4 h-4',
              currentId === conv.id ? 'text-primary-600' : 'text-gray-500'
            ]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
            </svg>
          </div>
          <div class="flex-1 min-w-0">
            <p :class="[
              'text-sm font-medium truncate',
              currentId === conv.id ? 'text-primary-700' : 'text-gray-700'
            ]">
              {{ conv.title }}
            </p>
            <p class="text-xs text-gray-400 mt-0.5">
              {{ formatTime(conv.updatedAt) }}
            </p>
          </div>
          <button
            @click="openDeleteModal(conv.id, conv.title, $event)"
            class="opacity-0 group-hover:opacity-100 p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-all"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <div class="p-4 border-t border-gray-100">
      <div class="flex items-center gap-3 p-3 rounded-xl bg-gray-50">
        <div class="w-9 h-9 rounded-full bg-gradient-to-br from-primary-400 to-purple-400 flex items-center justify-center text-white font-medium text-sm">
          U
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium text-gray-700">演示用户</p>
          <p class="text-xs text-gray-400">在线</p>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <Teleport to="body">
      <Transition name="modal">
        <div
          v-if="showDeleteModal"
          class="fixed inset-0 z-[100] flex items-center justify-center p-4"
        >
          <!-- Backdrop -->
          <div
            class="absolute inset-0 bg-black/50 backdrop-blur-sm"
            @click="closeDeleteModal"
          />

          <!-- Modal -->
          <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-sm overflow-hidden animate-modal-in">
            <!-- Header -->
            <div class="px-6 pt-6 pb-4">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 rounded-full bg-red-100 flex items-center justify-center flex-shrink-0">
                  <svg class="w-6 h-6 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </div>
                <div>
                  <h3 class="text-lg font-semibold text-gray-900">删除对话</h3>
                  <p class="text-sm text-gray-500 mt-0.5">此操作无法撤销</p>
                </div>
              </div>
            </div>

            <!-- Content -->
            <div class="px-6 pb-4">
              <p class="text-gray-600">
                确定要删除对话
                <span class="font-medium text-gray-900">"{{ deleteTargetTitle }}"</span>
                吗？删除后将无法恢复。
              </p>
            </div>

            <!-- Actions -->
            <div class="px-6 pb-6 flex gap-3">
              <button
                @click="closeDeleteModal"
                class="flex-1 px-4 py-2.5 text-sm font-medium text-gray-700 bg-gray-100 rounded-xl hover:bg-gray-200 transition-colors"
              >
                取消
              </button>
              <button
                @click="confirmDelete"
                class="flex-1 px-4 py-2.5 text-sm font-medium text-white bg-gradient-to-r from-red-500 to-red-600 rounded-xl hover:from-red-600 hover:to-red-700 transition-all shadow-sm hover:shadow-md"
              >
                确认删除
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

@keyframes modal-in {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(-10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.animate-modal-in {
  animation: modal-in 0.2s ease-out;
}
</style>
