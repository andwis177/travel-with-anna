import { Component, OnInit } from '@angular/core';
import {Router, RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-admin-home',
  standalone: true,
    imports: [
        RouterOutlet
    ],
  templateUrl: './admin-home.component.html',
  styleUrl: './admin-home.component.scss'
})
export class AdminHomeComponent implements OnInit  {

  constructor(private router : Router)
  { }

  ngOnInit(): void {
    this.router.navigate(['/admin/users-list']).then();
  }



}
