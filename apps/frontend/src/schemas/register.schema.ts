import { z } from 'zod'
import { toTypedSchema } from '@vee-validate/zod'

export const registerSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  username: z.string().min(4).max(20, 'Username must be between 4 and 20 characters'),
  firstname: z.string().min(2, 'First name must be at least 2 characters'),
  lastname: z.string().min(2, 'Last name must be at least 2 characters'),
  date_of_birth: z.date().max(new Date(), 'Date of birth must be in the past'),
  country_code: z.string().min(2).max(3, 'Country code must be 2 or 3 characters'),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER', 'NON_BINARY', 'PREFER_NOT_TO_SAY'], {
    errorMap: () => ({ message: 'Invalid gender' })
  })
})

export const formSchema = toTypedSchema(registerSchema)

export type RegisterSchemaType = z.infer<typeof registerSchema>

export const stepSchemas = [
  // Step 1: Account Information
  toTypedSchema(registerSchema.pick({
    email: true,
    password: true,
    username: true
  })),

  // Step 2: Personal Information
  toTypedSchema(registerSchema.pick({
    firstname: true,
    lastname: true,
    date_of_birth: true
  })),

  // Step 3: Additional Details
  toTypedSchema(registerSchema.pick({
    country_code: true,
    gender: true
  }))
]

export type RegisterStepSchemaType = typeof stepSchemas[number]