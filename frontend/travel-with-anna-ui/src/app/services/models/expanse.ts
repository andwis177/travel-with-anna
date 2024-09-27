/* tslint:disable */
/* eslint-disable */
import { Activity } from '../models/activity';
import { Item } from '../models/item';
import { Trip } from '../models/trip';
export interface Expanse {
  activity?: Activity;
  currency: string;
  exchangeRate: number;
  expanseId?: number;
  expanseName?: string;
  item?: Item;
  paid: number;
  paidInTripCurrency?: number;
  price: number;
  priceInTripCurrency?: number;
  trip?: Trip;
}
