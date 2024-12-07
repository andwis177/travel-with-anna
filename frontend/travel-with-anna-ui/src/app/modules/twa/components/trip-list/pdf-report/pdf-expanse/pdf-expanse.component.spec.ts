import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PdfExpanseComponent } from './pdf-expanse.component';

describe('PdfExpanseComponent', () => {
  let component: PdfExpanseComponent;
  let fixture: ComponentFixture<PdfExpanseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PdfExpanseComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PdfExpanseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
