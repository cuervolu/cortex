<script setup lang="ts">
import OAuthProfileForm from "~/components/auth/OAuthProfileForm.vue"
import type {UpdateProfileRequest} from "~/interfaces"
import {useProfileCompletion} from "~/composables/useProfileCompletion";

const {
  loading,
  needsProfileCompletion,
  needsPassword,
  initialData,
  updateProfile,
  checkSession
} = useProfileCompletion()

onMounted(checkSession)


const handleSubmit = async (formData: UpdateProfileRequest) => {
  try {
    if (!formData) {
      console.error('No form data provided')
      return
    }

    console.log('Submitting form data:', {
      ...formData,
      password: formData.password ? '[REDACTED]' : undefined,
      confirm_password: formData.confirm_password ? '[REDACTED]' : undefined
    })

    await updateProfile(formData)
  } catch (error) {
    console.error('Error submitting form:', error)
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center p-4">
    <template v-if="needsProfileCompletion || needsPassword">
      <OAuthProfileForm
          :loading="loading"
          :initial-data="initialData"
          :needs-password="needsPassword"
          @submit="handleSubmit"
      />
    </template>
  </div>
</template>