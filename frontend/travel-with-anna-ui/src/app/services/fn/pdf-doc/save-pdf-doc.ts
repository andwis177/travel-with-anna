/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { Pdf } from '../../models/pdf';

export interface SavePdfDoc$Params {
  pdfDoc: Pdf;
}

export function savePdfDoc(http: HttpClient, rootUrl: string, params: SavePdfDoc$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
  const rb = new RequestBuilder(rootUrl, savePdfDoc.PATH, 'post');
  if (params) {
    rb.query('pdfDoc', params.pdfDoc, {});
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

savePdfDoc.PATH = '/pdf_doc/create';
