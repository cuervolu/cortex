export interface User {
  id: number;
  username: string;
  email: string;
  first_name: string;
  last_name: string;
  full_name: string;
  avatar_url: string | null;
  date_of_birth: string;
  country_code: string;
  gender: string;
  account_locked: boolean;
  enabled: boolean;
  roles: string[];
}

export interface UserState {
  user: User | null;
  token: string | null;
}