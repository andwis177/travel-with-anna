/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { getBadges } from '../fn/badge/get-badges';
import { GetBadges$Params } from '../fn/badge/get-badges';

@Injectable({ providedIn: 'root' })
export class BadgeService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `getBadges()` */
  static readonly GetBadgesPath = '/badge';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getBadges()` instead.
   *
   * This method doesn't expect any request body.
   */
  getBadges$Response(params?: GetBadges$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<string>>> {
    return getBadges(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getBadges$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getBadges(params?: GetBadges$Params, context?: HttpContext): Observable<Array<string>> {
    return this.getBadges$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<string>>): Array<string> => r.body)
    );
  }

}
