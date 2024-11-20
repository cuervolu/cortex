export function useExerciseUI() {
  const isSettingsOpen = ref(false)
  const isLoading = ref(true)
  const isMounted = ref(true)

  const handleSettingsClick = () => {
    isSettingsOpen.value = true
  }

  const handleBackClick = async() => {
    const router = useRouter()
    await router.push('/exercises')
  }

  return {
    isSettingsOpen,
    isLoading,
    isMounted,
    handleSettingsClick,
    handleBackClick
  }
}