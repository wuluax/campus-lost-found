// Unocss 全局配置文件：用于定义原子化样式的预设与行为
// 官方文档：https://unocss.dev/
import { defineConfig, presetUno } from 'unocss'
import presetIcons from '@unocss/preset-icons'
import epIcons from '@iconify-json/ep/icons.json' with { type: 'json' }

export default defineConfig({
  // 预设说明：
  // - presetUno：基础原子化样式（如 flex、grid、p-4 等）
  presets: [
    presetUno(),
    presetIcons({
      collections: {
        ep: epIcons,
      },
    }),
  ],
  // 可选：在此处扩展自定义规则、变体、主题等
})
