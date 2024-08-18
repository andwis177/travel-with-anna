/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { getAllRoles } from '../fn/role/get-all-roles';
import { GetAllRoles$Params } from '../fn/role/get-all-roles';

@Injectable({ providedIn: 'root' })
export class RoleService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `getAllRoles()` */
  static readonly GetAllRolesPath = '/role/all';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getAllRoles()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllRoles$Response(params?: GetAllRoles$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<string>>> {
    return getAllRoles(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getAllRoles$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllRoles(params?: GetAllRoles$Params, context?: HttpContext): Observable<Array<string>> {
    return this.getAllRoles$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<string>>): Array<string> => r.body)
    );
  }

}
