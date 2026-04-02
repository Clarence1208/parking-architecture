export interface AuthResponse {
  token: string;
  email: string;
  role: string;
}

export interface RoleResponse {
  name: string;
  maxNumberOfBookingDays: number;
}