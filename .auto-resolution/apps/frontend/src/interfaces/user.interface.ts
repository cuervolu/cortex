/**
 * In this file we define all the interfaces that the User entity will use.
 *
 * **/

import {parseISO} from "date-fns";

/**
 * Represents a User entity.
 * It contains basic user information like username, email, roles, and providers.
 */
export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  password: string;
  accountLocked: boolean;
  enabled: boolean;
  providers: Set<string>;
  externalId?: string;
  avatar?: string;
  dateOfBirth?: Date;
  roles: Role[];
}

/**
 * Represents a Role entity.
 * Each user can be associated with one or more roles.
 */
export interface Role {
  id: number;
  name: string;
}

/**
 * Represents the relationship between a User and an Achievement.
 * It contains information about when the user obtained the achievement.
 */
export interface UserAchievement {
  id: number;
  user: User;
  achievement: Achievement;
  obtainedDate: Date;
}

/**
 * Represents an Achievement entity.
 * Each achievement has a name, type, points, and optional description and condition.
 */
export interface Achievement {
  id: number;
  type: AchievementType;
  name: string;
  description?: string;
  points: number;
  condition?: string;
}

/**
 * Represents the type of Achievement.
 * Each achievement type has a name, description, and an optional icon URL.
 */
export interface AchievementType {
  id: number;
  name: string;
  description?: string;
  iconUrl?: string;
}

export interface UpdateProfileRequest {
  first_name?: string
  last_name?: string
  username?: string
  email?: string
  date_of_birth?: Date
  country_code?: string
  gender?: Gender
  password?: string
  confirm_password?: string
  avatar?: File | null
}


export interface UserResponse {
  id: number
  username: string
  email: string
  gender: string | null
  enabled: boolean
  roles: string[]
  first_name: string | null
  last_name: string | null
  full_name: string
  avatar_url: string | null
  date_of_birth: string | null
  country_code: string | null
  account_locked: boolean
  created_at: string
  updated_at: string
  has_password: boolean
}


export type Gender = 'MALE' | 'FEMALE' | 'OTHER' | 'NON_BINARY' | 'PREFER_NOT_TO_SAY'

export const transformSessionToProfileRequest = (sessionData: any): UpdateProfileRequest => {
  if (!sessionData) {
    throw new Error('No session data provided')
  }


  let dateOfBirth: Date | undefined

  if (sessionData.date_of_birth) {
    try {
      if (typeof sessionData.date_of_birth === 'string') {
        dateOfBirth = parseISO(sessionData.date_of_birth)
      }
      else if (sessionData.date_of_birth instanceof Date) {
        dateOfBirth = sessionData.date_of_birth
      }
    } catch (error) {
      console.error('Error parsing date of birth:', error)
      dateOfBirth = undefined
    }
  }

  return {
    username: sessionData.username || '',
    first_name: sessionData.first_name,
    last_name: sessionData.last_name,
    email: sessionData.email || '',
    date_of_birth: dateOfBirth,
    country_code: sessionData.country_code,
    gender: sessionData.gender || 'PREFER_NOT_TO_SAY',
  }
}

export const genderLabels: Record<Gender, string> = {
  MALE: 'Masculino',
  FEMALE: 'Femenino',
  NON_BINARY: 'No binario',
  OTHER: 'Otro',
  PREFER_NOT_TO_SAY: 'Prefiero no decirlo'
};

export const useGender = () => {
  const translateGender = (gender: Gender | null | undefined): string => {
    if (!gender) return 'No especificado';
    return genderLabels[gender] || 'No especificado';
  };

  return {
    translateGender
  };
};
