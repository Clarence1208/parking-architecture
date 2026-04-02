import React from 'react';
import './StatsCard.css';

interface StatsCardProps {
    title: string;
    value: string | number;
    icon: React.ReactNode;
    trend?: {
        value: number;
        isPositive: boolean;
    };
    color?: string; // Pour varier l'accent (ex: #6366f1, #10b981)
}

export default function StatsCard({ title, value, icon, trend, color = '#6366f1' }: StatsCardProps) {
    return (
        <div className="stats-card">
            <div className="stats-card__header">
                <div className="stats-card__icon" style={{ backgroundColor: `${color}20`, color: color }}>
                    {icon}
                </div>
                {trend && (
                    <div className={`stats-card__trend ${trend.isPositive ? 'positive' : 'negative'}`}>
                        {trend.isPositive ? '↑' : '↓'} {Math.abs(trend.value)}%
                    </div>
                )}
            </div>

            <div className="stats-card__body">
                <span className="stats-card__value">{value}</span>
                <h3 className="stats-card__title">{title}</h3>
            </div>

            {/* Petite ligne de décoration en bas avec la couleur d'accent */}
            <div className="stats-card__footer-bar" style={{ backgroundColor: color }}></div>
        </div>
    );
}