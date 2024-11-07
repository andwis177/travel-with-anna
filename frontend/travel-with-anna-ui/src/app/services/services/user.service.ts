/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { AuthenticationResponse } from '../models/authentication-response';
import { changePassword } from '../fn/user/change-password';
import { ChangePassword$Params } from '../fn/user/change-password';
import { delete$ } from '../fn/user/delete';
import { Delete$Params } from '../fn/user/delete';
import { getCredentials } from '../fn/user/get-credentials';
import { GetCredentials$Params } from '../fn/user/get-credentials';
import { update } from '../fn/user/update';
import { Update$Params } from '../fn/user/update';
import { UserCredentialsResponse } from '../models/user-credentials-response';
import { UserResponse } from '../models/user-response';

@Injectable({ providedIn: 'root' })
export class UserService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `delete()` */
  static readonly DeletePath = '/user';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `delete()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  delete$Response(params: Delete$Params, context?: HttpContext): Observable<StrictHttpResponse<UserResponse>> {
    return delete$(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `delete$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  delete(params: Delete$Params, context?: HttpContext): Observable<UserResponse> {
    return this.delete$Response(params, context).pipe(
      map((r: StrictHttpResponse<UserResponse>): UserResponse => r.body)
    );
  }

  /** Path part for operation `update()` */
  static readonly UpdatePath = '/user';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `update()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  update$Response(params: Update$Params, context?: HttpContext): Observable<StrictHttpResponse<AuthenticationResponse>> {
    return update(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `update$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  update(params: Update$Params, context?: HttpContext): Observable<AuthenticationResponse> {
    return this.update$Response(params, context).pipe(
      map((r: StrictHttpResponse<AuthenticationResponse>): AuthenticationResponse => r.body)
    );
  }

  /** Path part for operation `changePassword()` */
  static readonly ChangePasswordPath = '/user/change-password';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `changePassword()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  changePassword$Response(params: ChangePassword$Params, context?: HttpContext): Observable<StrictHttpResponse<UserResponse>> {
    return changePassword(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `changePassword$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  changePassword(params: ChangePassword$Params, context?: HttpContext): Observable<UserResponse> {
    return this.changePassword$Response(params, context).pipe(
      map((r: StrictHttpResponse<UserResponse>): UserResponse => r.body)
    );
  }

  /** Path part for operation `getCredentials()` */
  static readonly GetCredentialsPath = '/user/credentials';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getCredentials()` instead.
   *
   * This method doesn't expect any request body.
   */
  getCredentials$Response(params?: GetCredentials$Params, context?: HttpContext): Observable<StrictHttpResponse<UserCredentialsResponse>> {
    return getCredentials(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getCredentials$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getCredentials(params?: GetCredentials$Params, context?: HttpContext): Observable<UserCredentialsResponse> {
    return this.getCredentials$Response(params, context).pipe(
      map((r: StrictHttpResponse<UserCredentialsResponse>): UserCredentialsResponse => r.body)
    );
  }

}
