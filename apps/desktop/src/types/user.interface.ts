export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  avatarUrl: string | null;
  dateOfBirth: string;
  countryCode: string;
  gender: string;
  accountLocked: boolean;
  enabled: boolean;
  roles: string[];
}

export interface UserState {
  user: User | null;
  token: string | null;
}