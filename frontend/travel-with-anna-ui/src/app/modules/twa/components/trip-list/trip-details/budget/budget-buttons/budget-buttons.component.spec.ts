import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BudgetButtonsComponent } from './budget-buttons.component';

describe('BudgetButtonsComponent', () => {
  let component: BudgetButtonsComponent;
  let fixture: ComponentFixture<BudgetButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BudgetButtonsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BudgetButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
