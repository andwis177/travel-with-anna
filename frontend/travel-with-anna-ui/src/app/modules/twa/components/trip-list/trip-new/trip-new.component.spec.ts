import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TripNewComponent } from './trip-new.component';

describe('TripNewComponent', () => {
  let component: TripNewComponent;
  let fixture: ComponentFixture<TripNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripNewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TripNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
