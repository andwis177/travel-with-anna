FROM node:20.15.0-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
RUN npx ngcc --properties es2023 browser module main --first-only --create-ivy-entry-points
COPY . .
RUN npm run build -- --output-path=dist/travel-with-anna-ui/browser --configuration=production
FROM nginx:stable
COPY --from=build /app/dist/travel-with-anna-ui/browser/ usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
