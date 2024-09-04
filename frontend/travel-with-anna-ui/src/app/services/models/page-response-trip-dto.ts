/* tslint:disable */
/* eslint-disable */
import { TripDto } from '../models/trip-dto';
export interface PageResponseTripDto {
  content?: Array<TripDto>;
  first?: boolean;
  last?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
