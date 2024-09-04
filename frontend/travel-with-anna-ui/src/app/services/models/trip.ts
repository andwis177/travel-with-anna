/* tslint:disable */
/* eslint-disable */
import { Backpack } from '../models/backpack';
import { Budget } from '../models/budget';
import { Day } from '../models/day';
import { User } from '../models/user';
export interface Trip {
  backpack?: Backpack;
  budget?: Budget;
  days?: Array<Day>;
  id?: number;
  owner?: User;
  tripName?: string;
  viewers?: Array<User>;
}
