import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from "./pages/home/home.component";
import {AuthGuard} from "../../services/auth/auth.guard";
import {TripListComponent} from "./components/trip-list/trip-list.component";
import {TripDetailsComponent} from "./components/trip-list/trip-details/trip-details.component";
import {BackpackComponent} from "./components/trip-list/trip-details/backpack/backpack.component";
import {BudgetComponent} from "./components/trip-list/trip-details/budget/budget.component";
import {DayDetailsComponent} from "./components/trip-list/trip-details/day/day-details/day-details.component";

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        component: TripListComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'trip-list',
        component: TripListComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'trip-details',
        component: TripDetailsComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'trip-details/:id',
        component: TripDetailsComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'backpack',
        component: BackpackComponent,
        canActivate: [AuthGuard]
      },
      // {
      //   path: 'backpack/:id',
      //   component: BackpackComponent,
      //   canActivate: [AuthGuard]
      // },
      {
        path: 'budget/:trip_id/:budget_id',
        component: BudgetComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'day/:day_id',
        component: DayDetailsComponent,
        canActivate: [AuthGuard]
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TwaRoutingModule {


}
