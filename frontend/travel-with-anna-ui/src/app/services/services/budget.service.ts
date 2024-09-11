/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { BudgetRequest } from '../models/budget-request';
import { getBudgetById } from '../fn/budget/get-budget-by-id';
import { GetBudgetById$Params } from '../fn/budget/get-budget-by-id';
import { saveBudget } from '../fn/budget/save-budget';
import { SaveBudget$Params } from '../fn/budget/save-budget';

@Injectable({ providedIn: 'root' })
export class BudgetService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `saveBudget()` */
  static readonly SaveBudgetPath = '/budget/create';

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

  /** Path part for operation `getBudgetById()` */
  static readonly GetBudgetByIdPath = '/budget/{budgetId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getBudgetById()` instead.
   *
   * This method doesn't expect any request body.
   */
  getBudgetById$Response(params: GetBudgetById$Params, context?: HttpContext): Observable<StrictHttpResponse<BudgetRequest>> {
    return getBudgetById(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getBudgetById$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getBudgetById(params: GetBudgetById$Params, context?: HttpContext): Observable<BudgetRequest> {
    return this.getBudgetById$Response(params, context).pipe(
      map((r: StrictHttpResponse<BudgetRequest>): BudgetRequest => r.body)
    );
  }

}
