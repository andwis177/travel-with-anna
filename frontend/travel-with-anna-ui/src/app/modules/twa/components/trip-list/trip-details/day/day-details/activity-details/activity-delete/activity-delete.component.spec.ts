import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityDeleteComponent } from './activity-delete.component';

describe('ActivityDeleteComponent', () => {
  let component: ActivityDeleteComponent;
  let fixture: ComponentFixture<ActivityDeleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivityDeleteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivityDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
