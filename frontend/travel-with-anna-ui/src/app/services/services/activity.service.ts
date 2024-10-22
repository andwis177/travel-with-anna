/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { ActivityDetailedResponse } from '../models/activity-detailed-response';
import { AddressDetail } from '../models/address-detail';
import { createActivity } from '../fn/activity/create-activity';
import { CreateActivity$Params } from '../fn/activity/create-activity';
import { createAssociatedActivities } from '../fn/activity/create-associated-activities';
import { CreateAssociatedActivities$Params } from '../fn/activity/create-associated-activities';
import { deleteActivityById } from '../fn/activity/delete-activity-by-id';
import { DeleteActivityById$Params } from '../fn/activity/delete-activity-by-id';
import { fetchActivitiesByDayId } from '../fn/activity/fetch-activities-by-day-id';
import { FetchActivitiesByDayId$Params } from '../fn/activity/fetch-activities-by-day-id';
import { fetchAddressDetailsByDayId } from '../fn/activity/fetch-address-details-by-day-id';
import { FetchAddressDetailsByDayId$Params } from '../fn/activity/fetch-address-details-by-day-id';
import { fetchAddressDetailsByTripId } from '../fn/activity/fetch-address-details-by-trip-id';
import { FetchAddressDetailsByTripId$Params } from '../fn/activity/fetch-address-details-by-trip-id';
import { updateActivity } from '../fn/activity/update-activity';
import { UpdateActivity$Params } from '../fn/activity/update-activity';

@Injectable({ providedIn: 'root' })
export class ActivityService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `createActivity()` */
  static readonly CreateActivityPath = '/activity/create';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `createActivity()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createActivity$Response(params: CreateActivity$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return createActivity(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `createActivity$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createActivity(params: CreateActivity$Params, context?: HttpContext): Observable<void> {
    return this.createActivity$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `createAssociatedActivities()` */
  static readonly CreateAssociatedActivitiesPath = '/activity/create/associated';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `createAssociatedActivities()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createAssociatedActivities$Response(params: CreateAssociatedActivities$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return createAssociatedActivities(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `createAssociatedActivities$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createAssociatedActivities(params: CreateAssociatedActivities$Params, context?: HttpContext): Observable<void> {
    return this.createAssociatedActivities$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `updateActivity()` */
  static readonly UpdateActivityPath = '/activity/update';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `updateActivity()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateActivity$Response(params: UpdateActivity$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return updateActivity(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `updateActivity$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  updateActivity(params: UpdateActivity$Params, context?: HttpContext): Observable<void> {
    return this.updateActivity$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `fetchAddressDetailsByTripId()` */
  static readonly FetchAddressDetailsByTripIdPath = '/activity/get/{tripId}/trip/details';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `fetchAddressDetailsByTripId()` instead.
   *
   * This method doesn't expect any request body.
   */
  fetchAddressDetailsByTripId$Response(params: FetchAddressDetailsByTripId$Params, context?: HttpContext): Observable<StrictHttpResponse<AddressDetail>> {
    return fetchAddressDetailsByTripId(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `fetchAddressDetailsByTripId$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  fetchAddressDetailsByTripId(params: FetchAddressDetailsByTripId$Params, context?: HttpContext): Observable<AddressDetail> {
    return this.fetchAddressDetailsByTripId$Response(params, context).pipe(
      map((r: StrictHttpResponse<AddressDetail>): AddressDetail => r.body)
    );
  }

  /** Path part for operation `fetchActivitiesByDayId()` */
  static readonly FetchActivitiesByDayIdPath = '/activity/get/{dayId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `fetchActivitiesByDayId()` instead.
   *
   * This method doesn't expect any request body.
   */
  fetchActivitiesByDayId$Response(params: FetchActivitiesByDayId$Params, context?: HttpContext): Observable<StrictHttpResponse<ActivityDetailedResponse>> {
    return fetchActivitiesByDayId(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `fetchActivitiesByDayId$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  fetchActivitiesByDayId(params: FetchActivitiesByDayId$Params, context?: HttpContext): Observable<ActivityDetailedResponse> {
    return this.fetchActivitiesByDayId$Response(params, context).pipe(
      map((r: StrictHttpResponse<ActivityDetailedResponse>): ActivityDetailedResponse => r.body)
    );
  }

  /** Path part for operation `fetchAddressDetailsByDayId()` */
  static readonly FetchAddressDetailsByDayIdPath = '/activity/get/{dayId}/day/details';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `fetchAddressDetailsByDayId()` instead.
   *
   * This method doesn't expect any request body.
   */
  fetchAddressDetailsByDayId$Response(params: FetchAddressDetailsByDayId$Params, context?: HttpContext): Observable<StrictHttpResponse<AddressDetail>> {
    return fetchAddressDetailsByDayId(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `fetchAddressDetailsByDayId$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  fetchAddressDetailsByDayId(params: FetchAddressDetailsByDayId$Params, context?: HttpContext): Observable<AddressDetail> {
    return this.fetchAddressDetailsByDayId$Response(params, context).pipe(
      map((r: StrictHttpResponse<AddressDetail>): AddressDetail => r.body)
    );
  }

  /** Path part for operation `deleteActivityById()` */
  static readonly DeleteActivityByIdPath = '/activity/{activityId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `deleteActivityById()` instead.
   *
   * This method doesn't expect any request body.
   */
  deleteActivityById$Response(params: DeleteActivityById$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return deleteActivityById(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `deleteActivityById$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  deleteActivityById(params: DeleteActivityById$Params, context?: HttpContext): Observable<void> {
    return this.deleteActivityById$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

}
