/* tslint:disable */
/* eslint-disable */
import { Activity } from '../models/activity';
import { ActivityLog } from '../models/activity-log';
import { Note } from '../models/note';
import { Trip } from '../models/trip';
export interface Day {
  activity?: Array<Activity>;
  date: string;
  dayId?: number;
  logId?: Array<ActivityLog>;
  note?: Note;
  trip?: Trip;
}
