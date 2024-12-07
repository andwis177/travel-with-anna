/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';


export interface GenerateExpansePdfReport$Params {
  tripId: number;
}

export function generateExpansePdfReport(http: HttpClient, rootUrl: string, params: GenerateExpansePdfReport$Params, context?: HttpContext): Observable<StrictHttpResponse<string>> {
  const rb = new RequestBuilder(rootUrl, generateExpansePdfReport.PATH, 'get');
  if (params) {
    rb.path('tripId', params.tripId, {});
  }

  return http.request(
    rb.build({ responseType: 'blob', accept: 'application/pdf', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<string>;
    })
  );
}

generateExpansePdfReport.PATH = '/pdf/reports/expanse/{tripId}';