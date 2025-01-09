/* tslint:disable */
/* eslint-disable */
export interface ExpanseRequest {
  currency: string;
  date?: string;
  entityId?: number;
  entityType?: string;
  exchangeRate: number;
  expanseCategory: string;
  expanseId?: number;
  expanseName?: string;
  paid: number;
  paidInTripCurrency?: number;
  price: number;
  priceInTripCurrency?: number;
  tripId?: number;
}
