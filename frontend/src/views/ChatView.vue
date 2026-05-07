<script setup>
import { onMounted, ref } from 'vue'
import { useChatStore } from '../store/chat'
import Sidebar from '../components/Sidebar.vue'
import ChatWindow from '../components/ChatWindow.vue'
import Toast from '../components/Toast.vue'

const chatStore = useChatStore()
const showMobileSidebar = ref(false)

onMounted(async () => {
  await chatStore.loadConversations()
  if (chatStore.conversations.length > 0) {
    await chatStore.selectConversation(chatStore.conversations[0].id)
  }
})

function toggleSidebar() {
  showMobileSidebar.value = !showMobileSidebar.value
}

function closeSidebar() {
  showMobileSidebar.value = false
}
</script>

<template>
  <div class="flex h-full gradient-bg">
    <!-- Mobile sidebar overlay -->
    <div
      v-if="showMobileSidebar"
      class="fixed inset-0 bg-black/50 z-40 lg:hidden"
      @click="closeSidebar"
    />

    <!-- Sidebar -->
    <aside
      :class="[
        'fixed lg:relative inset-y-0 left-0 z-50 w-72 transform transition-transform duration-300 lg:translate-x-0',
        showMobileSidebar ? 'translate-x-0' : '-translate-x-full'
      ]"
    >
      <Sidebar @close="closeSidebar" />
    </aside>

    <!-- Main content -->
    <main class="flex-1 flex flex-col min-w-0">
      <!-- Mobile header -->
      <div class="lg:hidden flex items-center px-4 py-3 bg-white border-b border-gray-200">
        <button
          @click="toggleSidebar"
          class="p-2 rounded-lg hover:bg-gray-100 transition-colors"
        >
          <svg class="w-6 h-6 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
        <h1 class="ml-3 text-lg font-semibold text-gray-800">AI Chat</h1>
      </div>

      <ChatWindow />
    </main>

    <!-- Toast notifications -->
    <Toast
      v-if="chatStore.error"
      :message="chatStore.error"
      type="error"
      @close="chatStore.clearError"
    />
  </div>
</template>
