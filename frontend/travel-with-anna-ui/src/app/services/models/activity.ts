/* tslint:disable */
/* eslint-disable */
import { Badge } from '../models/badge';
import { Expanse } from '../models/expanse';
import { Note } from '../models/note';
export interface Activity {
  activityId?: number;
  badge?: Badge;
  expanse?: Expanse;
  note?: Note;
}
