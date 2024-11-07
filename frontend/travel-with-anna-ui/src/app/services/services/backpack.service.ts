/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { addItemToBackpack } from '../fn/backpack/add-item-to-backpack';
import { AddItemToBackpack$Params } from '../fn/backpack/add-item-to-backpack';
import { BackpackResponse } from '../models/backpack-response';
import { deleteItem } from '../fn/backpack/delete-item';
import { DeleteItem$Params } from '../fn/backpack/delete-item';
import { getBackpackById } from '../fn/backpack/get-backpack-by-id';
import { GetBackpackById$Params } from '../fn/backpack/get-backpack-by-id';

@Injectable({ providedIn: 'root' })
export class BackpackService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `addItemToBackpack()` */
  static readonly AddItemToBackpackPath = '/backpack/{backpackId}/item';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `addItemToBackpack()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  addItemToBackpack$Response(params: AddItemToBackpack$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return addItemToBackpack(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `addItemToBackpack$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  addItemToBackpack(params: AddItemToBackpack$Params, context?: HttpContext): Observable<void> {
    return this.addItemToBackpack$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `getBackpackById()` */
  static readonly GetBackpackByIdPath = '/backpack/{backpackId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getBackpackById()` instead.
   *
   * This method doesn't expect any request body.
   */
  getBackpackById$Response(params: GetBackpackById$Params, context?: HttpContext): Observable<StrictHttpResponse<BackpackResponse>> {
    return getBackpackById(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getBackpackById$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getBackpackById(params: GetBackpackById$Params, context?: HttpContext): Observable<BackpackResponse> {
    return this.getBackpackById$Response(params, context).pipe(
      map((r: StrictHttpResponse<BackpackResponse>): BackpackResponse => r.body)
    );
  }

  /** Path part for operation `deleteItem()` */
  static readonly DeleteItemPath = '/backpack/{itemId}/item';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `deleteItem()` instead.
   *
   * This method doesn't expect any request body.
   */
  deleteItem$Response(params: DeleteItem$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return deleteItem(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `deleteItem$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  deleteItem(params: DeleteItem$Params, context?: HttpContext): Observable<void> {
    return this.deleteItem$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

}
