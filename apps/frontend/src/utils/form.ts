// utils/form.ts
import {format, parseISO} from 'date-fns'
import type {UpdateProfileRequest} from '~/interfaces'

export const createFormDataFromProfile = (data: UpdateProfileRequest): FormData => {
  const form = new FormData()
  
  console.log('Data to transform:', data)

  Object.entries(data).forEach(([key, value]) => {
    if (value != null && value !== '' && !['password', 'confirm_password'].includes(key)) {
      switch (key) {
        case 'date_of_birth': {
          const date = value instanceof Date ? value : parseISO(value as string)
          form.append(key, format(date, 'yyyy-MM-dd'))
          break
        }

        case 'avatar':
          if (value instanceof File) {
            form.append(key, value)
          }
          break

        default:
          form.append(key, value.toString())
      }
    }
  })
  
  console.log('FormData entries:', Object.fromEntries(form.entries()))

  return form
}