/* tslint:disable */
/* eslint-disable */
import { ExpanseResponse } from '../models/expanse-response';
export interface ItemRequest {
  expanse?: ExpanseResponse;
  itemId?: number;
  itemName?: string;
  packed?: boolean;
  qty?: string;
}
