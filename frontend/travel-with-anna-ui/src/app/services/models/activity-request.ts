/* tslint:disable */
/* eslint-disable */
import { AddressRequest } from '../models/address-request';
export interface ActivityRequest {
  activityTitle?: string;
  addressRequest?: AddressRequest;
  badge?: string;
  dateTime: string;
  endTime?: string;
  status?: string;
  tripId: number;
  type?: string;
}
