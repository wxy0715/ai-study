<template>
  <div class="app-layout">
    <div class="sidebar">
      <div class="logo-section">
        <img src="@/assets/logo.png" alt="木芒果小智" class="logo-image"/>
        <span class="logo-text">木芒果小智（医疗版）</span>
      </div>
      <el-button class="new-chat-button" @click="newChat">
        <i class="fa-solid fa-plus"></i>
        新会话
      </el-button>
    </div>
    <div class="main-content">
      <div class="chat-container">
        <div class="message-list" ref="messaggListRef">
          <div
              v-for="(message, index) in messages"
              :key="index"
              :class="[
                'message',
                message.isUser ? 'user-message' : 'bot-message',
                'message-fade-in'
              ]"
          >
            <i :class="[
              'message-icon',
              message.isUser ? 'fa-solid fa-user' : 'fa-solid fa-robot'
            ]"></i>
            <div class="message-content">
              <span v-html="message.isUser ? message.content : parseMarkdown(message.content)"></span>
              <span
                  class="loading-dots"
                  v-if="message.isThinking || message.isTyping"
              >
                <span class="dot"></span>
                <span class="dot"></span>
              </span>
            </div>
          </div>
        </div>
        <div class="input-container">
          <el-input
              v-model="inputMessage"
              placeholder="请输入消息"
              @keyup.enter="sendMessage"
              class="chat-input"
          ></el-input>
          <el-button
              @click="sendMessage"
              :disabled="isSending"
              type="primary"
              class="send-button"
          >发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref, watch} from 'vue'
import axios from 'axios'
import {v4 as uuidv4} from 'uuid'
import {marked} from 'marked'

const messaggListRef = ref()
const isSending = ref(false)
const uuid = ref()
const inputMessage = ref('')
const messages = ref([])

onMounted(() => {
  initUUID()
  // 移除 setInterval，改用手动滚动
  watch(messages, () => scrollToBottom(), {deep: true})
  hello()
})

const scrollToBottom = () => {
  if (messaggListRef.value) {
    messaggListRef.value.scrollTop = messaggListRef.value.scrollHeight
  }
}

const hello = () => {
  sendRequest('你好')
}

const sendMessage = () => {
  if (inputMessage.value.trim()) {
    sendRequest(inputMessage.value.trim())
    inputMessage.value = ''
  }
}

const sendRequest = (message) => {
  isSending.value = true
  const userMsg = {
    isUser: true,
    content: message,
    isTyping: false,
    isThinking: false,
  }
  //第一条默认发送的用户消息”你好“不放入会话列表
  if (messages.value.length > 0) {
    messages.value.push(userMsg)
  }


  // 添加机器人加载消息
  const botMsg = {
    isUser: false,
    content: '', // 增量填充
    isTyping: true, // 显示加载动画
    isThinking: false,
  }
  messages.value.push(botMsg)
  const lastMsg = messages.value[messages.value.length - 1]
  scrollToBottom()

  axios
      .post(
          '/api/chat/chat',
          {memoryId: uuid.value, message},
          {
            responseType: 'stream', // 必须为合法值 "text"
            onDownloadProgress: (e) => {
              const fullText = e.event.target.responseText // 累积的完整文本
              let newText = fullText.substring(lastMsg.content.length)
              lastMsg.content += newText //增量更新
              console.log(lastMsg)
              scrollToBottom() // 实时滚动
            },
          }
      )
      .then(() => {
        // 流结束后隐藏加载动画
        messages.value.at(-1).isTyping = false
        isSending.value = false
      })
      .catch((error) => {
        console.error('流式错误:', error)
        messages.value.at(-1).content = '请求失败，请重试'
        messages.value.at(-1).isTyping = false
        isSending.value = false
      })
}

// 初始化 UUID
const initUUID = () => {
  let storedUUID = localStorage.getItem('user_uuid')
  if (!storedUUID) {
    storedUUID = uuidToNumber(uuidv4())
    localStorage.setItem('user_uuid', storedUUID)
  }
  uuid.value = storedUUID
}

