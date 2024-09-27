/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { NoteResponse } from '../../models/note-response';

export interface GetNoteById$Params {
  tripId: number;
}

export function getNoteById(http: HttpClient, rootUrl: string, params: GetNoteById$Params, context?: HttpContext): Observable<StrictHttpResponse<NoteResponse>> {
  const rb = new RequestBuilder(rootUrl, getNoteById.PATH, 'get');
  if (params) {
    rb.path('tripId', params.tripId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<NoteResponse>;
    })
  );
}

getNoteById.PATH = '/note/{tripId}';
