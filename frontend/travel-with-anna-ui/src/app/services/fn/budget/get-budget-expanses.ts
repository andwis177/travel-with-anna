/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { BudgetExpensesRespond } from '../../models/budget-expenses-respond';

export interface GetBudgetExpanses$Params {
  tripId: number;
  budgetId: number;
}

export function getBudgetExpanses(http: HttpClient, rootUrl: string, params: GetBudgetExpanses$Params, context?: HttpContext): Observable<StrictHttpResponse<BudgetExpensesRespond>> {
  const rb = new RequestBuilder(rootUrl, getBudgetExpanses.PATH, 'get');
  if (params) {
    rb.path('tripId', params.tripId, {});
    rb.path('budgetId', params.budgetId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<BudgetExpensesRespond>;
    })
  );
}

getBudgetExpanses.PATH = '/budget/{budgetId}/expanses/{tripId}';
