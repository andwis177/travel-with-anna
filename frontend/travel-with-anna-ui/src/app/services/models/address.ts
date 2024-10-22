/* tslint:disable */
/* eslint-disable */
import { Activity } from '../models/activity';
export interface Address {
  activities?: Array<Activity>;
  address?: string;
  addressId?: number;
  city?: string;
  country?: string;
  countryCode?: string;
  currency?: string;
  email?: string;
  phone?: string;
  place?: string;
  website?: string;
}
