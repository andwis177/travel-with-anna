/* tslint:disable */
/* eslint-disable */
import { Activity } from '../models/activity';
import { Backpack } from '../models/backpack';
import { Day } from '../models/day';
export interface Note {
  activity?: Activity;
  backpack?: Backpack;
  day?: Day;
  id?: number;
  note?: string;
}
