import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DayDetailsButtonsComponent } from './day-details-buttons.component';

describe('DayDetailsButtonsComponent', () => {
  let component: DayDetailsButtonsComponent;
  let fixture: ComponentFixture<DayDetailsButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DayDetailsButtonsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DayDetailsButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
