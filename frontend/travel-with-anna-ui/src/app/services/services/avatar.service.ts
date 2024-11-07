/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { getCurrentUserAvatar } from '../fn/avatar/get-current-user-avatar';
import { GetCurrentUserAvatar$Params } from '../fn/avatar/get-current-user-avatar';
import { uploadAvatar } from '../fn/avatar/upload-avatar';
import { UploadAvatar$Params } from '../fn/avatar/upload-avatar';

@Injectable({ providedIn: 'root' })
export class AvatarService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `getCurrentUserAvatar()` */
  static readonly GetCurrentUserAvatarPath = '/avatar';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getCurrentUserAvatar()` instead.
   *
   * This method doesn't expect any request body.
   */
  getCurrentUserAvatar$Response(params?: GetCurrentUserAvatar$Params, context?: HttpContext): Observable<StrictHttpResponse<string>> {
    return getCurrentUserAvatar(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getCurrentUserAvatar$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getCurrentUserAvatar(params?: GetCurrentUserAvatar$Params, context?: HttpContext): Observable<string> {
    return this.getCurrentUserAvatar$Response(params, context).pipe(
      map((r: StrictHttpResponse<string>): string => r.body)
    );
  }

  /** Path part for operation `uploadAvatar()` */
  static readonly UploadAvatarPath = '/avatar';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `uploadAvatar()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  uploadAvatar$Response(params?: UploadAvatar$Params, context?: HttpContext): Observable<StrictHttpResponse<{
}>> {
    return uploadAvatar(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `uploadAvatar$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  uploadAvatar(params?: UploadAvatar$Params, context?: HttpContext): Observable<{
}> {
    return this.uploadAvatar$Response(params, context).pipe(
      map((r: StrictHttpResponse<{
}>): {
} => r.body)
    );
  }

}
