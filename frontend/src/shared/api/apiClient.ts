const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api';

export const apiClient = {
  get: async <T>(endpoint: string): Promise<T> => {
    const url = `${BASE_URL}${endpoint}`;
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const contentType = response.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
      return response.json();
    }
    return response.text() as unknown as T;
  },

};