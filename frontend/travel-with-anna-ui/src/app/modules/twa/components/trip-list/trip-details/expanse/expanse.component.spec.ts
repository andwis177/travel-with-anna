import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpanseComponent } from './expanse.component';

describe('ExpanseComponent', () => {
  let component: ExpanseComponent;
  let fixture: ComponentFixture<ExpanseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpanseComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExpanseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
