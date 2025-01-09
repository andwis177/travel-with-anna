/* tslint:disable */
/* eslint-disable */
import { AddressRequest } from '../models/address-request';
export interface ActivityUpdateRequest {
  activityId: number;
  activityTitle?: string;
  addressRequest?: AddressRequest;
  dayId: number;
  dayTag?: boolean;
  endTime?: string;
  newActivityDate: string;
  oldActivityDate: string;
  startTime: string;
  type?: string;
}
