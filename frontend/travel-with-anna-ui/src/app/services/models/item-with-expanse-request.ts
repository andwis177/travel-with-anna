/* tslint:disable */
/* eslint-disable */
import { ExpanseRequest } from '../models/expanse-request';
export interface ItemWithExpanseRequest {
  expanseRequest?: ExpanseRequest;
  item?: string;
  packed?: boolean;
  qty?: string;
}
