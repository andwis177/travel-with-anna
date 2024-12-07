import {Component, Inject, OnInit} from '@angular/core';
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {PdfReportService} from "../../../../../../services/pdf/pdf-report.service";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatToolbarRow} from "@angular/material/toolbar";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-pdf-expanse',
  standalone: true,
  imports: [
    MatIcon,
    MatIconButton,
    MatToolbarRow,
    NgIf
  ],
  templateUrl: './pdf-expanse.component.html',
  styleUrl: './pdf-expanse.component.scss'
})
export class PdfExpanseComponent implements OnInit {
  pdfUrl: SafeResourceUrl | null = null;
  tripId: number;

  constructor(private pdfReportService: PdfReportService,
              private sanitizer: DomSanitizer,
              private dialog: MatDialog,
              @Inject(MAT_DIALOG_DATA) public data: {tripId: number}) {
    this.tripId = data.tripId;
  }

  ngOnInit(): void {
    this.loadPdf(this.tripId);
  }

  loadPdf(tripId: number): void {
    this.pdfReportService.getExpansePdf(tripId).subscribe((data) => {
      const blob = new Blob([data], { type: 'application/pdf' });
      const objectUrl = URL.createObjectURL(blob);
      this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(objectUrl);
    });
  }

  onClose(): void {
    this.dialog.closeAll();
  }
}
