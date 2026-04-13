<template>
  <div class="min-h-screen bg-[#F5F7FA] pb-10">
    <NavBar :title="isEditMode ? '编辑物品' : (itemType === 0 ? '发布失物' : '发布招领')" />

    <van-form ref="formRef" @submit="handleSubmit" class="mt-4 space-y-4">
      
      <div class="mx-4 bg-white rounded-xl shadow-sm overflow-hidden p-5">

        <van-field
          v-model="formData.description"
          rows="3"
          autosize
          type="textarea"
          maxlength="300"
          placeholder="描述物品特征、品牌、颜色..."
          show-word-limit
          :rules="[{ required: true, message: '请输入描述' }]"
          class="!p-0 !text-[16px] !leading-relaxed mb-6 font-medium text-gray-800 placeholder:text-gray-300"
        />
        
        <div class="flex flex-wrap gap-3">
          <van-uploader
            v-model="fileList"
            :max-count="1"
            :max-size="2 * 1024 * 1024"
            @oversize="onOversize"
            :before-read="beforeRead"
            :after-read="afterRead"
            preview-size="80px"
          >
            <div class="w-[80px] h-[80px] bg-gray-50 rounded-lg flex items-center justify-center active:bg-gray-100 transition-colors border border-gray-100">
              <i class="i-ph:plus-bold text-xl text-gray-400" />
            </div>
          </van-uploader>
          
          <div v-if="fileList.length === 0" class="flex items-center text-gray-300 text-xs">
            <i class="i-ph:image mr-1"></i>
            <span>添加图片有助于匹配</span>
          </div>
        </div>
      </div>

      <van-cell-group inset :border="false" class="!mx-4 !rounded-xl !shadow-sm !overflow-hidden">
        
        <van-field
          v-model="categoryText"
          readonly
          placeholder="选择分类"
          :rules="[{ required: true, message: '请选择分类' }]"
          @click="showCategoryPicker = true"
          is-link
          class="!py-5 items-center"
        >
          <template #label>
            <div class="flex items-center text-gray-900 font-bold min-w-[4em]">
              <i class="i-ph:squares-four text-lg text-gray-400 mr-2" />
              分类
            </div>
          </template>
        </van-field>

        <van-field
          v-model="locationText"
          readonly
          placeholder="选择地点"
          :rules="[{ required: true, message: '请选择地点' }]"
          @click="showLocationPicker = true"
          is-link
          class="!py-5 items-center"
        >
          <template #label>
            <div class="flex items-center text-gray-900 font-bold min-w-[4em]">
              <i class="i-ph:map-pin text-lg text-gray-400 mr-2" />
              地点
            </div>
          </template>
        </van-field>

        <van-field
          v-model="itemTimeText"
          readonly
          placeholder="选择丢/拾时间"
          :rules="[
            { required: true, message: '请选择丢/拾时间' },
            { validator: validateItemTime, message: '时间无效' }
          ]"
          @click="showDatePicker = true"
          is-link
          class="!py-5 items-center"
        >
          <template #label>
            <div class="flex items-center text-gray-900 font-bold min-w-[4em]">
              <i class="i-ph:clock text-lg text-gray-400 mr-2" />
              时间
            </div>
          </template>
        </van-field>

      </van-cell-group>

      <van-cell-group inset :border="false" class="!mx-4 !rounded-xl !shadow-sm !overflow-hidden">
        <van-field
          v-model="formData.contact"
          placeholder="微信号 / 手机号"
          :rules="[{ max: 50, message: '字符过长' }]"
          class="!py-5 items-center"
        >
          <template #label>
            <div class="flex items-center text-gray-900 font-bold min-w-[4em]">
              <i class="i-ph:chat-circle-dots text-lg text-gray-400 mr-2" />
              联系方式
            </div>
          </template>
        </van-field>
      </van-cell-group>

      <div class="px-6 pt-6 pb-10">
        <van-button 
          block 
          round 
          native-type="submit" 
          :loading="loading"
          class="!h-[50px] !bg-gray-600 !border-gray-600 !text-white !font-bold !text-[15px] shadow-lg shadow-gray-200 active:!opacity-80"
        >
          {{ isEditMode ? '保存变更' : '立即发布' }}
        </van-button>
      </div>

    </van-form>

    <van-action-sheet
      v-model:show="showCategoryPicker"
      title="选择分类"
      :actions="categoryActions"
      close-on-click-action
      cancel-text="取消"
      @select="onCategorySelect"
    />

    <van-action-sheet
      v-model:show="showLocationPicker"
      title="选择地点"
      :actions="locationActions"
      close-on-click-action
      cancel-text="取消"
      @select="onLocationSelect"
    />

    <van-popup v-model:show="showDatePicker" position="bottom" round safe-area-inset-bottom>
      <van-picker-group
        title="时间"
        :tabs="['日期', '时间']"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      >
        <van-date-picker
          v-model="dateValues"
          :min-date="minDate"
          :max-date="maxDate"
        />
        <van-time-picker v-model="timeValues" />
      </van-picker-group>
    </van-popup>
  </div>
</template>

<script setup>

import { showFailToast, showSuccessToast } from 'vant'
import { createItem, getItemDetail, updateItem } from '@/api/item'
import { uploadItemImage } from '@/api/common'
import NavBar from '@/layout/NavBar.vue'
import { useDictMap } from '@/hooks/useDictMap'
import { formatDateTime } from '@/utils/date'

