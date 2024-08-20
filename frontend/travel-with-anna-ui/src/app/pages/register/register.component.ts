import {Component, HostListener, OnInit, ViewEncapsulation} from '@angular/core';
import {RegistrationRequest} from "../../services/models/registration-request";
import {FormsModule} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";
import {MatDivider} from "@angular/material/divider";
import {RoleService} from "../../services/services/role.service";
import {MatOption, MatSelect} from "@angular/material/select";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    FormsModule,
    MatButton,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatSuffix,
    NgForOf,
    NgIf,
    MatDivider,
    MatSelect,
    MatOption,
    NgClass
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  encapsulation: ViewEncapsulation.None
})
export class RegisterComponent implements OnInit {

  registerRequest: RegistrationRequest = {userName: '', email: '', password: '', confirmPassword: '', roleName: ''};
  roles: Array<string> = [];
  errorMsg: Array<string> = [];
  hidePassword: boolean = true;

  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private roleService: RoleService,
  ) {}

  ngOnInit(): void {
    this.roleService.getAllRoleNames()
      .subscribe( {
      next: (role) => {
        this.roles = role;
        this.registerRequest.roleName = this.roles[0];
        console.log(role);
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

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
   this.login();
  }

  @HostListener('document:keydown.enter', ['$event'])
  onEnterKeydownHandler(event: KeyboardEvent): void {
    this.register()
  }

  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }

  register() {
    this.errorMsg = [];
    this.authService.register({
      body: this.registerRequest
    }).subscribe( {
      next: () => {
        this.router.navigate(['activate-account']);
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

  login() {
    this.router.navigate(['login']).then(r => console.log('Login'));
  }

  getRoleClass(role: string): string {
    switch (role) {
      case 'ADMIN':
        return 'admin';
      default:
        return 'default';
    }
  }
}
