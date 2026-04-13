import {
  defineConfig,
  presetAttributify,
  presetTypography,
  presetUno,
  presetWebFonts,
} from 'unocss'
import presetIcons from '@unocss/preset-icons'
import presetRemToPx from '@unocss/preset-rem-to-px'
import transformerVariantGroup from '@unocss/transformer-variant-group'
import transformerDirectives from '@unocss/transformer-directives'

export default defineConfig({
  presets: [
    presetUno(),
    presetRemToPx({ baseFontSize: 16 }),
    presetIcons({
      extraProperties: {
        'display': 'inline-block',
        'vertical-align': 'middle',
      },
    }),
    presetAttributify(),
    presetTypography(),
    presetWebFonts({
      provider: 'google',
      fonts: { mono: ['Fira Code'] },
    }),
  ],
  transformers: [
    transformerVariantGroup(),
    transformerDirectives(),
  ],
  safelist: [
    'i-simple-icons:atlassian',
    'i-simple-icons:soundcharts',
    'i-simple-icons:docsify',
    'i-material-symbols:award-star',
    'i-material-symbols:add-box',
    'i-material-symbols:person',
    'i-material-symbols:chat',
    'i-tabler:message-chatbot-filled',
    'i-tabler:home-filled',
    'i-solar:home-2-bold',
    'i-solar:box-bold', 
    'i-ph:chat-circle-dots-fill',
  ],
})
