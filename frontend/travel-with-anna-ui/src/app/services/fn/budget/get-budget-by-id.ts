/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { BudgetResponse } from '../../models/budget-response';

export interface GetBudgetById$Params {
  budgetId: number;
}

export function getBudgetById(http: HttpClient, rootUrl: string, params: GetBudgetById$Params, context?: HttpContext): Observable<StrictHttpResponse<BudgetResponse>> {
  const rb = new RequestBuilder(rootUrl, getBudgetById.PATH, 'get');
  if (params) {
    rb.path('budgetId', params.budgetId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<BudgetResponse>;
    })
  );
}

getBudgetById.PATH = '/budget/{budgetId}';
