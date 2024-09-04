/* tslint:disable */
/* eslint-disable */
import { Trip } from '../models/trip';
export interface Budget {
  currency: string;
  id?: number;
  toSpend: number;
  trip?: Trip;
}
