import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  children: React.ReactNode;
}

export const Button = ({ children, className = '', ...props }: ButtonProps) => {
  return (
    <button
      className={`
        /* Dimensions et Texte */
        px-12 py-5 text-xl font-bold uppercase tracking-wider
        
        /* Couleurs et Dégradé (Style ParkOffice) */
        bg-gradient-to-r from-blue-600 to-indigo-600 text-white
        
        /* Forme et Bordure */
        rounded-full border-none
        
        /* Ombre et Transition (Le côté "stylé") */
        shadow-lg shadow-blue-500/30
        transition-all duration-300 ease-out
        
        /* Effets au Survol (Hover) */
        hover:from-blue-700 hover:to-indigo-700
        hover:shadow-xl hover:shadow-blue-500/50
        hover:-translate-y-1 /* Léger soulèvement */
        
        /* Effet au Clic (Active) */
        active:scale-95 active:shadow-md
        
        /* État Désactivé */
        disabled:opacity-60 disabled:pointer-events-none
        
        /* Pour ajouter des styles spécifiques si besoin */
        ${className}
      `}
      {...props}
    >
      {children}
    </button>
  );
};