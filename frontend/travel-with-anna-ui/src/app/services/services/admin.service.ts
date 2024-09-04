/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { deleteUser } from '../fn/admin/delete-user';
import { DeleteUser$Params } from '../fn/admin/delete-user';
import { getAllRoleNamesWithAdmin } from '../fn/admin/get-all-role-names-with-admin';
import { GetAllRoleNamesWithAdmin$Params } from '../fn/admin/get-all-role-names-with-admin';
import { getAllUsers } from '../fn/admin/get-all-users';
import { GetAllUsers$Params } from '../fn/admin/get-all-users';
import { getAvatar } from '../fn/admin/get-avatar';
import { GetAvatar$Params } from '../fn/admin/get-avatar';
import { getUserAdminViewByIdentifier } from '../fn/admin/get-user-admin-view-by-identifier';
import { GetUserAdminViewByIdentifier$Params } from '../fn/admin/get-user-admin-view-by-identifier';
import { PageResponseUserAdminView } from '../models/page-response-user-admin-view';
import { updateUser } from '../fn/admin/update-user';
import { UpdateUser$Params } from '../fn/admin/update-user';
import { UserAdminView } from '../models/user-admin-view';
import { UserAvatar } from '../models/user-avatar';
import { UserRespond } from '../models/user-respond';

@Injectable({ providedIn: 'root' })
export class AdminService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `updateUser()` */
  static readonly UpdateUserPath = '/admin/update';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `updateUser()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateUser$Response(params: UpdateUser$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return updateUser(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `updateUser$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateUser(params: UpdateUser$Params, context?: HttpContext): Observable<void> {
    return this.updateUser$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `getAllUsers()` */
  static readonly GetAllUsersPath = '/admin/users';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getAllUsers()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllUsers$Response(params?: GetAllUsers$Params, context?: HttpContext): Observable<StrictHttpResponse<PageResponseUserAdminView>> {
    return getAllUsers(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getAllUsers$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllUsers(params?: GetAllUsers$Params, context?: HttpContext): Observable<PageResponseUserAdminView> {
    return this.getAllUsers$Response(params, context).pipe(
      map((r: StrictHttpResponse<PageResponseUserAdminView>): PageResponseUserAdminView => r.body)
    );
  }

  /** Path part for operation `getUserAdminViewByIdentifier()` */
  static readonly GetUserAdminViewByIdentifierPath = '/admin/user/{identifier}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getUserAdminViewByIdentifier()` instead.
   *
   * This method doesn't expect any request body.
   */
  getUserAdminViewByIdentifier$Response(params: GetUserAdminViewByIdentifier$Params, context?: HttpContext): Observable<StrictHttpResponse<UserAdminView>> {
    return getUserAdminViewByIdentifier(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getUserAdminViewByIdentifier$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getUserAdminViewByIdentifier(params: GetUserAdminViewByIdentifier$Params, context?: HttpContext): Observable<UserAdminView> {
    return this.getUserAdminViewByIdentifier$Response(params, context).pipe(
      map((r: StrictHttpResponse<UserAdminView>): UserAdminView => r.body)
    );
  }

  /** Path part for operation `getAllRoleNamesWithAdmin()` */
  static readonly GetAllRoleNamesWithAdminPath = '/admin/roles';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getAllRoleNamesWithAdmin()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllRoleNamesWithAdmin$Response(params?: GetAllRoleNamesWithAdmin$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<string>>> {
    return getAllRoleNamesWithAdmin(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getAllRoleNamesWithAdmin$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllRoleNamesWithAdmin(params?: GetAllRoleNamesWithAdmin$Params, context?: HttpContext): Observable<Array<string>> {
    return this.getAllRoleNamesWithAdmin$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<string>>): Array<string> => r.body)
    );
  }

  /** Path part for operation `getAvatar()` */
  static readonly GetAvatarPath = '/admin/avatar/{userId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getAvatar()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAvatar$Response(params: GetAvatar$Params, context?: HttpContext): Observable<StrictHttpResponse<UserAvatar>> {
    return getAvatar(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getAvatar$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAvatar(params: GetAvatar$Params, context?: HttpContext): Observable<UserAvatar> {
    return this.getAvatar$Response(params, context).pipe(
      map((r: StrictHttpResponse<UserAvatar>): UserAvatar => r.body)
    );
  }

  /** Path part for operation `deleteUser()` */
  static readonly DeleteUserPath = '/admin/delete/{userId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `deleteUser()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  deleteUser$Response(params: DeleteUser$Params, context?: HttpContext): Observable<StrictHttpResponse<UserRespond>> {
    return deleteUser(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `deleteUser$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  deleteUser(params: DeleteUser$Params, context?: HttpContext): Observable<UserRespond> {
    return this.deleteUser$Response(params, context).pipe(
      map((r: StrictHttpResponse<UserRespond>): UserRespond => r.body)
    );
  }

}
