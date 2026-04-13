<template>
  <div
    class="bg-white rounded-2xl p-3 shadow-sm active:scale-[0.99] transition-all duration-200"
    @click="emit('click')"
  >
    <div class="flex gap-3">
      <div class="relative w-28 h-28 shrink-0 bg-gray-50 rounded-xl overflow-hidden">
        <van-image
          :src="item.imageUrl || '/school.jpg'"
          width="100%"
          height="100%"
          fit="cover"
        />
        <div
          class="absolute top-1.5 left-1.5 px-1.5 py-0.5 rounded-md text-[10px] font-bold text-white shadow-sm"
          :class="item.type === 0 ? 'bg-rose-500' : 'bg-emerald-500'"
        >
          {{ item.type === 0 ? '失物' : '招领' }}
        </div>
      </div>
      <div class="flex-1 min-w-0 flex flex-col justify-between py-0.5">
        <div>
          <div class="text-[15px] text-gray-900 font-bold leading-snug line-clamp-2 mb-2">
            {{ item.description || '暂无详细描述...' }}
          </div>
          <div class="flex flex-wrap gap-1.5">
            <span class="inline-flex items-center px-2 py-0.5 rounded-md bg-blue-50 text-blue-600 text-[10px] font-bold">
              {{ categoryName || '未分类' }}
            </span>
            <span class="inline-flex items-center px-2 py-0.5 rounded-md bg-gray-50 text-gray-500 text-[10px] font-medium">
              {{ locationName || '未指定地点' }}
            </span>
          </div>
        </div>
        <div class="flex items-center justify-between mt-auto pt-1.5">
          <div class="flex items-center text-[11px] text-gray-400">
            <span>{{ formatDateTime(item.itemTime) }}</span>
            <span class="mx-1">·</span>
            <span>{{ item.type === 0 ? '丢失' : '拾到' }}</span>
          </div>
        </div>
      </div>
    </div>
    <div v-if="$slots.action" class="border-t border-gray-50 mt-3 pt-2.5" @click.stop>
      <slot name="action" />
    </div>
  </div>
</template>

<script setup>
import { formatDateTime } from '@/utils/date'

defineOptions({ name: 'ItemCard' })
defineProps({
  item: { type: Object, required: true },
  categoryName: { type: String, default: '' },
  locationName: { type: String, default: '' },
})
const emit = defineEmits(['click'])
</script>
