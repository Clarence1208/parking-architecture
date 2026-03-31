FROM node:24.14.1-alpine AS builder
 
WORKDIR /app

COPY package.json package-lock.json ./

RUN npm install
 
COPY . .

RUN npm run build


# Web server for production
FROM nginx:1.29.7 as prod

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
