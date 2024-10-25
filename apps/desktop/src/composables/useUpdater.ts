import { check } from '@tauri-apps/plugin-updater'
import { relaunch } from '@tauri-apps/plugin-process'
import {info, error as logError} from "@tauri-apps/plugin-log";


export interface UpdateProgress {
  downloaded: number
  total: number | null
}

export function useUpdater() {
  const isUpdateAvailable = ref(false)
  const updateVersion = ref('')
  const updateNotes = ref('')
  const updateDate = ref('')
  const isUpdating = ref(false)
  const progress = ref<UpdateProgress>({
    downloaded: 0,
    total: null
  })

  const checkForUpdates = async () => {
    try {
      const update = await check()
      if (update) {
        await info(`Update available: ${update.version}`)
        isUpdateAvailable.value = true
        updateVersion.value = update.version
        updateNotes.value = update.body || ''
        updateDate.value = update.date || new Date().toISOString()
      }else{
        await info('No updates available')
      }
      return update
    } catch (error) {
     await logError(`Error checking for updates: ${error}`)
      return null
    }
  }

  const installUpdate = async () => {
    try {
      const update = await check()
      if (!update) return
      await info(`Installing update: ${update.version}`)

      isUpdating.value = true
      progress.value = { downloaded: 0, total: null }

      await update.downloadAndInstall((event) => {
        switch (event.event) {
          case 'Started':
            progress.value.total = event.data.contentLength || null
            break
          case 'Progress':
            progress.value.downloaded += event.data.chunkLength
            break
          case 'Finished':
            isUpdating.value = false
            break
        }
      })

      await info('Update installed, relaunching app...')
      await relaunch()
    } catch (error) {
      await logError(`Error installing update: ${error}`)
      isUpdating.value = false
    }
  }

  return {
    isUpdateAvailable,
    updateVersion,
    updateNotes,
    updateDate,
    isUpdating,
    progress,
    checkForUpdates,
    installUpdate
  }
}