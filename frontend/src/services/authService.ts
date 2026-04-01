import { apiClient } from '../shared/api/apiClient';

export interface AuthResponse {
  token: string;
  email: string;
  role: string;
}

export interface RoleResponse {
  name: string;
  maxNumberOfBookingDays: number;
}

export const authService = {
  login: (data: any): Promise<AuthResponse> => apiClient.post('/auth/login', data),
  register: (data: any): Promise<AuthResponse> => apiClient.post('/auth/register', data),
  getRoles: (): Promise<RoleResponse[]> => apiClient.get('/auth/roles'),
};
