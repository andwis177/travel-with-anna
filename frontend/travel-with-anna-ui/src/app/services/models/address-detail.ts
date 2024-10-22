/* tslint:disable */
/* eslint-disable */
import { City } from '../models/city';
import { Country } from '../models/country';
export interface AddressDetail {
  cities?: Array<City>;
  countries?: Array<Country>;
  lastCity?: City;
  lastCountry?: Country;
}
