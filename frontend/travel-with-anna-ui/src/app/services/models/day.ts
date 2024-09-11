/* tslint:disable */
/* eslint-disable */
import { Activity } from '../models/activity';
import { Note } from '../models/note';
export interface Day {
  activity?: Array<Activity>;
  date: string;
  dayId?: number;
  note?: Note;
}
