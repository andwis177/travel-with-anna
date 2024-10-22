/* tslint:disable */
/* eslint-disable */
import { Address } from '../models/address';
import { Day } from '../models/day';
import { Expanse } from '../models/expanse';
import { Note } from '../models/note';
export interface Activity {
  activityId?: number;
  activityTitle?: string;
  address?: Address;
  associatedId?: number;
  badge?: string;
  beginTime?: string;
  day?: Day;
  endTime?: string;
  expanse?: Expanse;
  note?: Note;
  status?: string;
  type?: string;
}
