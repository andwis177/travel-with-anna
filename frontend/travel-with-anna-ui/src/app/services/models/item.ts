/* tslint:disable */
/* eslint-disable */
import { Backpack } from '../models/backpack';
import { Expanse } from '../models/expanse';
export interface Item {
  backpack?: Backpack;
  expanse?: Expanse;
  id?: number;
  item: string;
  packed?: boolean;
  quantity?: number;
}
