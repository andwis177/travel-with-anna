/* tslint:disable */
/* eslint-disable */
import { AddressResponse } from '../models/address-response';
import { ExpanseResponse } from '../models/expanse-response';
import { NoteResponse } from '../models/note-response';
export interface ActivityResponse {
  activityId?: number;
  activityTitle?: string;
  address?: AddressResponse;
  associatedId?: number;
  badge?: string;
  endTime?: string;
  expanse?: ExpanseResponse;
  note?: NoteResponse;
  startTime?: string;
  status?: string;
  type?: string;
}
