<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()

const loginForm = reactive({
  username: '',
  password: ''
})
const errorMessage = ref('')

const isLoading = ref(false)

const handleLogin = async () => {
  if (isLoading.value){
    return
  }
  errorMessage.value = ''

  if (loginForm.username.trim() === '') {
    errorMessage.value = '请输入账号...'
    return
  }
  if (loginForm.password === ''){
    errorMessage.value = '请输入密码...'
    return
  }
  isLoading.value = true
  try {
    const response = await request.post(
        '/employee/login',
        loginForm
    )

    const result = response.data

    if (result.code === 1) {
      localStorage.setItem(
          'sky-admin-token',
          result.data.token
      )
      await router.push({
        name: 'dashboard'
      })
    } else {
      errorMessage.value = result.msg || '登录失败，请重试'
    }
  } catch (error) {
    errorMessage.value = '服务器连接失败，请稍后重试'
    console.error('登录请求发送失败：', error)
  } finally {
    isLoading.value = false
  }

}
</script>


<template>
  <main class="login-page">
    <!-- 左侧：品牌介绍区域 -->
    <section class="brand-section">
      <div class="brand-content">
        <div class="brand-badge">
          <span class="brand-dot"></span>
          SKY TAKE OUT
        </div>

        <h1>
          让每一份美味，
          <span>高效抵达。</span>
        </h1>

        <p class="brand-description">
          一站式餐饮运营管理平台，帮助门店高效管理菜品、订单和经营数据。
        </p>

        <div class="feature-list">
          <div class="feature-item">
            <strong>实时</strong>
            <span>订单状态追踪</span>
          </div>

          <div class="feature-item">
            <strong>高效</strong>
            <span>菜品套餐管理</span>
          </div>

          <div class="feature-item">
            <strong>清晰</strong>
            <span>经营数据分析</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 右侧：登录区域 -->
    <section class="login-section">
      <div class="login-card">
        <p class="login-eyebrow">ADMIN PORTAL</p>

        <h2>欢迎回来</h2>

        <p class="login-description">
          登录苍穹外卖管理后台
        </p>

        <form class="login-form"
        @submit.prevent="handleLogin">
          <label for="username">账号</label>
          <input
              id="username"
              v-model="loginForm.username"
              type="text"
              placeholder="请输入管理员账号"
          />

          <label for="password">密码</label>
          <input
              id="password"
              v-model="loginForm.password"
              type="password"
              placeholder="请输入登录密码"
          />

          <p class="error-message">
            {{ errorMessage }}</p>

          <button
              type="submit"
              :disabled="isLoading">
            {{isLoading ? '登录中...' : '登录管理后台'}}
          </button>
        </form>
      </div>
    </section>
  </main>
</template>
