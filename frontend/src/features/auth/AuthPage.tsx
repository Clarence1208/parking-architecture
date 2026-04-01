import React, { useState } from 'react';
import { useAuth } from '../../store/AuthContext';
import { authService } from '../../services/authService';
import './AuthPage.css';

export const AuthPage: React.FC = () => {
    const { login } = useAuth();
    const [isLogin, setIsLogin] = useState(true);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('EMPLOYEE');
    const [error, setError] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        try {
            if (isLogin) {
                const res = await authService.login({ email, password });
                login(res.token, { email: res.email, role: res.role });
            } else {
                const res = await authService.register({ email, password, role });
                login(res.token, { email: res.email, role: res.role });
            }
        } catch (err: any) {
            setError(err.message || 'Authentication failed. Please check your credentials.');
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <h1 className="auth-title">{isLogin ? 'Welcome Back' : 'Create Account'}</h1>
                {error && <div className="auth-error">{error}</div>}
                
                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="input-group">
                        <label>Email</label>
                        <input 
                            type="email" 
                            value={email} 
                            onChange={e => setEmail(e.target.value)} 
                            required 
                            placeholder="you@example.com"
                        />
                    </div>
                    
                    <div className="input-group">
                        <label>Password</label>
                        <input 
                            type="password" 
                            value={password} 
                            onChange={e => setPassword(e.target.value)} 
                            required 
                            placeholder="••••••••"
                        />
                    </div>

                    {!isLogin && (
                        <div className="input-group">
                            <label>Role</label>
                            <select value={role} onChange={e => setRole(e.target.value)}>
                                <option value="EMPLOYEE">Employee</option>
                                <option value="SECRETARY">Secretary</option>
                                <option value="MANAGER">Manager</option>
                            </select>
                        </div>
                    )}

                    <button type="submit" className="auth-button">
                        {isLogin ? 'Sign In' : 'Sign Up'}
                    </button>
                </form>

                <p className="auth-toggle">
                    {isLogin ? "Don't have an account? " : "Already have an account? "}
                    <span onClick={() => setIsLogin(!isLogin)}>
                        {isLogin ? 'Register here' : 'Login here'}
                    </span>
                </p>
            </div>
        </div>
    );
};
