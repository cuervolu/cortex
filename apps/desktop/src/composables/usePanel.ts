import { ref } from 'vue';

export function usePanel() {
  const isPanelOpen = ref(true);

  const handleSettingsClick = () => {
    isPanelOpen.value = !isPanelOpen.value;
  };

  return {
    isPanelOpen,
    handleSettingsClick
  };
}
