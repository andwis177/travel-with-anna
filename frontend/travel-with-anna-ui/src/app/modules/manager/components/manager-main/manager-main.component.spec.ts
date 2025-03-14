import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerMainComponent } from './manager-main.component';

describe('ManagerMainComponent', () => {
  let component: ManagerMainComponent;
  let fixture: ComponentFixture<ManagerMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerMainComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagerMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
