/* tslint:disable */
/* eslint-disable */
import { TripResponse } from '../models/trip-response';
export interface PageResponseTripResponse {
  content?: Array<TripResponse>;
  first?: boolean;
  last?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