const uuidToNumber = (uuid) => {
  let number = 0
  for (let i = 0; i < uuid.length && i < 6; i++) {
    const hexValue = uuid[i]
    number = number * 16 + (parseInt(hexValue, 16) || 0)
  }
  return number % 1000000
}

// 转换特殊字符
const convertStreamOutput = (output) => {
  return output
      .replace(/\n/g, '<br>')
      .replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;')
      .replace(/&/g, '&amp;') // 新增转义，避免 HTML 注入
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
}

const newChat = () => {
  // 这里添加新会话的逻辑
  console.log('开始新会话')
  localStorage.removeItem('user_uuid')
  window.location.reload()
}

// 添加 Markdown 解析函数
const parseMarkdown = (content) => {
  if (!content) return ''
  try {
    return marked(content)
  } catch (error) {
    console.error('Markdown 解析错误:', error)
    return content
  }
}
</script>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
  background-color: #ffffff;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
}

.sidebar {
  width: 280px;
  background-color: rgba(250, 250, 250, 0.95);
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-right: 1px solid #e7e7e7;
  backdrop-filter: blur(10px);
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32px;
}

.logo-image {
  width: 120px;
  height: 120px;
  border-radius: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.logo-image:hover {
  transform: scale(1.05);
}

.logo-text {
  font-size: 20px;
  font-weight: 500;
  margin-top: 16px;
  color: #1d1d1f;
}

.new-chat-button {
  width: 100%;
  height: 44px;
  border-radius: 22px;
  background: linear-gradient(135deg, #0077ED, #00A6FF);
  border: none;
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
}

.new-chat-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 119, 237, 0.2);
}

.main-content {
  flex: 1;
  padding: 24px;
  background-color: #ffffff;
}

.chat-container {
  max-width: 1000px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  border-radius: 16px;
  background-color: #f5f5f7;
  margin-bottom: 24px;
  scroll-behavior: smooth;
}

.message {
  margin-bottom: 20px;
  padding: 16px;
  border-radius: 16px;
  display: flex;
  align-items: flex-start;
  animation: fadeIn 0.3s ease;
}

.message-fade-in {
  opacity: 0;
  animation: fadeIn 0.3s ease forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  background-color: #007AFF;
  margin-left: 48px;
  color: white;
}

.bot-message {
  background-color: white;
  margin-right: 48px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.message-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  margin-right: 12px;
  background-color: #f5f5f7;
}

.user-message .message-icon {
  background-color: white;
  color: #007AFF;
}

.bot-message .message-icon {
  background-color: #007AFF;
  color: white;
}

.message-content {
  flex: 1;
  line-height: 1.6;
}

.input-container {
  display: flex;
  gap: 12px;
  padding: 16px;
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.chat-input :deep(.el-input__wrapper) {
  border-radius: 24px;
  background-color: #f5f5f7;
  border: none;
  padding: 8px 20px;
  box-shadow: none !important;
}

.chat-input :deep(.el-input__inner) {
  height: 44px;
  font-size: 16px;
}

.send-button {
  height: 44px;
  border-radius: 22px;
  padding: 0 24px;
  font-weight: 500;
  background: linear-gradient(135deg, #0077ED, #00A6FF);
  border: none;
  transition: all 0.3s ease;
}

.send-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 119, 237, 0.2);
}

.loading-dots {
  display: inline-flex;
  align-items: center;
  margin-left: 8px;
}

.dot {
  width: 6px;
  height: 6px;
  margin: 0 2px;
  background-color: currentColor;
  border-radius: 50%;
  opacity: 0.6;
  animation: bounce 1.4s infinite ease-in-out both;
}

.dot:nth-child(1) {
  animation-delay: -0.32s;
}

.dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

