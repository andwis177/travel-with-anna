/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { addDay } from '../fn/day/add-day';
import { AddDay$Params } from '../fn/day/add-day';
import { changeDayDate } from '../fn/day/change-day-date';
import { ChangeDayDate$Params } from '../fn/day/change-day-date';
import { DayResponse } from '../models/day-response';
import { deleteDay } from '../fn/day/delete-day';
import { DeleteDay$Params } from '../fn/day/delete-day';
import { generateDays } from '../fn/day/generate-days';
import { GenerateDays$Params } from '../fn/day/generate-days';
import { getDayById } from '../fn/day/get-day-by-id';
import { GetDayById$Params } from '../fn/day/get-day-by-id';
import { getDays } from '../fn/day/get-days';
import { GetDays$Params } from '../fn/day/get-days';

@Injectable({ providedIn: 'root' })
export class DayService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `generateDays()` */
  static readonly GenerateDaysPath = '/day/generate';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `generateDays()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  generateDays$Response(params: GenerateDays$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return generateDays(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `generateDays$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  generateDays(params: GenerateDays$Params, context?: HttpContext): Observable<void> {
    return this.generateDays$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `addDay()` */
  static readonly AddDayPath = '/day/add';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `addDay()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  addDay$Response(params: AddDay$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return addDay(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `addDay$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  addDay(params: AddDay$Params, context?: HttpContext): Observable<void> {
    return this.addDay$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `changeDayDate()` */
  static readonly ChangeDayDatePath = '/day/change/date';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `changeDayDate()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  changeDayDate$Response(params: ChangeDayDate$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return changeDayDate(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `changeDayDate$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  changeDayDate(params: ChangeDayDate$Params, context?: HttpContext): Observable<void> {
    return this.changeDayDate$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `getDays()` */
  static readonly GetDaysPath = '/day/{tripId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getDays()` instead.
   *
   * This method doesn't expect any request body.
   */
  getDays$Response(params: GetDays$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<DayResponse>>> {
    return getDays(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getDays$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getDays(params: GetDays$Params, context?: HttpContext): Observable<Array<DayResponse>> {
    return this.getDays$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<DayResponse>>): Array<DayResponse> => r.body)
    );
  }

  /** Path part for operation `getDayById()` */
  static readonly GetDayByIdPath = '/day/{dayId}/day';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getDayById()` instead.
   *
   * This method doesn't expect any request body.
   */
  getDayById$Response(params: GetDayById$Params, context?: HttpContext): Observable<StrictHttpResponse<DayResponse>> {
    return getDayById(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getDayById$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getDayById(params: GetDayById$Params, context?: HttpContext): Observable<DayResponse> {
    return this.getDayById$Response(params, context).pipe(
      map((r: StrictHttpResponse<DayResponse>): DayResponse => r.body)
    );
  }

  /** Path part for operation `deleteDay()` */
  static readonly DeleteDayPath = '/day';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `deleteDay()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  deleteDay$Response(params: DeleteDay$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return deleteDay(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `deleteDay$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  deleteDay(params: DeleteDay$Params, context?: HttpContext): Observable<void> {
    return this.deleteDay$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

}
