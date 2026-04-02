const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api';

export const apiClient = {
  get: async <T>(endpoint: string): Promise<T> => {
    const url = `${BASE_URL}${endpoint}`;
    const token = localStorage.getItem('token');
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, {
      method: 'GET',
      headers,
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
  post: async <T>(endpoint: string, data: any): Promise<T> => {
    const url = `${BASE_URL}${endpoint}`;
    const token = localStorage.getItem('token');
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, {
      method: 'POST',
      headers,
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      let errorMsg = `HTTP error! status: ${response.status}`;
      try {
        const errorData = await response.json();
        // Fallback sequence: try message first, then error, finally default
        errorMsg = errorData.message || errorData.error || errorMsg;
      } catch (e) {
        // ignore parse error, fallback to default status text
      }
      throw new Error(errorMsg);
    }

    // Gestion propre de la réponse (JSON ou vide)
    const contentType = response.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
      return response.json();
    }
    return {} as T; 
  },
  delete: async <T>(endpoint: string): Promise<T> => {
    const url = `${BASE_URL}${endpoint}`;
    const token = localStorage.getItem('token');
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };
    
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, {
      method: 'DELETE',
      headers,
    });

    if (!response.ok) {
      let errorMsg = `HTTP error! status: ${response.status}`;
      try {
        const errorData = await response.json();
        errorMsg = errorData.message || errorData.error || errorMsg;
      } catch (e) {
      }
      throw new Error(errorMsg);
    }

    const contentType = response.headers.get("content-type");
    if (response.status !== 204 && contentType && contentType.includes("application/json")) {
      return response.json();
    }
    
    return {} as T;
  },
};