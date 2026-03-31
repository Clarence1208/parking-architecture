# Frontend - Parking architecture

src/
├── shared/               
│   ├── components/         
│   ├── ui/                 
│   └── api/                
│
├── features/               
│   ├── booking/           
│   ├── dashboard/          
│   └── auth/               
│
├── services/               
│   ├── bookingService.ts   
│   ├── authService.ts      
│   └── userService.ts      
│
├── store/                  
│   ├── useAuthStore.ts     
│   ├── useBookingStore.ts  
│   └── useUIStore.ts       
│
├── types/                  
│   ├── api-models.ts       
│   └── index.ts            
│
├── App.tsx                
└── main.tsx
tests/