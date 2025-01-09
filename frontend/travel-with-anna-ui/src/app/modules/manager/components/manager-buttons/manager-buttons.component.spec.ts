import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerButtonsComponent } from './manager-buttons.component';

describe('ManagerButtonsComponent', () => {
  let component: ManagerButtonsComponent;
  let fixture: ComponentFixture<ManagerButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerButtonsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagerButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
