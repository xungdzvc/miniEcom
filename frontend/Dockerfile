# STEP 1: Build Angular App
FROM node:18 AS build
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# STEP 2: Serve Angular using NGINX
FROM nginx:alpine

# COPY đúng thư mục Angular 17 build output!
COPY --from=build /app/dist/frontend-app/browser /usr/share/nginx/html

# SPA Routing Support
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
