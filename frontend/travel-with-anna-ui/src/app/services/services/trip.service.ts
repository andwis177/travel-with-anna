/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { addViewer } from '../fn/trip/add-viewer';
import { AddViewer$Params } from '../fn/trip/add-viewer';
import { changeTripName } from '../fn/trip/change-trip-name';
import { ChangeTripName$Params } from '../fn/trip/change-trip-name';
import { createTrip } from '../fn/trip/create-trip';
import { CreateTrip$Params } from '../fn/trip/create-trip';
import { deleteTrip } from '../fn/trip/delete-trip';
import { DeleteTrip$Params } from '../fn/trip/delete-trip';
import { getAllOwnersTrips } from '../fn/trip/get-all-owners-trips';
import { GetAllOwnersTrips$Params } from '../fn/trip/get-all-owners-trips';
import { getTripById } from '../fn/trip/get-trip-by-id';
import { GetTripById$Params } from '../fn/trip/get-trip-by-id';
import { getTripViewers } from '../fn/trip/get-trip-viewers';
import { GetTripViewers$Params } from '../fn/trip/get-trip-viewers';
import { PageResponseTripDto } from '../models/page-response-trip-dto';
import { removeViewer } from '../fn/trip/remove-viewer';
import { RemoveViewer$Params } from '../fn/trip/remove-viewer';
import { TripDto } from '../models/trip-dto';
import { ViewerDto } from '../models/viewer-dto';

@Injectable({ providedIn: 'root' })
export class TripService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `createTrip()` */
  static readonly CreateTripPath = '/trip/create';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `createTrip()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createTrip$Response(params: CreateTrip$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    return createTrip(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `createTrip$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createTrip(params: CreateTrip$Params, context?: HttpContext): Observable<number> {
    return this.createTrip$Response(params, context).pipe(
      map((r: StrictHttpResponse<number>): number => r.body)
    );
  }

  /** Path part for operation `removeViewer()` */
  static readonly RemoveViewerPath = '/trip/{tripId}/viewer/{viewerId}/remove';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `removeViewer()` instead.
   *
   * This method doesn't expect any request body.
   */
  removeViewer$Response(params: RemoveViewer$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return removeViewer(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `removeViewer$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  removeViewer(params: RemoveViewer$Params, context?: HttpContext): Observable<void> {
    return this.removeViewer$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `addViewer()` */
  static readonly AddViewerPath = '/trip/{tripId}/viewer/{viewerId}/add';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `addViewer()` instead.
   *
   * This method doesn't expect any request body.
   */
  addViewer$Response(params: AddViewer$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return addViewer(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `addViewer$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  addViewer(params: AddViewer$Params, context?: HttpContext): Observable<void> {
    return this.addViewer$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `changeTripName()` */
  static readonly ChangeTripNamePath = '/trip/{tripId}/name/change';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `changeTripName()` instead.
   *
   * This method doesn't expect any request body.
   */
  changeTripName$Response(params: ChangeTripName$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    return changeTripName(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `changeTripName$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  changeTripName(params: ChangeTripName$Params, context?: HttpContext): Observable<number> {
    return this.changeTripName$Response(params, context).pipe(
      map((r: StrictHttpResponse<number>): number => r.body)
    );
  }

  /** Path part for operation `getAllOwnersTrips()` */
  static readonly GetAllOwnersTripsPath = '/trip';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getAllOwnersTrips()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllOwnersTrips$Response(params?: GetAllOwnersTrips$Params, context?: HttpContext): Observable<StrictHttpResponse<PageResponseTripDto>> {
    return getAllOwnersTrips(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getAllOwnersTrips$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllOwnersTrips(params?: GetAllOwnersTrips$Params, context?: HttpContext): Observable<PageResponseTripDto> {
    return this.getAllOwnersTrips$Response(params, context).pipe(
      map((r: StrictHttpResponse<PageResponseTripDto>): PageResponseTripDto => r.body)
    );
  }

  /** Path part for operation `getTripById()` */
  static readonly GetTripByIdPath = '/trip/{tripId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getTripById()` instead.
   *
   * This method doesn't expect any request body.
   */
  getTripById$Response(params: GetTripById$Params, context?: HttpContext): Observable<StrictHttpResponse<TripDto>> {
    return getTripById(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getTripById$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getTripById(params: GetTripById$Params, context?: HttpContext): Observable<TripDto> {
    return this.getTripById$Response(params, context).pipe(
      map((r: StrictHttpResponse<TripDto>): TripDto => r.body)
    );
  }

  /** Path part for operation `getTripViewers()` */
  static readonly GetTripViewersPath = '/trip/{tripId}/viewers';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getTripViewers()` instead.
   *
   * This method doesn't expect any request body.
   */
  getTripViewers$Response(params: GetTripViewers$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<ViewerDto>>> {
    return getTripViewers(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getTripViewers$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getTripViewers(params: GetTripViewers$Params, context?: HttpContext): Observable<Array<ViewerDto>> {
    return this.getTripViewers$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<ViewerDto>>): Array<ViewerDto> => r.body)
    );
  }

  /** Path part for operation `deleteTrip()` */
  static readonly DeleteTripPath = '/trip/{tripId}/delete';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `deleteTrip()` instead.
   *
   * This method doesn't expect any request body.
   */
  deleteTrip$Response(params: DeleteTrip$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return deleteTrip(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `deleteTrip$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  deleteTrip(params: DeleteTrip$Params, context?: HttpContext): Observable<void> {
    return this.deleteTrip$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

}
