import {Component, OnInit} from '@angular/core';
import {Router, RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-manager-home',
    imports: [
        RouterOutlet
    ],
  templateUrl: './manager-home.component.html',
  styleUrl: './manager-home.component.scss'
})
export class ManagerHomeComponent implements OnInit{

  constructor(private router : Router)
  {}

  // @HostListener('window:beforeunload', ['$event'])
  // clearLocalStorage(event: any): void {
  //   localStorage.clear();
  // }

  ngOnInit(): void {
    this.router.navigate(['/manager/manager-main']).then();
  }
}
