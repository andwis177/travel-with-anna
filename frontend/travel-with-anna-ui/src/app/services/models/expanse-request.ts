/* tslint:disable */
/* eslint-disable */
export interface ExpanseRequest {
  currency: string;
  entityId?: number;
  entityType?: string;
  exchangeRate: number;
  expanseId?: number;
  expanseName?: string;
  paid: number;
  paidInTripCurrency?: number;
  price: number;
  priceInTripCurrency?: number;
  tripId?: number;
}
