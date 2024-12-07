import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {PdfReportService} from "../../../../../../services/pdf/pdf-report.service";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatToolbarRow} from "@angular/material/toolbar";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-pdf-report',
  standalone: true,
  imports: [
    MatIcon,
    MatIconButton,
    MatToolbarRow,
    NgIf
  ],
  templateUrl: './pdf-report.component.html',
  styleUrl: './pdf-report.component.scss'
})
export class PdfReportComponent implements OnInit, OnDestroy {
  tripId: number;
  reportName: string = '';
  pdfUrl: SafeResourceUrl | undefined;

  constructor(private pdfReportService: PdfReportService,
              private sanitizer: DomSanitizer,
              private dialog: MatDialog,
              @Inject(MAT_DIALOG_DATA) public data: {
                tripId: number,
                reportName: string
              }) {
    this.tripId = data.tripId;
    this.reportName = data.reportName
  }

  ngOnInit() {
    switch (this.reportName) {
      case  'Trip' : {this.loadTripPdf(this.tripId); break;}
        case 'Expanses' : {this.loadExpansesPdf(this.tripId); break;}
    }
  }

  ngOnDestroy(): void {
    URL.revokeObjectURL((this.pdfUrl as any).changingThisBreaksApplicationSecurity);
  }


  loadTripPdf(tripId: number): void {
    this.pdfReportService.getTripPdf(tripId).subscribe((data) => {
      const blob = new Blob([data], { type: 'application/pdf' });
      const objectUrl = URL.createObjectURL(blob);
      this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(objectUrl);
    });
  }

  loadExpansesPdf(tripId: number): void {
    this.pdfReportService.getExpansePdf(tripId).subscribe((data) => {
      const blob = new Blob([data], { type: 'application/pdf' });
      const objectUrl = URL.createObjectURL(blob);
      this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(objectUrl);
    });
  }

  onClose() {
    this.dialog.getDialogById('pdf-report-dialog')?.close();
  }
}
