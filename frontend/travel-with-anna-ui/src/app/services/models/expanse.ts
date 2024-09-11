/* tslint:disable */
/* eslint-disable */
import { Pdf } from '../models/pdf';
export interface Expanse {
  currency: string;
  exchangeRate: number;
  expanseId?: number;
  expanseName?: string;
  paid: number;
  paidInTripCurrency?: number;
  pdf?: Pdf;
  price: number;
  priceInTripCurrency?: number;
}
