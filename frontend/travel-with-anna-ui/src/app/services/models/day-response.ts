/* tslint:disable */
/* eslint-disable */
import { ActivityResponse } from '../models/activity-response';
import { NoteResponse } from '../models/note-response';
export interface DayResponse {
  activity?: Array<ActivityResponse>;
  date?: string;
  dayId?: number;
  dayNumber?: number;
  dayOfWeek?: string;
  isToday?: boolean;
  note?: NoteResponse;
  tripId?: number;
}
