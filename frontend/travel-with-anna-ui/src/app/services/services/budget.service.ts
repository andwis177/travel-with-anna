/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { BudgetExpensesRespond } from '../models/budget-expenses-respond';
import { BudgetResponse } from '../models/budget-response';
import { ExpanseTotalByBadge } from '../models/expanse-total-by-badge';
import { getBudgetById } from '../fn/budget/get-budget-by-id';
import { GetBudgetById$Params } from '../fn/budget/get-budget-by-id';
import { getBudgetExpanses } from '../fn/budget/get-budget-expanses';
import { GetBudgetExpanses$Params } from '../fn/budget/get-budget-expanses';
import { getExpansesByBadgeByTripId } from '../fn/budget/get-expanses-by-badge-by-trip-id';
import { GetExpansesByBadgeByTripId$Params } from '../fn/budget/get-expanses-by-badge-by-trip-id';
import { saveBudget } from '../fn/budget/save-budget';
import { SaveBudget$Params } from '../fn/budget/save-budget';
import { updateBudget } from '../fn/budget/update-budget';
import { UpdateBudget$Params } from '../fn/budget/update-budget';

@Injectable({ providedIn: 'root' })
export class BudgetService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `saveBudget()` */
  static readonly SaveBudgetPath = '/budget';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `saveBudget()` instead.
   *
   * This method doesn't expect any request body.
   */
  saveBudget$Response(params: SaveBudget$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return saveBudget(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `saveBudget$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  saveBudget(params: SaveBudget$Params, context?: HttpContext): Observable<void> {
    return this.saveBudget$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `updateBudget()` */
  static readonly UpdateBudgetPath = '/budget/update';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `updateBudget()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateBudget$Response(params: UpdateBudget$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return updateBudget(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `updateBudget$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateBudget(params: UpdateBudget$Params, context?: HttpContext): Observable<void> {
    return this.updateBudget$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `getBudgetById()` */
  static readonly GetBudgetByIdPath = '/budget/{budgetId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getBudgetById()` instead.
   *
   * This method doesn't expect any request body.
   */
  getBudgetById$Response(params: GetBudgetById$Params, context?: HttpContext): Observable<StrictHttpResponse<BudgetResponse>> {
    return getBudgetById(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getBudgetById$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getBudgetById(params: GetBudgetById$Params, context?: HttpContext): Observable<BudgetResponse> {
    return this.getBudgetById$Response(params, context).pipe(
      map((r: StrictHttpResponse<BudgetResponse>): BudgetResponse => r.body)
    );
  }

  /** Path part for operation `getBudgetExpanses()` */
  static readonly GetBudgetExpansesPath = '/budget/{budgetId}/expanses/{tripId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getBudgetExpanses()` instead.
   *
   * This method doesn't expect any request body.
   */
  getBudgetExpanses$Response(params: GetBudgetExpanses$Params, context?: HttpContext): Observable<StrictHttpResponse<BudgetExpensesRespond>> {
    return getBudgetExpanses(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getBudgetExpanses$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getBudgetExpanses(params: GetBudgetExpanses$Params, context?: HttpContext): Observable<BudgetExpensesRespond> {
    return this.getBudgetExpanses$Response(params, context).pipe(
      map((r: StrictHttpResponse<BudgetExpensesRespond>): BudgetExpensesRespond => r.body)
    );
  }

  /** Path part for operation `getExpansesByBadgeByTripId()` */
  static readonly GetExpansesByBadgeByTripIdPath = '/budget/calculate/{tripId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getExpansesByBadgeByTripId()` instead.
   *
   * This method doesn't expect any request body.
   */
  getExpansesByBadgeByTripId$Response(params: GetExpansesByBadgeByTripId$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<ExpanseTotalByBadge>>> {
    return getExpansesByBadgeByTripId(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getExpansesByBadgeByTripId$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getExpansesByBadgeByTripId(params: GetExpansesByBadgeByTripId$Params, context?: HttpContext): Observable<Array<ExpanseTotalByBadge>> {
    return this.getExpansesByBadgeByTripId$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<ExpanseTotalByBadge>>): Array<ExpanseTotalByBadge> => r.body)
    );
  }

}
