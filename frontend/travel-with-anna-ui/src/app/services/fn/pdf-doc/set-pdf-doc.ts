/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { PdfDocRequest } from '../../models/pdf-doc-request';

export interface SetPdfDoc$Params {
  request: PdfDocRequest;
}

export function setPdfDoc(http: HttpClient, rootUrl: string, params: SetPdfDoc$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
  const rb = new RequestBuilder(rootUrl, setPdfDoc.PATH, 'post');
  if (params) {
    rb.query('request', params.request, {});
  }

  return http.request(
    rb.build({ responseType: 'text', accept: '*/*', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return (r as HttpResponse<any>).clone({ body: undefined }) as StrictHttpResponse<void>;
    })
  );
}

setPdfDoc.PATH = '/pdf_doc/set';
