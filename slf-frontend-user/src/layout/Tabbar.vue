<template>
  <div class="min-h-screen flex flex-col">
    <RouterView class="flex-1 overflow-x-hidden app-content">
      <template #default="{ Component, route }">
        <component :is="Component" :key="route.fullPath" />
      </template>
    </RouterView>
    <van-tabbar route fixed placeholder class="tabbar">
      <van-tabbar-item
        v-for="menu in getMenus"
        :key="menu.name"
        replace
        :to="menu.path"
      >
        <template #icon="{ active }">
          <div class="flex flex-col items-center">
            <i :class="menu.meta?.icon" class="text-xl transition-transform duration-200" :style="active ? 'transform: scale(1.15)' : ''" />
          </div>
        </template>
        {{ menu.meta?.title }}
      </van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { routes as routeModuleList } from '@/router/routes'

const getMenus = computed(() => routeModuleList.filter(item => item.meta?.showInTabbar))
</script>

<style scoped lang="scss">
.tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;
  width: 100%;
  padding-bottom: env(safe-area-inset-bottom);
}

:deep(.van-tabbar) {
  height: 56px;
  border-top: 1px solid #f3f4f6;
  box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.03);
}

:deep(.van-tabbar-item) {
  font-size: 11px;
  font-weight: 600;
  color: #9ca3af;
}

:deep(.van-tabbar-item--active) {
  color: #3b82f6;
}
</style>
