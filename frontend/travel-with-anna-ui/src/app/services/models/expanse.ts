/* tslint:disable */
/* eslint-disable */
import { Activity } from '../models/activity';
import { Item } from '../models/item';
import { PdfDoc } from '../models/pdf-doc';
export interface Expanse {
  activity?: Activity;
  currency: string;
  exchangeRate: number;
  expanseName?: string;
  id?: number;
  item?: Item;
  paid: number;
  pdfDoc?: PdfDoc;
  price: number;
}
