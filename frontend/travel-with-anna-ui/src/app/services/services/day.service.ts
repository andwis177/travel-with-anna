/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { saveDay } from '../fn/day/save-day';
import { SaveDay$Params } from '../fn/day/save-day';

@Injectable({ providedIn: 'root' })
export class DayService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `saveDay()` */
  static readonly SaveDayPath = '/day/create';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `saveDay()` instead.
   *
   * This method doesn't expect any request body.
   */
  saveDay$Response(params: SaveDay$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return saveDay(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `saveDay$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  saveDay(params: SaveDay$Params, context?: HttpContext): Observable<void> {
    return this.saveDay$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

}
