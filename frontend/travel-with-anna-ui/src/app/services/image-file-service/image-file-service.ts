/* tslint:disable */
/* eslint-disable */
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {BaseService} from '../base-service';
import {ApiConfiguration} from '../api-configuration';

@Injectable({ providedIn: 'root' })
export class ImageFileService extends BaseService {
  base64ToString: string | ArrayBuffer = '';
  constructor(config: ApiConfiguration,
              http: HttpClient) {
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

  convertFileToBase64(file: File): Promise<string | ArrayBuffer | null> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = error => reject(error);
    });
  }

  async convertBase64ToString(file: File): Promise<string> {
    try {
      const base64 = await this.convertFileToBase64(file);
      if (base64) {
        this.base64ToString = base64.toString();
        return this.base64ToString;
      } else {
        console.error('Conversion to base64 failed');
        return '';
      }
    } catch (error) {
      console.error(error);
      throw error;
    }
  }
}
