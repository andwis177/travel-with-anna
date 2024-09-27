/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { createNewNoteForTrip } from '../fn/note/create-new-note-for-trip';
import { CreateNewNoteForTrip$Params } from '../fn/note/create-new-note-for-trip';
import { getNoteById } from '../fn/note/get-note-by-id';
import { GetNoteById$Params } from '../fn/note/get-note-by-id';
import { NoteResponse } from '../models/note-response';

@Injectable({ providedIn: 'root' })
export class NoteService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `createNewNoteForTrip()` */
  static readonly CreateNewNoteForTripPath = '/note/save';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `createNewNoteForTrip()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createNewNoteForTrip$Response(params: CreateNewNoteForTrip$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return createNewNoteForTrip(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `createNewNoteForTrip$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createNewNoteForTrip(params: CreateNewNoteForTrip$Params, context?: HttpContext): Observable<void> {
    return this.createNewNoteForTrip$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `getNoteById()` */
  static readonly GetNoteByIdPath = '/note/{tripId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getNoteById()` instead.
   *
   * This method doesn't expect any request body.
   */
  getNoteById$Response(params: GetNoteById$Params, context?: HttpContext): Observable<StrictHttpResponse<NoteResponse>> {
    return getNoteById(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getNoteById$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getNoteById(params: GetNoteById$Params, context?: HttpContext): Observable<NoteResponse> {
    return this.getNoteById$Response(params, context).pipe(
      map((r: StrictHttpResponse<NoteResponse>): NoteResponse => r.body)
    );
  }

}
