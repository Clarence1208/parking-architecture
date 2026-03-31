FROM node:24-alpine

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm install

EXPOSE 80

CMD ["npm", "run", "dev", "--", "--host", "--port", "80"]
