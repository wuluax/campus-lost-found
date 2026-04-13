<template>
  <van-nav-bar
    :title="title || getTitle"
    left-arrow
    @click-left="router.back"
    class="!bg-white/90 backdrop-blur-md border-b border-gray-100"
  >
    <template #right>
      <slot name="right" />
    </template>
  </van-nav-bar>
</template>

<script setup>
defineProps({
  title: { type: String, default: '' },
})

const router = useRouter()
const currentRoute = useRoute()
const getTitle = computed(() => String(currentRoute.meta.title || ''))
</script>

<style scoped>
:deep(.van-nav-bar) {
  --van-nav-bar-height: 48px;
}
:deep(.van-nav-bar__title) {
  font-weight: 700;
  font-size: 16px;
  color: #111827;
}
:deep(.van-nav-bar__arrow) {
  color: #374151;
  font-size: 18px;
}
</style>
