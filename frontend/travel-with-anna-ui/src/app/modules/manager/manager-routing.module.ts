import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthGuard} from "../../services/auth/auth.guard";
import {ManagerHomeComponent} from "./pages/manager-home/manager-home.component";
import {ManagerMainComponent} from "./components/manager-main/manager-main.component";

const routes: Routes = [
  {
    path: '',
    component: ManagerHomeComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'manager-main',
        component: ManagerMainComponent,
        canActivate: [AuthGuard]
      },
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ManagerRoutingModule { }
