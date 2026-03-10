import { ApplicationConfig } from '@angular/core';
import { provideRouter, withInMemoryScrolling } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    // Phải gộp withInMemoryScrolling vào trong provideRouter để đúng Injection Context
    provideRouter(
      routes,
      withInMemoryScrolling({ 
        scrollPositionRestoration: 'enabled',
        anchorScrolling: 'enabled' 
      })
    ),
    provideHttpClient(
      withInterceptors([AuthInterceptor])
    )
  ]
};