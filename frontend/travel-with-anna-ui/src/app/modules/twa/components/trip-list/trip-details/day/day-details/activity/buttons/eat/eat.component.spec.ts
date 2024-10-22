import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EatComponent } from './eat.component';

describe('EatComponent', () => {
  let component: EatComponent;
  let fixture: ComponentFixture<EatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EatComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
