/* tslint:disable */
/* eslint-disable */
import { UserAdminView } from './user-admin-view';
export interface PageResponseUserAdminView {
  content?: Array<UserAdminView>;
  first?: boolean;
  last?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
