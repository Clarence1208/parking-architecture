import { apiClient } from '../shared/api/apiClient';
import type { AuthResponse, RoleResponse } from './interfaces/authInterface';

export const authService = {
  login: (data: any): Promise<AuthResponse> => apiClient.post('/auth/login', data),
  register: (data: any): Promise<AuthResponse> => apiClient.post('/auth/register', data),
  getRoles: (): Promise<RoleResponse[]> => apiClient.get('/auth/roles'),
};
