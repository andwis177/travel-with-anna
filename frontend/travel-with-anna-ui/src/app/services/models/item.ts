/* tslint:disable */
/* eslint-disable */
import { Backpack } from '../models/backpack';
import { Expanse } from '../models/expanse';
export interface Item {
  backpack?: Backpack;
  expanse?: Expanse;
  itemId?: number;
  itemName?: string;
  packed?: boolean;
  quantity?: string;
}
