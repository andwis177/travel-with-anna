import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BudgetCurrenciesComponent } from './budget-currencies.component';

describe('BudgetCurrenciesComponent', () => {
  let component: BudgetCurrenciesComponent;
  let fixture: ComponentFixture<BudgetCurrenciesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BudgetCurrenciesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BudgetCurrenciesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
