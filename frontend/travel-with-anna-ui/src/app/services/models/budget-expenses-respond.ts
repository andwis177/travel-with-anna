/* tslint:disable */
/* eslint-disable */
import { BudgetResponse } from '../models/budget-response';
import { ExpanseByCurrency } from '../models/expanse-by-currency';
import { ExpanseResponse } from '../models/expanse-response';
export interface BudgetExpensesRespond {
  budgetResponse?: BudgetResponse;
  expanses?: Array<ExpanseResponse>;
  overallPaidInTripCurrency?: number;
  overallPriceInTripCurrency?: number;
  paidBalanceDue?: number;
  priceBalanceDue?: number;
  sumsByCurrency?: Array<ExpanseByCurrency>;
  totalDebtInTripCurrency?: number;
}
