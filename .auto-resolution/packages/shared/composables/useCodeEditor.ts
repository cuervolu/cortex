import { ref, computed } from 'vue';
import { useColorMode } from '@vueuse/core';
import { materialDark, materialLight } from '@cortex/shared/themes';

export function useCodeEditor() {
  const colorMode = useColorMode();
  const availableExtensions = [
    'lineNumbersRelative',
    'indentationMarkers',
    'interact',
  ];
  const availableThemes = { materialLight, materialDark };
  const activeExtensions = ref([
    'lineNumbersRelative',
    'indentationMarkers',
    'interact',
  ]);

  const editorTheme = computed(() =>
    colorMode.value === 'light' ? 'materialLight': 'materialDark'
  );

  return {
    availableExtensions,
    availableThemes,
    activeExtensions,
    editorTheme,
  };
}
