/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { getNoteByActivityId } from '../fn/note/get-note-by-activity-id';
import { GetNoteByActivityId$Params } from '../fn/note/get-note-by-activity-id';
import { getNoteByTripId } from '../fn/note/get-note-by-trip-id';
import { GetNoteByTripId$Params } from '../fn/note/get-note-by-trip-id';
import { NoteResponse } from '../models/note-response';
import { saveNoteForActivity } from '../fn/note/save-note-for-activity';
import { SaveNoteForActivity$Params } from '../fn/note/save-note-for-activity';
import { saveNoteForTrip } from '../fn/note/save-note-for-trip';
import { SaveNoteForTrip$Params } from '../fn/note/save-note-for-trip';

@Injectable({ providedIn: 'root' })
export class NoteService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `saveNoteForTrip()` */
  static readonly SaveNoteForTripPath = '/note/trip/save';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `saveNoteForTrip()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  saveNoteForTrip$Response(params: SaveNoteForTrip$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return saveNoteForTrip(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `saveNoteForTrip$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  saveNoteForTrip(params: SaveNoteForTrip$Params, context?: HttpContext): Observable<void> {
    return this.saveNoteForTrip$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `saveNoteForActivity()` */
  static readonly SaveNoteForActivityPath = '/note/activity/save';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `saveNoteForActivity()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  saveNoteForActivity$Response(params: SaveNoteForActivity$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return saveNoteForActivity(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `saveNoteForActivity$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  saveNoteForActivity(params: SaveNoteForActivity$Params, context?: HttpContext): Observable<void> {
    return this.saveNoteForActivity$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `getNoteByTripId()` */
  static readonly GetNoteByTripIdPath = '/note/trip/{tripId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getNoteByTripId()` instead.
   *
   * This method doesn't expect any request body.
   */
  getNoteByTripId$Response(params: GetNoteByTripId$Params, context?: HttpContext): Observable<StrictHttpResponse<NoteResponse>> {
    return getNoteByTripId(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getNoteByTripId$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getNoteByTripId(params: GetNoteByTripId$Params, context?: HttpContext): Observable<NoteResponse> {
    return this.getNoteByTripId$Response(params, context).pipe(
      map((r: StrictHttpResponse<NoteResponse>): NoteResponse => r.body)
    );
  }

  /** Path part for operation `getNoteByActivityId()` */
  static readonly GetNoteByActivityIdPath = '/note/activity/{activityId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getNoteByActivityId()` instead.
   *
   * This method doesn't expect any request body.
   */
  getNoteByActivityId$Response(params: GetNoteByActivityId$Params, context?: HttpContext): Observable<StrictHttpResponse<NoteResponse>> {
    return getNoteByActivityId(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getNoteByActivityId$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getNoteByActivityId(params: GetNoteByActivityId$Params, context?: HttpContext): Observable<NoteResponse> {
    return this.getNoteByActivityId$Response(params, context).pipe(
      map((r: StrictHttpResponse<NoteResponse>): NoteResponse => r.body)
    );
  }

}
