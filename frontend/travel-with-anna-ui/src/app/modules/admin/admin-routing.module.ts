import {NgModule} from '@angular/core';

import {AuthGuard} from "../../services/auth/auth.guard";
import {RouterModule, Routes} from "@angular/router";
import {AdminHomeComponent} from "./pages/admin-home/admin-home.component";
import {UsersListComponent} from "./components/users-list/users-list.component";

const routes: Routes = [
  {
    path: '',
    component: AdminHomeComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'users-list',
        component: UsersListComponent,
        canActivate: [AuthGuard]
      },
    ]
  },

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
