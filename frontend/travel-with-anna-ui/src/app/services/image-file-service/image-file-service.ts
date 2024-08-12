/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { uploadAvatar } from '../fn/avatar/upload-avatar';
import { UploadAvatar$Params } from '../fn/avatar/upload-avatar';

@Injectable({ providedIn: 'root' })
export class ImageFileService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }
  uploadAvatar(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.http.post(this.rootUrl + '/avatar/upload-avatar', formData);
  }

  getAvatar(): Observable<Blob> {
    return this.http.get<Blob>(this.rootUrl + '/avatar/get-avatar', {responseType: 'blob' as 'json'});
  }
}
