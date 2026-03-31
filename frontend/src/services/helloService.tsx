import { apiClient } from '../shared/api/apiClient.ts';

export const helloService = {
  sayHello: () => apiClient.get<string>('/hello'),
  sayHelloDb: () => apiClient.get<string>('/hello/db'),
  sayHelloRedis: () => apiClient.get<string>('/hello/redis'),
};
