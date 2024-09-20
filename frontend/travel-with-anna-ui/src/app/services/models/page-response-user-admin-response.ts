/* tslint:disable */
/* eslint-disable */
import { UserAdminResponse } from '../models/user-admin-response';
export interface PageResponseUserAdminResponse {
  content?: Array<UserAdminResponse>;
  first?: boolean;
  last?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
