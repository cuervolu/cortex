import { convertFileSrc } from '@tauri-apps/api/core';
import { listen, type Event, TauriEvent } from '@tauri-apps/api/event';
import { debug } from "@tauri-apps/plugin-log";
import { open } from '@tauri-apps/plugin-dialog';
import { AppError } from "@cortex/shared/types";

type DropEventPayload = {
  paths: string[];
  position: { x: number; y: number; };
};

export function useImageDrop() {
  const previewImage = ref<string | null>(null);
  const isDragging = ref(false);
  const currentImagePath = ref<string | null>(null);
  const unlisteners: (() => void)[] = [];

  const setupDragListeners = async () => {
    try {
      const unlistenEnter = await listen(TauriEvent.DRAG_ENTER, async () => {
        await debug('File drag detected');
        isDragging.value = true;
      });
      unlisteners.push(unlistenEnter);

      const unlistenLeave = await listen(TauriEvent.DRAG_LEAVE, async () => {
        await debug('Drag leave detected');
        isDragging.value = false;
      });
      unlisteners.push(unlistenLeave);

      const unlistenDrop = await listen<DropEventPayload>(TauriEvent.DRAG_DROP, handleDrop);
      unlisteners.push(unlistenDrop);

      await debug('Image drop listeners set up');
    } catch (e) {
      await debug(`Error setting up image drop events: ${e}`);
    }
  };

  const handleDrop = async (event: Event<DropEventPayload>) => {
    try {
      const [filePath] = event.payload.paths;
      if (!filePath) return;

      if (isValidImagePath(filePath)) {
        previewImage.value = convertFileSrc(filePath);
        currentImagePath.value = filePath;
        await debug(`Image loaded: ${filePath}`);
      }
    } catch (e) {
      await debug(`Error processing dropped image: ${e}`);
    } finally {
      isDragging.value = false;
    }
  };


  const handleImageUpload = async (event?: MouseEvent) => {
    if (event) event.preventDefault();

    try {
      const selectedPath = await open({
        title: 'Select an image',
        directory: false,
        multiple: false,
        filters: [{name: 'Images', extensions: ['jpg', 'jpeg', 'png', 'webp']}]
      });

      if (selectedPath) {
        previewImage.value = convertFileSrc(selectedPath as string);
        currentImagePath.value = selectedPath as string;
      }
    } catch (e) {
      throw new AppError('No image selected', { statusCode: 401, data: {e} });
    }
  };


  const cleanup = () => {
    unlisteners.forEach(unlisten => unlisten());
  };

  return {
    currentImagePath,
    previewImage,
    isDragging,
    setupDragListeners,
    handleImageUpload,
    cleanup
  };
}

function isValidImagePath(path: string): boolean {
  const lowercasePath = path.toLowerCase();
  return lowercasePath.endsWith('.jpg') ||
      lowercasePath.endsWith('.jpeg') ||
      lowercasePath.endsWith('.png') ||
      lowercasePath.endsWith('.webp');
}