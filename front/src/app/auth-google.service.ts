import { Injectable } from '@angular/core';
import { AuthConfig, OAuthService } from 'angular-oauth2-oidc'

@Injectable({
  providedIn: 'root'
})
export class AuthGoogleService {

  constructor(private oauthService: OAuthService) {
    this.initLogin();
  }

  initLogin() {
    const config: AuthConfig = {
      issuer: 'https://accounts.google.com',
      strictDiscoveryDocumentValidation: false,
      clientId: '134741093553-s9sq950k6q9jbfl3k6q49qrfsfd1a9aq.apps.googleusercontent.com',
      redirectUri: 'http://localhost:4200/home2',
      scope: 'openid profile email',
    }
    this.oauthService.configure(config);
    this.oauthService.setupAutomaticSilentRefresh();
    this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }

  login() {
    this.oauthService.initLoginFlow();
    localStorage.setItem('jwtToken', "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjcmlzbm9lYXhAZ21haWwuY29tIiwiaWF0IjoxNzMxNTA4MjE1LCJleHAiOjE3MzE1NDQyMTV9.9EaFP8B-koIQDrPBO35YwI_BauXDhA-XqnDIZSb6SyI");
  }

  logout() {
    this.oauthService.logOut();
  }

  getProfile() {
    return this.oauthService.getIdentityClaims();
  }

}
