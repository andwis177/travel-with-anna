/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { generateExpansePdfReport } from '../fn/pdf-report/generate-expanse-pdf-report';
import { GenerateExpansePdfReport$Params } from '../fn/pdf-report/generate-expanse-pdf-report';
import { generateTripPdfReport } from '../fn/pdf-report/generate-trip-pdf-report';
import { GenerateTripPdfReport$Params } from '../fn/pdf-report/generate-trip-pdf-report';

@Injectable({ providedIn: 'root' })
export class PdfReportService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `generateTripPdfReport()` */
  static readonly GenerateTripPdfReportPath = '/pdf/reports/trip/{tripId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `generateTripPdfReport()` instead.
   *
   * This method doesn't expect any request body.
   */
  generateTripPdfReport$Response(params: GenerateTripPdfReport$Params, context?: HttpContext): Observable<StrictHttpResponse<string>> {
    return generateTripPdfReport(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `generateTripPdfReport$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  generateTripPdfReport(params: GenerateTripPdfReport$Params, context?: HttpContext): Observable<string> {
    return this.generateTripPdfReport$Response(params, context).pipe(
      map((r: StrictHttpResponse<string>): string => r.body)
    );
  }

  /** Path part for operation `generateExpansePdfReport()` */
  static readonly GenerateExpansePdfReportPath = '/pdf/reports/expanse/{tripId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `generateExpansePdfReport()` instead.
   *
   * This method doesn't expect any request body.
   */
  generateExpansePdfReport$Response(params: GenerateExpansePdfReport$Params, context?: HttpContext): Observable<StrictHttpResponse<string>> {
    return generateExpansePdfReport(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `generateExpansePdfReport$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  generateExpansePdfReport(params: GenerateExpansePdfReport$Params, context?: HttpContext): Observable<string> {
    return this.generateExpansePdfReport$Response(params, context).pipe(
      map((r: StrictHttpResponse<string>): string => r.body)
    );
  }

}
