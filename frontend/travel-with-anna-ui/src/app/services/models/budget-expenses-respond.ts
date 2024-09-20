/* tslint:disable */
/* eslint-disable */
import { BudgetRequest } from '../models/budget-request';
import { ExpanseCalculator } from '../models/expanse-calculator';
import { ExpanseResponse } from '../models/expanse-response';
export interface BudgetExpensesRespond {
  budgetRequest?: BudgetRequest;
  expanses?: Array<ExpanseResponse>;
  overallPaidInTripCurrency?: number;
  overallPriceInTripCurrency?: number;
  sumsByCurrency?: {
[key: string]: ExpanseCalculator;
};
  totalDebtInTripCurrency?: number;
}
