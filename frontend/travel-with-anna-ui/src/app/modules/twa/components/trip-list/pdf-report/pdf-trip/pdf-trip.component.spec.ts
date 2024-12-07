import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PdfTripComponent } from './pdf-trip.component';

describe('PdfTripComponent', () => {
  let component: PdfTripComponent;
  let fixture: ComponentFixture<PdfTripComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PdfTripComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PdfTripComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
