import {Injectable} from '@angular/core';
import {LocalStorageService} from "../local-storage/local-storage.service";
import {DayResponse} from "../models/day-response";
import {TripResponse} from "../models/trip-response";
import {ActivityResponse} from "../models/activity-response";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {Country} from "../models/country";
import {City} from "../models/city";

@Injectable({
  providedIn: 'root'
})
export class SharedService {
  private userName = new BehaviorSubject<string |null>('');
  private userName$: Observable<string |null> = this.userName.asObservable();
  private avatarImg = new BehaviorSubject<string | null>(null);
  private avatarImg$:Observable<string | null> = this.avatarImg.asObservable();
  private userAdminEditId = new BehaviorSubject<number | null>(null);
  private userAdminEditId$:Observable<number | null> = this.userAdminEditId.asObservable();
  private userAdminViewIdentifier= new BehaviorSubject<string>('');
  private userAdminViewIdentifier$:Observable<string> = this.userAdminViewIdentifier.asObservable();
  private trip = new BehaviorSubject<TripResponse>({} as TripResponse);
  private trip$ = this.trip.asObservable();
  private tripDays = new BehaviorSubject<Array<DayResponse> | []>([]);
  private tripDays$ = this.tripDays.asObservable();
  private day = new BehaviorSubject<DayResponse>({} as DayResponse);
  private day$ = this.day.asObservable();
  private tripCurrency = new BehaviorSubject<string>('');
  private tripCurrency$ = this.tripCurrency.asObservable();
  private activity = new BehaviorSubject<ActivityResponse>({} as ActivityResponse);
  private activity$ = this.activity.asObservable();
  private getDayTrigger = new Subject<void>();
  public getDayTriggerEvent$ = this.getDayTrigger.asObservable();
  private getActivityTrigger = new Subject<void>();
  public getActivityTriggerEvent$ = this.getActivityTrigger.asObservable();

  private lastCountry = new BehaviorSubject<Country>({currency: '', name: '', iso2: '', iso3: ''});
  private lastCountry$ = this.lastCountry.asObservable();
  private lastCity = new BehaviorSubject<City>({city:''});
  private lastCity$ = this.lastCity.asObservable();

  private userNameKey: string = 'userName';
  private imageKey: string = 'image';

  constructor(
    private localStorageService: LocalStorageService)
  {}

  clean(): void {
    this.localStorageService.clear();
    this.userName.next(null);
    this.avatarImg.next(null);
  }

  updateUserName(newUserName: string): void {
    this.userName.next(newUserName);
    this.localStorageService.setItem(this.userNameKey, newUserName);
  }

  getUserName(): Observable<string | null> {
    this.userName.next(this.localStorageService.getItem(this.userNameKey));
    return this.userName$;
  }

  setUserAdminEditId(userAdminEditId: number | null): void {
      this.userAdminEditId.next(userAdminEditId);
  }

  getUserAdminEditId(): Observable<number | null> {
    return this.userAdminEditId$;
  }

  setUserAdminViewIdentifier(identifier: string): void {
      this.userAdminViewIdentifier.next(identifier);
  }

  getUserAdminViewIdentifier(): Observable<string> {
    return this.userAdminViewIdentifier$;
  }

  updateAvatarImg(newImg: string): void {
    this.avatarImg.next(newImg);
  }

  storeImage(base64: string | ArrayBuffer): void {
    this.localStorageService.setItem(this.imageKey, base64.toString());
  }

  getImage(): Observable<string | null> {
    this.avatarImg.next(this.localStorageService.getItem(this.imageKey));
    return this.avatarImg$;
  }

  setTrip(trip: TripResponse): void {
    this.trip.next(trip);
  }

  getTrip(): Observable<TripResponse | null> {
    return this.trip$;
  }

  setTripDays(days: Array<DayResponse>): void {
    this.tripDays.next(days);
  }

  getTripDays(): Observable<Array<DayResponse> | []> {
    return this.tripDays$;
  }

  setDay(day: DayResponse): void {
    this.day.next(day);
  }

  getDay(): Observable<DayResponse> {
    return this.day$;
  }

  setTripCurrency(currency: string): void {
    this.tripCurrency.next(currency);
  }

  getTripCurrency(): Observable<string> {
    return this.tripCurrency$;
  }

  setActivity(activity: ActivityResponse): void {
    this.activity.next(activity);
  }

  getActivity(): Observable<ActivityResponse> {
    return this.activity$;
  }

  getLastACountry(): Observable<Country> {
    return this.lastCountry$;
  }

  setLastCountry(country: Country): void {
    this.lastCountry.next(country);
  }

  getLastCity(): Observable<City> {
    return this.lastCity$;
  }

  setLastCity(city: City): void {
    this.lastCity.next(city);
  }



  triggerGetDays() {
    this.getDayTrigger.next();
  }

  triggerGetActivity() {
    this.getActivityTrigger.next();
  }
}

