/* tslint:disable */
/* eslint-disable */
import { Item } from '../models/item';
import { Note } from '../models/note';
import { Trip } from '../models/trip';
export interface Backpack {
  id?: number;
  items?: Array<Item>;
  note?: Note;
  trip?: Trip;
}
