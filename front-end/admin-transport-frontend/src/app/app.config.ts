import { ApplicationConfig, APP_INITIALIZER } from '@angular/core';
import { provideRouter } from '@angular/router';
import {
  provideHttpClient,
  withInterceptorsFromDi,
  HTTP_INTERCEPTORS
} from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { routes } from './app.routes';

import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { importProvidersFrom } from '@angular/core';

import { AuthInterceptor } from './interceptors/auth.interceptor';

export function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8080',
        realm: 'urbana',
        clientId: 'frontend-client',
      },
      initOptions: {
        onLoad: 'check-sso',
        checkLoginIframe: false,
      },
    });
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),

    provideHttpClient(
      withInterceptorsFromDi()
    ),

    provideAnimations(),

    importProvidersFrom(KeycloakAngularModule),

    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService],
    },

    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
  ],
};
