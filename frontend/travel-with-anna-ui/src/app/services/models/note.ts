/* tslint:disable */
/* eslint-disable */
import { Activity } from '../models/activity';
import { Day } from '../models/day';
import { Trip } from '../models/trip';
export interface Note {
  activity?: Activity;
  day?: Day;
  note?: string;
  noteId?: number;
  trip?: Trip;
}
