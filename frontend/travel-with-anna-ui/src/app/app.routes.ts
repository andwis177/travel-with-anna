import { Routes } from '@angular/router';
import {LoginComponent} from "./pages/login/login.component";
import {RegisterComponent} from "./pages/register/register.component";
import {ActivateAccountComponent} from "./pages/activate-account/activate-account.component";
import {ResetPasswordComponent} from "./pages/reset-password/reset-password.component";
import {AuthGuard} from "./services/auth/auth.guard";
import {DeleteAccountComponent} from "./pages/delete-account/delete-account.component";

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
    path:'delete-account',
    component: DeleteAccountComponent
  },
  {
    path: 'twa',
    loadChildren: () => import('./modules/twa/twa.module').then(m => m.TwaModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'admin',
    loadChildren: () => import('./modules/admin/admin.module').then(m => m.AdminModule),
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
