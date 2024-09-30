/* tslint:disable */
/* eslint-disable */
import { ActivityResponse } from '../models/activity-response';
import { Note } from '../models/note';
export interface DayResponse {
  activity?: Array<ActivityResponse>;
  date?: string;
  dayId?: number;
  dayOfWeek?: string;
  isToday?: boolean;
  note?: Note;
  tripId?: number;
}