@media (max-width: 768px) {
  .app-layout {
    flex-direction: column;
    height: 100vh;
    overflow: hidden;
  }

  .sidebar {
    width: 100%;
    height: auto;
    padding: 12px;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    border-right: none;
    border-bottom: 1px solid #e7e7e7;
    background-color: rgba(250, 250, 250, 0.98);
    position: sticky;
    top: 0;
    z-index: 100;
  }

  .logo-section {
    flex-direction: row;
    margin-bottom: 0;
    align-items: center;
  }

  .logo-image {
    width: 36px;
    height: 36px;
    margin-right: 12px;
  }

  .logo-text {
    margin-top: 0;
    font-size: 16px;
  }

  .new-chat-button {
    width: auto;
    height: 36px;
    padding: 0 16px;
    font-size: 14px;
    display: flex;
    align-items: center;
    white-space: nowrap;
    min-width: 90px;
  }

  .new-chat-button i {
    margin-right: 4px;
  }

  .main-content {
    flex: 1;
    padding: 12px;
    height: calc(100vh - 61px); /* 减去顶部导航栏高度 */
    overflow: hidden;
  }

  .chat-container {
    height: 100%;
  }

  .message-list {
    padding: 12px;
    margin-bottom: 12px;
  }

  .message {
    margin: 8px 0;
    padding: 12px;
    font-size: 14px;
  }

  .user-message {
    margin-left: 24px;
    margin-right: 8px;
  }

  .bot-message {
    margin-right: 24px;
    margin-left: 8px;
  }

  .message-icon {
    width: 28px;
    height: 28px;
    margin-right: 8px;
  }

  .input-container {
    padding: 12px;
    margin-bottom: env(safe-area-inset-bottom);
  }

  .chat-input :deep(.el-input__wrapper) {
    height: 40px;
  }

  .chat-input :deep(.el-input__inner) {
    height: 40px;
    font-size: 14px;
  }

  .send-button {
    height: 40px;
    padding: 0 16px;
    font-size: 14px;
  }
}

/* 添加针对超小屏幕的优化 */
@media (max-width: 320px) {
  .logo-text {
    font-size: 14px;
  }

  .new-chat-button {
    padding: 0 12px;
    min-width: 80px;
  }

  .new-chat-button i {
    margin-right: 2px;
  }

  .message {
    padding: 10px;
    font-size: 13px;
  }
}

/* Markdown 样式保持不变 */
:deep(.bot-message) {
  /* 标题样式 */

  h1, h2, h3, h4, h5, h6 {
    margin: 16px 0 8px 0;
    font-weight: 600;
    line-height: 1.25;
  }

  /* 段落样式 */

  p {
    margin: 8px 0;
    line-height: 1.6;
  }

  /* 列表样式 */

  ul, ol {
    padding-left: 20px;
    margin: 8px 0;
  }

  /* 代码块样式 */

  pre {
    background-color: #f6f8fa;
    border-radius: 6px;
    padding: 16px;
    overflow: auto;
    margin: 8px 0;
  }

  code {
    font-family: Consolas, Monaco, 'Andale Mono', monospace;
    background-color: rgba(175, 184, 193, 0.2);
    padding: 0.2em 0.4em;
    border-radius: 6px;
    font-size: 85%;
  }

  pre code {
    background-color: transparent;
    padding: 0;
  }

  /* 表格样式 */

  table {
    border-collapse: collapse;
    margin: 8px 0;
    width: 100%;
  }

  th, td {
    border: 1px solid #d0d7de;
    padding: 6px 13px;
  }

  th {
    background-color: #f6f8fa;
  }

  /* 引用样式 */

  blockquote {
    border-left: 4px solid #d0d7de;
    margin: 8px 0;
    padding: 0 16px;
    color: #57606a;
  }

  /* 链接样式 */

  a {
    color: #0969da;
    text-decoration: none;
  }

  a:hover {
    text-decoration: underline;
  }
}
</style>
