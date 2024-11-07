/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { createTrip } from '../fn/trip/create-trip';
import { CreateTrip$Params } from '../fn/trip/create-trip';
import { deleteTrip } from '../fn/trip/delete-trip';
import { DeleteTrip$Params } from '../fn/trip/delete-trip';
import { getAllOwnersTrips } from '../fn/trip/get-all-owners-trips';
import { GetAllOwnersTrips$Params } from '../fn/trip/get-all-owners-trips';
import { getTripById } from '../fn/trip/get-trip-by-id';
import { GetTripById$Params } from '../fn/trip/get-trip-by-id';
import { PageResponseTripResponse } from '../models/page-response-trip-response';
import { TripResponse } from '../models/trip-response';
import { updateTrip } from '../fn/trip/update-trip';
import { UpdateTrip$Params } from '../fn/trip/update-trip';

@Injectable({ providedIn: 'root' })
export class TripService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `getAllOwnersTrips()` */
  static readonly GetAllOwnersTripsPath = '/trip';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getAllOwnersTrips()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllOwnersTrips$Response(params?: GetAllOwnersTrips$Params, context?: HttpContext): Observable<StrictHttpResponse<PageResponseTripResponse>> {
    return getAllOwnersTrips(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getAllOwnersTrips$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllOwnersTrips(params?: GetAllOwnersTrips$Params, context?: HttpContext): Observable<PageResponseTripResponse> {
    return this.getAllOwnersTrips$Response(params, context).pipe(
      map((r: StrictHttpResponse<PageResponseTripResponse>): PageResponseTripResponse => r.body)
    );
  }

  /** Path part for operation `createTrip()` */
  static readonly CreateTripPath = '/trip';

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

  /** Path part for operation `deleteTrip()` */
  static readonly DeleteTripPath = '/trip';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `deleteTrip()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  deleteTrip$Response(params: DeleteTrip$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return deleteTrip(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `deleteTrip$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  deleteTrip(params: DeleteTrip$Params, context?: HttpContext): Observable<void> {
    return this.deleteTrip$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `updateTrip()` */
  static readonly UpdateTripPath = '/trip';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `updateTrip()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateTrip$Response(params: UpdateTrip$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return updateTrip(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `updateTrip$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateTrip(params: UpdateTrip$Params, context?: HttpContext): Observable<void> {
    return this.updateTrip$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
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
  getTripById$Response(params: GetTripById$Params, context?: HttpContext): Observable<StrictHttpResponse<TripResponse>> {
    return getTripById(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getTripById$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getTripById(params: GetTripById$Params, context?: HttpContext): Observable<TripResponse> {
    return this.getTripById$Response(params, context).pipe(
      map((r: StrictHttpResponse<TripResponse>): TripResponse => r.body)
    );
  }

}
