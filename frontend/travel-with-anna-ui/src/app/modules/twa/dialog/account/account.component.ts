import {Component, ElementRef, HostListener, OnInit, signal, ViewChild} from '@angular/core';
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardModule} from "@angular/material/card";
import {MatButton, MatButtonModule} from "@angular/material/button";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {UserService} from "../../../../services/services/user.service";
import {MatToolbarRow} from "@angular/material/toolbar";
import {NgForOf, NgIf} from "@angular/common";
import {PasswordComponent} from "../password/password.component";
import {MatIcon} from "@angular/material/icon";
import {MatDivider} from "@angular/material/divider";
import {UserCredentials} from "../../../../services/models/user-credentials";
import {TokenService} from "../../../../services/token/token.service";
import {Router} from "@angular/router";
import {SharedService} from "../../../../pages/shared/shared.service";

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatCardActions,
    MatButton,
    MatCardModule,
    MatButtonModule,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    FormsModule,
    MatToolbarRow,
    NgIf,
    MatIcon,
    MatDivider,
    NgForOf,
    MatSuffix
  ],
  templateUrl: './account.component.html',
  styleUrl: './account.component.scss',

})
export class AccountComponent implements OnInit {
  userCredentials: UserCredentials = { email: '', userName: '', password: ''};
  errorMsg: Array<string> = [];
  isEdit: boolean = false;

  constructor(
    public router: Router,
    public dialogRef: MatDialogRef<AccountComponent>,
    public dialog: MatDialog,
    private sharedService: SharedService,
    private userService: UserService,
    private tokenService: TokenService
  ) {}

  hide = signal(true);
  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    this.onClose();
  }

  @HostListener('document:keydown.enter', ['$event'])
  onEnterKeydownHandler(event: KeyboardEvent): void {
    if (this.isEdit) {
      this.save();
    } else {
      this.edit();
    }
  }

  ngOnInit(): void {
    this.errorMsg = [];
    this.userService.getProfile().subscribe({
      next: (response) => {
        this.userCredentials = response;
      },
      error: (err) => {
        console.error('Error fetching user details:', err);
      }
    })
  }

  onClose(): void {
    this.dialogRef.close();
  }

  edit() {
    this.userCredentials.password = '';
    this.isEdit = true;
  }

  save() {
    this.errorMsg = [];
    this.userService.update({body: this.userCredentials})
      .subscribe({
        next: (response) => {
          this.tokenService.token = response.token as string;
          this.sharedService.updateLabel(<string> this.userCredentials.userName);
          this.isEdit = false;
          this.userCredentials.password = '';
        },
        error: (err) => {
          console.log(err);
          if (err.error.errors && err.error.errors.length > 0) {
            this.errorMsg = err.error.errors;
          } else {
            this.errorMsg.push('Unexpected error occurred');
          }
        }
      })
  }

  managePassword() {
    this.dialogRef.close();
    this.dialog.open(PasswordComponent);
  }

  deleteAccount() {
    this.router.navigate(['delete-account']).then(r => this.dialogRef.close());
  }
}
