/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { AddressDetail } from '../../models/address-detail';

export interface FetchAddressDetailsByTripId$Params {
  tripId: number;
}

export function fetchAddressDetailsByTripId(http: HttpClient, rootUrl: string, params: FetchAddressDetailsByTripId$Params, context?: HttpContext): Observable<StrictHttpResponse<AddressDetail>> {
  const rb = new RequestBuilder(rootUrl, fetchAddressDetailsByTripId.PATH, 'get');
  if (params) {
    rb.path('tripId', params.tripId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<AddressDetail>;
    })
  );
}

fetchAddressDetailsByTripId.PATH = '/activity/get/{tripId}/trip/details';
