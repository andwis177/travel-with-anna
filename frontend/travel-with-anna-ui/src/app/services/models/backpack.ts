/* tslint:disable */
/* eslint-disable */
import { Item } from '../models/item';
import { Trip } from '../models/trip';
export interface Backpack {
  backpackId?: number;
  items?: Array<Item>;
  trip?: Trip;
}
