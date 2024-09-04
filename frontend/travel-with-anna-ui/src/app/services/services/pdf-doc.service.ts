/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { savePdfDoc } from '../fn/pdf-doc/save-pdf-doc';
import { SavePdfDoc$Params } from '../fn/pdf-doc/save-pdf-doc';

@Injectable({ providedIn: 'root' })
export class PdfDocService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `savePdfDoc()` */
  static readonly SavePdfDocPath = '/pdf_doc/create';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `savePdfDoc()` instead.
   *
   * This method doesn't expect any request body.
   */
  savePdfDoc$Response(params: SavePdfDoc$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return savePdfDoc(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `savePdfDoc$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  savePdfDoc(params: SavePdfDoc$Params, context?: HttpContext): Observable<void> {
    return this.savePdfDoc$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

}
