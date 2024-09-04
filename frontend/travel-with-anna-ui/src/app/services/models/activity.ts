/* tslint:disable */
/* eslint-disable */
import { Badge } from '../models/badge';
import { Day } from '../models/day';
import { Expanse } from '../models/expanse';
import { Note } from '../models/note';
export interface Activity {
  badge?: Badge;
  day?: Day;
  expanse?: Expanse;
  id?: number;
  note?: Note;
}