const router = useRouter()
const route = useRoute()
const itemType = computed(() => route.path.includes('lost') ? 0 : 1)
const editId = computed(() => route.query?.editId)
const isEditMode = computed(() => !!editId.value)
const formRef = ref()
const loading = ref(false)
const formData = reactive({
  type: itemType.value,
  description: '',
  categoryId: '',
  locationId: '',
  itemTime: '',
  contact: '',
  imageUrl: ''
})

const showCategoryPicker = ref(false)
const showLocationPicker = ref(false)
const showDatePicker = ref(false)
const {
  categoryOptions,
  locationOptions,
  categoryNameMap,
  locationNameMap,
  loadDicts,
} = useDictMap()
const minDate = ref(new Date(new Date().getFullYear() - 10, 0, 1))
const maxDate = ref(new Date())
const dateValues = ref([
  String(maxDate.value.getFullYear()),
  String(maxDate.value.getMonth() + 1).padStart(2, '0'),
  String(maxDate.value.getDate()).padStart(2, '0')
])
const timeValues = ref([
  String(maxDate.value.getHours()).padStart(2, '0'),
  String(maxDate.value.getMinutes()).padStart(2, '0')
])
const fileList = ref([])
const categoryText = ref('')
const locationText = ref('')
const itemTimeText = ref('')

const loadOptions = async () => {
  try {
    await loadDicts()
  } catch (error) {
    console.error('加载选项失败:', error)
    showFailToast('加载选项失败')
  }
}

const categoryActions = computed(() => {
  return categoryOptions.value.map(option => ({
    name: option.text,
    value: option.value,
  }))
})

const locationActions = computed(() => {
  return locationOptions.value.map(option => ({
    name: option.text,
    value: option.value,
  }))
})

const onCategorySelect = (action) => {
  const selectedValue = action?.value
  categoryText.value = action?.name || ''
  formData.categoryId = selectedValue ?? ''
  showCategoryPicker.value = false
}
const onLocationSelect = (action) => {
  const selectedValue = action?.value
  locationText.value = action?.name || ''
  formData.locationId = selectedValue ?? ''
  showLocationPicker.value = false
}
const onDateConfirm = () => {
  const [year, month, day] = dateValues.value.map(v => Number(v))
  const [hour, minute] = timeValues.value.map(v => Number(v))
  const date = new Date(year, month - 1, day, hour, minute)
  formData.itemTime = formatLocalDateTimeForApi(date)
  itemTimeText.value = formatDateTime(date)
  showDatePicker.value = false
}
const formatLocalDateTimeForApi = (date) => {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const hh = String(date.getHours()).padStart(2, '0')
  const mm = String(date.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${d}T${hh}:${mm}:00`
}
const onOversize = () => {
  showFailToast('图片大小不能超过2MB')
}

const validateItemTime = (val) => {
  if (!val) return true
  const now = new Date()
  const selected = new Date(formData.itemTime)
  return selected < now
}
const beforeRead = (file) => {
  if (file.type.indexOf('image/') !== 0) {
    showFailToast('请上传图片文件')
    return false
  }
  return true
}
const afterRead = async (file) => {
  try {
    if (!file.file) return
    const url = await uploadItemImage(file.file)
    file.url = url
    formData.imageUrl = url
  } catch (e) {
    console.error('图片上传失败:', e)
    showFailToast('图片上传失败')
  }
}
const handleSubmit = async () => {
  try {
    loading.value = true
    if (!formData.imageUrl || !String(formData.imageUrl).trim()) {
      formData.imageUrl = null
    }
    if (isEditMode.value) {
      await updateItem(editId.value, formData)
      showSuccessToast('修改成功')
    } else {
      await createItem(formData)
      showSuccessToast('发布成功')
    }
    router.back()
  } catch (error) {
    console.error('发布失败:', error)
  } finally {
    loading.value = false
  }
}
onMounted(async () => {
  await loadOptions()
  if (isEditMode.value) {
    try {
      const detail = await getItemDetail(editId.value)
      formData.type = detail.item.type
      formData.description = detail.item.description || ''
      formData.categoryId = detail.item.categoryId
      formData.locationId = detail.item.locationId
      formData.itemTime = detail.item.itemTime
      formData.contact = detail.item.contact || ''
      fileList.value = detail.item?.imageUrl ? [{ url: detail.item.imageUrl }] : []
      itemTimeText.value = formatDateTime(new Date(detail.item.itemTime))
      const itemDate = new Date(detail.item.itemTime)
      dateValues.value = [
        String(itemDate.getFullYear()),
        String(itemDate.getMonth() + 1).padStart(2, '0'),
        String(itemDate.getDate()).padStart(2, '0')
      ]
      timeValues.value = [
        String(itemDate.getHours()).padStart(2, '0'),
        String(itemDate.getMinutes()).padStart(2, '0')
      ]
      categoryText.value = categoryNameMap.value.get(formData.categoryId) || ''
      locationText.value = locationNameMap.value.get(formData.locationId) || ''
    } catch (e) {
      console.error('加载详情失败:', e)
      showFailToast('加载详情失败')
    }
  }
})
</script>

<style scoped>
/* 样式穿透：让 Van Field 的输入框字体变黑变大，更像标题 */
:deep(.van-field__control) {
  color: #111827; /* gray-900 */
}

/* 调整 cell 的边框，使其不那么明显 */
:deep(.van-cell:after) {
  border-bottom-color: #f3f4f6;
  left: 16px;
  right: 16px;
}
</style>
