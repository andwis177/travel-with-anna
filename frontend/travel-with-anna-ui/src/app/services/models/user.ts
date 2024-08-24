/* tslint:disable */
/* eslint-disable */
import { Role } from '../models/role';
export interface User {
  accountLocked?: boolean;
  avatarId: number;
  createdDate?: string;
  email?: string;
  enabled?: boolean;
  lastModifiedDate?: string;
  name?: string;
  password?: string;
  role: Role;
  userId?: number;
  userName?: string;
}
