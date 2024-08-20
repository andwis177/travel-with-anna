import {Component, HostListener} from '@angular/core';
import {AuthenticationService} from "../../services/services/authentication.service";
import {Router} from "@angular/router";
import {CodeInputModule} from "angular-code-input";
import {NgForOf, NgIf} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {MatDivider} from "@angular/material/divider";

@Component({
  selector: 'app-activate-account',
  standalone: true,
  imports: [
    CodeInputModule,
    NgIf,
    MatButton,
    MatCard,
    MatCardHeader,
    MatIcon,
    NgForOf,
    MatCardContent,
    MatDivider
  ],
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.scss',
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition(':enter, :leave', [
        animate(5000)
      ]),
    ])
  ]
})
export class ActivateAccountComponent {
  errorMsg: Array<string> = [];
  message: string = '';
  isOkay: boolean = true;
  submitted: boolean = false;
  jsonObject: any;


  constructor(
    private router: Router,
    private authService: AuthenticationService
  )
  {}

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    if (this.submitted && this.isOkay) {
      this.redirectToLogin();
    }
    if (!this.isOkay && this.submitted) {
      this.submitted = false;
    }
  }

  @HostListener('document:keydown.enter', ['$event'])
  onEnterKeydownHandler(event: KeyboardEvent): void {
    if (!this.isOkay && this.submitted) {
      this.submitted = false;
    }
    if (this.submitted && this.isOkay) {
      this.redirectToLogin();
    }
  }

  onCodeCompleted(token: string) {
    this.confirmAccount(token);
  }

  redirectToLogin() {
    this.router.navigate(['login']).then(r => console.log('Login'));
  }

  private confirmAccount(token: string) {
    this.authService.confirm({token})
      .subscribe( {
        next: () => {
          this.message = 'Your account has been activated successfully. \nYou can now login.';
          this.submitted = true;
          this.isOkay = true;
        },
        error: (err) => {
          this.errorMsg = [];
          console.log(err);
          this.jsonObject = JSON.parse(err.error)
          if (this.jsonObject.errors ) {
            this.errorMsg = this.jsonObject.errors;
          } else {
            this.errorMsg.push('Unexpected error occurred');
          }
          this.submitted = true;
          this.isOkay = false;
        }
      });
  }
}
