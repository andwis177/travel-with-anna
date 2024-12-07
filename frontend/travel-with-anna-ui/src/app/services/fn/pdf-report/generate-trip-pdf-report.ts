/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';


export interface GenerateTripPdfReport$Params {
  tripId: number;
}

export function generateTripPdfReport(http: HttpClient, rootUrl: string, params: GenerateTripPdfReport$Params, context?: HttpContext): Observable<StrictHttpResponse<string>> {
  const rb = new RequestBuilder(rootUrl, generateTripPdfReport.PATH, 'get');
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

generateTripPdfReport.PATH = '/pdf/reports/trip/{tripId}';
