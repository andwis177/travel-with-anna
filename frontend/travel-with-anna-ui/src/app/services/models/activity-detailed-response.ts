/* tslint:disable */
/* eslint-disable */
import { ActivityResponse } from '../models/activity-response';
import { AddressDetail } from '../models/address-detail';
export interface ActivityDetailedResponse {
  activities?: Array<ActivityResponse>;
  addressDetail?: AddressDetail;
  totalPayment?: number;
  totalPrice?: number;
}
