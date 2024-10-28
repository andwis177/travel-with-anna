import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BudgetBadgesComponent } from './budget-badges.component';

describe('BudgetBadgesComponent', () => {
  let component: BudgetBadgesComponent;
  let fixture: ComponentFixture<BudgetBadgesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BudgetBadgesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BudgetBadgesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
