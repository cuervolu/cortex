import {check, type Update, type DownloadEvent} from '@tauri-apps/plugin-updater'
import {relaunch} from '@tauri-apps/plugin-process'
import {info} from "@tauri-apps/plugin-log"
import { AppError } from '@cortex/shared/types'

export interface UpdateProgress {
  downloaded: number
  total: number | null
}

export function useUpdater() {
  const errorHandler = useDesktopErrorHandler()

  const state = reactive({
    isUpdateAvailable: false,
    updateVersion: '',
    updateNotes: '',
    updateDate: '',
    isUpdating: false,
    progress: {
      downloaded: 0,
      total: null as number | null
    }
  })

  const validateUpdate = (update: Update): void => {
    if (!update?.version) {
      throw new AppError('Invalid update information received', {
        statusCode: 400,
        data: {update}
      })
    }
  }

  const checkForUpdates = async () => {
    try {
      await info('Checking for updates...')
      const update = await check()

      if (update) {
        validateUpdate(update)
        await info(`Update available: ${update.version}, date: ${update.date}`)
        state.isUpdateAvailable = true
        state.updateVersion = update.version
        state.updateNotes = update.body || ''
        state.updateDate = update.date || new Date().toISOString()
        return update
      }

      await info('No updates available')
      state.isUpdateAvailable = false
      return null
    } catch (error) {
      await errorHandler.handleError(error, {
        statusCode: 503,
        data: {
          action: 'check_updates',
          currentState: state
        }
      })
    }
  }

  const resetProgress = () => {
    state.progress = {
      downloaded: 0,
      total: null
    }
  }

  const handleDownloadProgress = (progress: DownloadEvent) => {
    switch (progress.event) {
      case 'Started':
        state.progress.total = progress.data.contentLength || null
        break
      case 'Progress':
        if (progress.data.chunkLength) {
          state.progress.downloaded += progress.data.chunkLength
        }
        break
      case 'Finished':
        state.isUpdating = false
        break
      default:
        throw new AppError(`Unknown download event: ${progress}`, {
          statusCode: 500,
          data: {progress}
        })
    }
  }

  const installUpdate = async () => {
    try {
      const update = await check()
      if (!update) {
        throw new AppError('No update available to install', {
          statusCode: 404,
          data: {action: 'install_update'}
        })
      }

      validateUpdate(update)
      await info(`Installing update: ${update.version}`)
      state.isUpdating = true
      resetProgress()
      await update.downloadAndInstall(handleDownloadProgress)
      await info('Update installed successfully, relaunching application...')
      await relaunch()
    } catch (error) {
      state.isUpdating = false
      resetProgress()
      await errorHandler.handleError(error, {
        statusCode: 500,
        data: {
          action: 'install_update',
          updateVersion: state.updateVersion,
          progress: state.progress
        }
      })
    }
  }

  return {
    isUpdateAvailable: computed(() => state.isUpdateAvailable),
    updateVersion: computed(() => state.updateVersion),
    updateNotes: computed(() => state.updateNotes),
    updateDate: computed(() => state.updateDate),
    isUpdating: computed(() => state.isUpdating),
    progress: computed(() => state.progress),
    checkForUpdates,
    installUpdate
  }
}