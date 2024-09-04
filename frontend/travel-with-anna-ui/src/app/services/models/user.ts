/* tslint:disable */
/* eslint-disable */
import { Role } from '../models/role';
import { Trip } from '../models/trip';
export interface User {
  accountLocked?: boolean;
  avatarId: number;
  createdDate?: string;
  email?: string;
  enabled?: boolean;
  lastModifiedDate?: string;
  name?: string;
  ownedTrips?: Array<Trip>;
  password?: string;
  role: Role;
  tripsToView?: Array<Trip>;
  userId?: number;
  userName?: string;
}
