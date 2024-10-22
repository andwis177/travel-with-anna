/* tslint:disable */
/* eslint-disable */
import { AddressRequest } from '../models/address-request';
export interface ActivityUpdateRequest {
  activityId: number;
  activityTitle?: string;
  addressRequest?: AddressRequest;
  dayId: number;
  endTime?: string;
  newDate: string;
  oldDate: string;
  startTime: string;
  type?: string;
}
