import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserListButtonsComponent } from './user-list-buttons.component';

describe('UserListButtonsComponent', () => {
  let component: UserListButtonsComponent;
  let fixture: ComponentFixture<UserListButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserListButtonsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserListButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
