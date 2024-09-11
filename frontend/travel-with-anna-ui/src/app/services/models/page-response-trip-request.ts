/* tslint:disable */
/* eslint-disable */
import { TripRequest } from '../models/trip-request';
export interface PageResponseTripRequest {
  content?: Array<TripRequest>;
  first?: boolean;
  last?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
