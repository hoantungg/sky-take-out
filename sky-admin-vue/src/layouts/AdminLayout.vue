<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const handlelogout = () => {
  localStorage.removeItem('sky-admin-token')
  router.push({
    name: 'login'
  })
}
</script>


<template>
  <div class="admin-layout">
    <!-- 左侧菜单 -->
    <aside class="sidebar">
      <h2 class="brand">苍穹外卖</h2>

      <nav class="menu">
        <RouterLink to="/dashboard">
          工作台
        </RouterLink>
      </nav>
    </aside>

    <!-- 右侧区域 -->
    <div class="main-area">
      <header class="topbar">
        <span>门店管理</span>
        <div>
          <span>管理端</span>

          <button
              type="button"
              @click="handlelogout">
            退出登录
          </button>
        </div>
      </header>

      <section class="page-content">
        <RouterView />
      </section>
    </div>
  </div>
</template>