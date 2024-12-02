import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TripListButtonsComponent } from './trip-list-buttons.component';

describe('TripListButtonsComponent', () => {
  let component: TripListButtonsComponent;
  let fixture: ComponentFixture<TripListButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripListButtonsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TripListButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
