/**
 * In this file we define all the interfaces that the User entity will use.
 *
 * **/

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
