<template>
  <div class="min-h-screen bg-[#f8f9fc] pb-24">
    <!-- 顶部区域 -->
    <div class="bg-gradient-to-br from-blue-500 to-blue-600 pt-10 pb-14 px-5 relative overflow-hidden">
      <div class="absolute top-0 right-0 w-40 h-40 bg-white/10 rounded-full -mr-16 -mt-16" />
      <div class="absolute bottom-0 left-0 w-24 h-24 bg-white/5 rounded-full -ml-10 -mb-10" />
      <h1 class="text-[22px] font-bold text-white m-0 relative z-10">功能中心</h1>
      <p class="text-white/70 text-sm mt-1 relative z-10">发布物品、AI寻物、校园地图</p>
    </div>

    <div class="px-4 -mt-8 relative z-10 space-y-3">

      <!-- 快捷操作 -->
      <div class="bg-white rounded-2xl shadow-sm p-4 grid grid-cols-3 gap-2">
        <router-link
          to="/feature/publish-lost"
          class="flex flex-col items-center gap-2 py-3 rounded-xl active:bg-gray-50 transition-colors"
        >
          <div class="w-11 h-11 rounded-full bg-rose-50 flex items-center justify-center">
            <i class="i-ph:package-bold text-xl text-rose-500" />
          </div>
          <span class="text-xs font-bold text-gray-700">发布失物</span>
        </router-link>

        <router-link
          to="/feature/publish-found"
          class="flex flex-col items-center gap-2 py-3 rounded-xl active:bg-gray-50 transition-colors"
        >
          <div class="w-11 h-11 rounded-full bg-emerald-50 flex items-center justify-center">
            <i class="i-ph:hand-heart-bold text-xl text-emerald-500" />
          </div>
          <span class="text-xs font-bold text-gray-700">发布招领</span>
        </router-link>

        <router-link
          to="/feature/ai"
          class="flex flex-col items-center gap-2 py-3 rounded-xl active:bg-gray-50 transition-colors"
        >
          <div class="w-11 h-11 rounded-full bg-violet-50 flex items-center justify-center">
            <i class="i-ph:sparkle-fill text-xl text-violet-500" />
          </div>
          <span class="text-xs font-bold text-gray-700">AI 助手</span>
        </router-link>
      </div>

      <!-- AI 智能寻物 -->
      <router-link
        to="/feature/ai"
        class="block bg-gradient-to-r from-violet-500 to-purple-600 rounded-2xl p-4 shadow-sm active:scale-[0.99] transition-all duration-200 relative overflow-hidden"
      >
        <div class="absolute top-0 right-0 w-28 h-28 bg-white/10 rounded-full -mr-10 -mt-10" />
        <div class="flex items-center gap-3 relative z-10">
          <div class="w-12 h-12 rounded-2xl bg-white/20 backdrop-blur-sm flex items-center justify-center">
            <i class="i-ph:sparkle-fill text-2xl text-white" />
          </div>
          <div class="flex-1">
            <div class="text-[15px] font-bold text-white">AI 智能寻物助手</div>
            <div class="text-xs text-white/70 mt-0.5">上传照片，AI自动匹配相似物品</div>
          </div>
          <i class="i-ph:caret-right-bold text-white/50 text-sm" />
        </div>
      </router-link>

      <!-- 校园地图 -->
      <div class="bg-white rounded-2xl shadow-sm overflow-hidden">
        <div class="p-4 flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-full bg-sky-50 flex items-center justify-center">
              <i class="i-ph:map-trifold-bold text-lg text-sky-500" />
            </div>
            <div>
              <div class="text-[15px] font-bold text-gray-800">南苑校本部地图</div>
              <div class="text-[11px] text-gray-400 mt-0.5">更新时间：2025年12月1日</div>
            </div>
          </div>
          <a :href="mapUrl" download class="no-underline">
            <van-button
              size="small" round
              class="!h-7 !px-3 !text-[11px] !font-bold !text-sky-600 !bg-sky-50 !border-0"
            >
              下载
            </van-button>
          </a>
        </div>

        <div class="relative cursor-pointer" @click="openPreview">
          <van-image
            :src="mapUrl"
            fit="cover"
            width="100%"
            lazy-load
            class="block bg-gray-50"
          />
          <div class="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent pointer-events-none" />
          <div class="absolute bottom-3 left-1/2 -translate-x-1/2 bg-white/90 backdrop-blur-sm text-gray-600 text-[11px] px-3 py-1 rounded-full font-medium shadow-sm">
            点击查看大图
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { showImagePreview } from 'vant'

const mapUrl = '/map.jpg'

const openPreview = () => {
  showImagePreview({
    images: [mapUrl],
    closeable: true,
    showIndex: false,
  })
}
</script>
