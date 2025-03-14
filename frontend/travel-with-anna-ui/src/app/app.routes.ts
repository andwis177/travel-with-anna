import { Routes } from '@angular/router';
import {LoginComponent} from "./pages/login/login.component";
import {RegisterComponent} from "./pages/register/register.component";
import {ActivateAccountComponent} from "./pages/activate-account/activate-account.component";
import {ResetPasswordComponent} from "./pages/reset-password/reset-password.component";
import {AuthGuard} from "./services/auth/auth.guard";
import {DeleteAccountComponent} from "./pages/delete-account/delete-account.component";
import {ActivationCodeComponent} from "./pages/activation-code/activation-code.component";

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'activate-account',
    component: ActivateAccountComponent
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent
  },
  {
    path: 'resend-code',
    component: ActivationCodeComponent
  },
  {
    path:'delete-account',
    component: DeleteAccountComponent
  },
  {
    path: 'twa',
    loadChildren: () => import('./modules/twa/twa.module').then(m => m.TwaModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'manager',
    loadChildren: () => import('./modules/manager/manager.module').then(m => m.ManagerModule),
    canActivate: [AuthGuard]
  },
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/login',
  }
];
