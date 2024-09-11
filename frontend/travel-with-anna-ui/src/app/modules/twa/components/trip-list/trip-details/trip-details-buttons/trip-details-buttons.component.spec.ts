import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TripDetailsButtonsComponent } from './trip-details-buttons.component';

describe('TripDetailsButtonsComponent', () => {
  let component: TripDetailsButtonsComponent;
  let fixture: ComponentFixture<TripDetailsButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripDetailsButtonsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TripDetailsButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
