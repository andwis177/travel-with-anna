/* tslint:disable */
/* eslint-disable */
import { ActivityListResponse } from '../models/activity-list-response';
import { NoteResponse } from '../models/note-response';
export interface DayResponse {
  activities?: ActivityListResponse;
  date?: string;
  dayId?: number;
  dayNumber?: number;
  dayOfWeek?: string;
  isToday?: boolean;
  note?: NoteResponse;
  tripId?: number;
}
