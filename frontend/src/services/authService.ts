import { apiClient } from '../shared/api/apiClient';

export interface AuthResponse {
  token: string;
  email: string;
  role: string;
}

export const authService = {
  login: (data: any): Promise<AuthResponse> => apiClient.post('/auth/login', data),
  register: (data: any): Promise<AuthResponse> => apiClient.post('/auth/register', data),
};
