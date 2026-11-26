import { Configuration } from '@azure/msal-browser';

export const msalConfig: Configuration = {
  auth: {
    clientId: '4d91fc12-0438-4f42-bb18-21a77f3423ca',
    authority: 'https://login.microsoftonline.com/83ff2569-d772-43f7-97e2-9c69f67484ad',
    redirectUri: 'http://localhost:4200/',
    postLogoutRedirectUri: 'http://localhost:4200/login',
  },
  cache: {
    cacheLocation: 'localStorage',
  },
  system: {
    loggerOptions: {
      loggerCallback: (level, message) => {
        console.log(`[MSAL] ${level}: ${message}`);
      },
      logLevel: 2,
    },
  },
};

export const protectedResources = {
  bffApi: {
    endpoint: 'http://localhost:8080/api',
    scopes: ['api://4d91fc12-0438-4f42-bb18-21a77f3423ca/access_as_user'],
  },
};

export const loginRequest = {
  scopes: ['openid', 'profile'],
};
