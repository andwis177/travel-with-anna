/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { NoteResponse } from '../../models/note-response';

export interface GetNote$Params {
  entityId: number;
  entityType: string;
}

export function getNote(http: HttpClient, rootUrl: string, params: GetNote$Params, context?: HttpContext): Observable<StrictHttpResponse<NoteResponse>> {
  const rb = new RequestBuilder(rootUrl, getNote.PATH, 'get');
  if (params) {
    rb.query('entityId', params.entityId, {});
    rb.query('entityType', params.entityType, {});
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

getNote.PATH = '/note';
