import { describe, it, expect, vi, beforeEach } from 'vitest';
import { authService } from '../../../src/services/auth/authService';
import { apiClient } from '../../../src/services/api/apiClient';

vi.mock('../../../src/services/api/apiClient', () => ({
    apiClient: {
        get: vi.fn(),
        post: vi.fn(),
    },
}));

describe('AuthService Specification', () => {

    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('should call /auth/login with post method and credentials', async () => {
        const credentials = { email: 'test@test.fr', password: 'password123' };

        await authService.login(credentials);

        expect(apiClient.post).toHaveBeenCalledWith('/auth/login', credentials);
    });

    it('should call /auth/register with post method and user data', async () => {
        const userData = { email: 'new@test.fr', password: '123', role: 'USER' };

        await authService.register(userData);

        expect(apiClient.post).toHaveBeenCalledWith('/auth/register', userData);
    });

    it('should fetch roles from /auth/roles with get method', async () => {
        await authService.getRoles();

        expect(apiClient.get).toHaveBeenCalledWith('/auth/roles');
    });
});