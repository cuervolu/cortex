import {format} from 'date-fns'
import type {UpdateProfileRequest} from '~/interfaces'

export const createFormDataFromProfile = (data: UpdateProfileRequest): FormData => {
  const form = new FormData()

  if (!data || typeof data !== 'object') {
    console.error('Invalid data provided to createFormDataFromProfile')
    return form
  }

  // Procesamos cada campo
  Object.entries(data).forEach(([key, value]) => {
    if (value != null && value !== undefined && !['password', 'confirm_password'].includes(key)) {
      switch (key) {
        case 'dateOfBirth': {
          if (value instanceof Date) {
            form.append(key, format(value, 'yyyy-MM-dd'))
          } else if (typeof value === 'string') {
            try {
              const date = new Date(value)
              form.append(key, format(date, 'yyyy-MM-dd'))
            } catch (error) {
              console.error('Error:', error)
              console.error('Error parsing date string:', value)
            }
          }
          break
        }
        case 'avatar':
          if (value instanceof File) {
            form.append(key, value)
          }
          break
        default:
          form.append(key, String(value))
      }
    }
  })

  // Log para debug
  console.log('FormData contents:')
  for (const pair of form.entries()) {
    console.log(`${pair[0]}: ${pair[1]}`)
  }

  return form
}