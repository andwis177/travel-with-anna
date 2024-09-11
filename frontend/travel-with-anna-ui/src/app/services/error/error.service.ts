import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  constructor() { }

  errorHandler(err: any): Array<string> | any {
    const errorMsg: Array<string> = [];
    console.log(err.error.errors);
    if (err.error.errors && err.error.errors.length > 0) {
      return err.error.errors;
    } else {
      errorMsg.push('Unexpected error occurred');
      return errorMsg;
    }
  }
  errorHandlerWithJson(err: any): Array<string> | any {
    console.log(err);
    let jsonObject: any;
    let errorMsg: Array<string> = [];
    jsonObject = JSON.parse(err.error)
    if (jsonObject.errors ) {
      errorMsg = jsonObject.errors;
    } else {
      errorMsg.push('Unexpected error occurred');
    }
    return errorMsg;
  }
}
