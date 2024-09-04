/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { saveExpanse } from '../fn/expanse/save-expanse';
import { SaveExpanse$Params } from '../fn/expanse/save-expanse';

@Injectable({ providedIn: 'root' })
export class ExpanseService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `saveExpanse()` */
  static readonly SaveExpansePath = '/expanse/create';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `saveExpanse()` instead.
   *
   * This method doesn't expect any request body.
   */
  saveExpanse$Response(params: SaveExpanse$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return saveExpanse(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `saveExpanse$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  saveExpanse(params: SaveExpanse$Params, context?: HttpContext): Observable<void> {
    return this.saveExpanse$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

}
