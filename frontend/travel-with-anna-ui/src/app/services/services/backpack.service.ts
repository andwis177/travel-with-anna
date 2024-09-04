/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { saveBackpack } from '../fn/backpack/save-backpack';
import { SaveBackpack$Params } from '../fn/backpack/save-backpack';

@Injectable({ providedIn: 'root' })
export class BackpackService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `saveBackpack()` */
  static readonly SaveBackpackPath = '/backpack/create';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `saveBackpack()` instead.
   *
   * This method doesn't expect any request body.
   */
  saveBackpack$Response(params: SaveBackpack$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return saveBackpack(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `saveBackpack$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  saveBackpack(params: SaveBackpack$Params, context?: HttpContext): Observable<void> {
    return this.saveBackpack$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

}
