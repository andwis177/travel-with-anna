/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { getAllItemsByBackpackId } from '../fn/item/get-all-items-by-backpack-id';
import { GetAllItemsByBackpackId$Params } from '../fn/item/get-all-items-by-backpack-id';
import { ItemRequest } from '../models/item-request';
import { saveAllItemsFromTheList } from '../fn/item/save-all-items-from-the-list';
import { SaveAllItemsFromTheList$Params } from '../fn/item/save-all-items-from-the-list';

@Injectable({ providedIn: 'root' })
export class ItemService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `saveAllItemsFromTheList()` */
  static readonly SaveAllItemsFromTheListPath = '/item/save-list';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `saveAllItemsFromTheList()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  saveAllItemsFromTheList$Response(params: SaveAllItemsFromTheList$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return saveAllItemsFromTheList(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `saveAllItemsFromTheList$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  saveAllItemsFromTheList(params: SaveAllItemsFromTheList$Params, context?: HttpContext): Observable<void> {
    return this.saveAllItemsFromTheList$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }

  /** Path part for operation `getAllItemsByBackpackId()` */
  static readonly GetAllItemsByBackpackIdPath = '/item/{backpackId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getAllItemsByBackpackId()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllItemsByBackpackId$Response(params: GetAllItemsByBackpackId$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<ItemRequest>>> {
    return getAllItemsByBackpackId(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getAllItemsByBackpackId$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllItemsByBackpackId(params: GetAllItemsByBackpackId$Params, context?: HttpContext): Observable<Array<ItemRequest>> {
    return this.getAllItemsByBackpackId$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<ItemRequest>>): Array<ItemRequest> => r.body)
    );
  }

}
