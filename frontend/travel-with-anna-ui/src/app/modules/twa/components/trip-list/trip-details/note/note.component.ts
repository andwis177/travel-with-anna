import {Component, Inject, OnInit} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatToolbarRow} from "@angular/material/toolbar";
import {MatDivider} from "@angular/material/divider";
import {MatTooltip} from "@angular/material/tooltip";
import {NgForOf, NgIf} from "@angular/common";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {ErrorService} from "../../../../../../services/error/error.service";
import {NoteService} from "../../../../../../services/services/note.service";
import {CreateNewNoteForTrip$Params} from "../../../../../../services/fn/note/create-new-note-for-trip";
import {NoteForTripRequest} from "../../../../../../services/models/note-for-trip-request";
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader} from "@angular/material/card";
import {NoteResponse} from "../../../../../../services/models/note-response";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {MatFormField} from "@angular/material/form-field";
import {FormsModule} from "@angular/forms";
import {MatInput} from "@angular/material/input";

@Component({
  selector: 'app-note',
  standalone: true,
  imports: [
    MatIcon,
    MatIconButton,
    MatToolbarRow,
    MatDivider,
    MatTooltip,
    NgForOf,
    NgIf,
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatCardFooter,
    MatChipSet,
    MatChip,
    MatFormField,
    FormsModule,
    MatInput
  ],
  templateUrl: './note.component.html',
  styleUrl: './note.component.scss'
})
export class NoteComponent implements OnInit {
  errorMsg: Array<string> = [];
  tripId: number;
  noteRequest: NoteForTripRequest = {};
  noteResponse: NoteResponse = {};


  constructor(private noteService: NoteService,
              public dialog: MatDialog,
              private errorService: ErrorService,
              @Inject(MAT_DIALOG_DATA) public data: {tripId: number}) {
    this.tripId = data.tripId;
  }

  ngOnInit() {
    this.getNote();

  }

  getNote() {
    this.noteService.getNoteById({tripId: this.tripId}).subscribe({
      next: (note) => {
        this.noteResponse = note;
        this.noteRequest.note = this.noteResponse.note;
        this.noteRequest.tripId = this.tripId;
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    });
  }


  onClose() {
    this.dialog.getDialogById('note-dialog')?.close();
  }

  saveNote() {
    this.errorMsg = [];

    const params: CreateNewNoteForTrip$Params = {body: this.noteRequest};
    this.noteService.createNewNoteForTrip(params).subscribe({ next: () => {
        this.onClose();
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    });
  }
}
