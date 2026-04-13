import { computed, ref } from 'vue'
import { getCategoryList } from '@/api/category'
import { getLocationList } from '@/api/location'

export function useDictMap(options = {}) {
  const includeAllOption = Boolean(options.includeAllOption)
  const categoryOptions = ref([])
  const locationOptions = ref([])
  const categoryNameMap = ref(new Map())
  const locationNameMap = ref(new Map())
  const loading = ref(false)

  const normalizeId = (val) => {
    if (val === null || val === undefined || val === '') return null
    const num = Number(val)
    return Number.isNaN(num) ? String(val) : num
  }

  const buildOptions = (list) => {
    const options = Array.isArray(list)
      ? list.map((item) => ({
          text: item?.name || '-',
          value: normalizeId(item?.id),
          raw: item,
        }))
      : []
    if (includeAllOption) {
      return [{ text: '全部', value: '' }, ...options]
    }
    return options
  }

  const buildMap = (list) => {
    const map = new Map()
    if (Array.isArray(list)) {
      list.forEach((item) => {
        const id = normalizeId(item?.id)
        if (id !== null) {
          map.set(id, item?.name || '')
        }
      })
    }
    return map
  }

  const loadDicts = async () => {
    if (loading.value) return
    loading.value = true
    try {
      const [categories, locations] = await Promise.all([getCategoryList(), getLocationList()])
      categoryOptions.value = buildOptions(categories)
      locationOptions.value = buildOptions(locations)
      categoryNameMap.value = buildMap(categories)
      locationNameMap.value = buildMap(locations)
    } finally {
      loading.value = false
    }
  }

  const getCategoryName = (id) => {
    const key = normalizeId(id)
    if (key === null) return ''
    return categoryNameMap.value.get(key) || ''
  }

  const getLocationName = (id) => {
    const key = normalizeId(id)
    if (key === null) return ''
    return locationNameMap.value.get(key) || ''
  }

  const hasOptions = computed(() => ({
    categories: categoryOptions.value.length > 0,
    locations: locationOptions.value.length > 0,
  }))

  return {
    categoryOptions,
    locationOptions,
    categoryNameMap,
    locationNameMap,
    loading,
    hasOptions,
    loadDicts,
    getCategoryName,
    getLocationName,
  }
}
