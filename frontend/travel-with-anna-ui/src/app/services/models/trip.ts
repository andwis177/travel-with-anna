/* tslint:disable */
/* eslint-disable */
import { Backpack } from '../models/backpack';
import { Budget } from '../models/budget';
import { Day } from '../models/day';
import { Expanse } from '../models/expanse';
import { Note } from '../models/note';
import { User } from '../models/user';
export interface Trip {
  backpack?: Backpack;
  budget?: Budget;
  days?: Array<Day>;
  expanses?: Array<Expanse>;
  note?: Note;
  owner?: User;
  tripId?: number;
  tripName?: string;
}
