/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { getNote } from '../fn/note/get-note';
import { GetNote$Params } from '../fn/note/get-note';
import { NoteResponse } from '../models/note-response';
import { saveNote } from '../fn/note/save-note';
import { SaveNote$Params } from '../fn/note/save-note';

@Injectable({ providedIn: 'root' })
export class NoteService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `getNote()` */
  static readonly GetNotePath = '/note';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getNote()` instead.
   *
   * This method doesn't expect any request body.
   */
  getNote$Response(params: GetNote$Params, context?: HttpContext): Observable<StrictHttpResponse<NoteResponse>> {
    return getNote(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getNote$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getNote(params: GetNote$Params, context?: HttpContext): Observable<NoteResponse> {
    return this.getNote$Response(params, context).pipe(
      map((r: StrictHttpResponse<NoteResponse>): NoteResponse => r.body)
    );
  }

  /** Path part for operation `saveNote()` */
  static readonly SaveNotePath = '/note';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `saveNote()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  saveNote$Response(params: SaveNote$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return saveNote(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `saveNote$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  saveNote(params: SaveNote$Params, context?: HttpContext): Observable<void> {
    return this.saveNote$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

}
