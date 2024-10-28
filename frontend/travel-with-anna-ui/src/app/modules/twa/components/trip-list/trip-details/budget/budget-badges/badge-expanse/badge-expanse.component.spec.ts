import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BadgeExpanseComponent } from './badge-expanse.component';

describe('BadgeExpanseComponent', () => {
  let component: BadgeExpanseComponent;
  let fixture: ComponentFixture<BadgeExpanseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BadgeExpanseComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BadgeExpanseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
