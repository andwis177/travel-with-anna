import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TripListButtons } from './trip-list-buttons.component';

describe('TripListButtonsComponent', () => {
  let component: TripListButtons;
  let fixture: ComponentFixture<TripListButtons>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripListButtons]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TripListButtons);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
