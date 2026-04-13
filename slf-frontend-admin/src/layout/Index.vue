<script setup>
// 主布局：左侧菜单 + 顶部退出 + 右侧内容区
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getCurrentUser, logout } from '../utils/auth'
import { adminLogout } from '@/api/user'

const router = useRouter()
const route = useRoute()

// 从本地存储中获取当前已登录的管理员用户信息
const currentUser = computed(() => getCurrentUser())

// 退出登录：调用后端注销接口并清除本地令牌
function handleLogout() {
  ElMessageBox.confirm('确定退出管理端并返回登录页？', '提示', {
    type: 'warning',
    confirmButtonText: '退出',
    cancelButtonText: '取消',
  })
    .then(async () => {
      try {
        await adminLogout()
      } catch (e) {}
      logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    })
    .catch(() => {})
}

// 菜单跳转：Element Plus 的 el-menu 配合 router 属性可自动路由跳转
</script>

<template>
  <el-container class="h-full">
    <!-- 侧边栏菜单 -->
    <el-aside class="w-[220px] border-r border-solid border-[#ebeef5]">
      <div class="px-3 py-4 flex items-center gap-2">
        <img src="/nfu.png" alt="nfu" class="w-7 h-7 object-contain" />
        <span class="font-600 text-[#374151]">！失物招领管理系统</span>
      </div>
      <el-menu :default-active="route.path" router class="border-0">
        <el-menu-item index="/users">
          <span class="i-ep-user mr-2" aria-hidden="true"></span>
           用户管理
        </el-menu-item>
        <el-menu-item index="/items">
          <span class="i-ep-box mr-2" aria-hidden="true"></span>
          物品管理
        </el-menu-item>
        <el-menu-item index="/clues">
          <span class="i-ep-chat-dot-round mr-2" aria-hidden="true"></span>
          线索管理
        </el-menu-item>
        <el-menu-item index="/categories">
          <span class="i-ep-collection-tag mr-2" aria-hidden="true"></span>
          分类管理
        </el-menu-item>
        <el-menu-item index="/locations">
          <span class="i-ep-location-information mr-2" aria-hidden="true"></span>
          地点管理
        </el-menu-item>
        <el-menu-item index="/messages">
          <span class="i-ep-message mr-2" aria-hidden="true"></span>
          私信管理
        </el-menu-item>
        <el-menu-item index="/notifications">
          <span class="i-ep-bell mr-2" aria-hidden="true"></span>
          通知管理
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 顶部 + 主内容 -->
    <el-container>
      <el-header class="flex items-center justify-end h-[56px]! border-b border-solid border-[#ebeef5] bg-white px-6 shadow-sm z-10">
        <div class="flex items-center gap-3 mr-4">
          <el-avatar 
            :size="34" 
            class="!bg-[#eef5fe] !text-[#409eff] !text-sm font-bold border border-[#d9ecff]"
            :src="currentUser?.avatar || 'cat.jpeg'"
          />
          <div class="flex flex-col justify-center">
            <span class="text-sm font-medium text-[#303133] leading-tight">
              {{ currentUser?.nickname }}
            </span>
            <span class="text-[11px] text-[#909399] leading-tight mt-0.5">
              {{ currentUser?.phone }}
            </span>
          </div>
        </div>
        <div class="h-5 w-[1px] bg-[#ebeef5] mr-4"></div>
        <el-button 
          type="danger" 
          plain 
          size="small" 
          class="!rounded-full !px-4"
          @click="handleLogout"
        >
          <span class="i-ep-switch-button mr-1"></span>
          退出登录
        </el-button>
      </el-header>
      <el-main class="p-4 bg-[#f5f7fa]">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
