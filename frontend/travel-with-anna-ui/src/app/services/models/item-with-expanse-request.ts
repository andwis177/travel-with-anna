/* tslint:disable */
/* eslint-disable */
import { ExpanseRequest } from '../models/expanse-request';
export interface ItemWithExpanseRequest {
  expanseRequest?: ExpanseRequest;
  itemName?: string;
  packed?: boolean;
  qty?: string;
}
