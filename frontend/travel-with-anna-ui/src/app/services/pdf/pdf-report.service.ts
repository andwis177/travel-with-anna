import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ApiConfiguration} from "../api-configuration";
import {BaseService} from "../base-service";

@Injectable({
  providedIn: 'root'
})
export class PdfReportService extends BaseService{

  constructor(config: ApiConfiguration,
              http: HttpClient) {
    super(config, http);
  }

  getTripPdf(tripId: number) {
    console.log(this.rootUrl + '/pdf/reports/trip/' + tripId);
    return this.http.get(this.rootUrl + '/pdf/reports/trip/' + tripId, {
      responseType: 'arraybuffer'
    });
  }

  getExpansePdf(tripId: number) {
    console.log(this.rootUrl + '/pdf/reports/expanse/' + tripId);
    return this.http.get(this.rootUrl + '/pdf/reports/expanse/' + tripId, {
      responseType: 'arraybuffer'
    });
  }
}
