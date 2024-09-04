/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { saveNote } from '../fn/note/save-note';
import { SaveNote$Params } from '../fn/note/save-note';

@Injectable({ providedIn: 'root' })
export class NoteService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `saveNote()` */
  static readonly SaveNotePath = '/note/create';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `saveNote()` instead.
   *
   * This method doesn't expect any request body.
   */
  saveNote$Response(params: SaveNote$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return saveNote(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `saveNote$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  saveNote(params: SaveNote$Params, context?: HttpContext): Observable<void> {
    return this.saveNote$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

}
