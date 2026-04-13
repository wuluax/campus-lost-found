<template>
  <router-view v-slot="{ Component }">
    <div class="w-full min-h-screen overflow-hidden">
      <transition name="zoom-fade" mode="out-in" appear>
        <component :is="Component" />
      </transition>
    </div>
  </router-view>
</template>

<style lang="scss">
@use './styles/index.scss';
</style>
