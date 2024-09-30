import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DayDeleteComponent } from './day-delete.component';

describe('DayDeleteComponent', () => {
  let component: DayDeleteComponent;
  let fixture: ComponentFixture<DayDeleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DayDeleteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DayDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
