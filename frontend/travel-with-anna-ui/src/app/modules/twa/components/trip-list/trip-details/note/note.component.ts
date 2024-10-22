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
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader} from "@angular/material/card";
import {NoteResponse} from "../../../../../../services/models/note-response";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {MatFormField} from "@angular/material/form-field";
import {FormsModule} from "@angular/forms";
import {MatInput} from "@angular/material/input";
import {NoteRequest} from "../../../../../../services/models/note-request";
import {SaveNoteForTrip$Params} from "../../../../../../services/fn/note/save-note-for-trip";
import {SaveNoteForActivity$Params} from "../../../../../../services/fn/note/save-note-for-activity";

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
  id: number;
  noteRequest: NoteRequest = {};
  noteResponse: NoteResponse = {};
  relatedEntity: string = '';

  constructor(private noteService: NoteService,
              public dialog: MatDialog,
              private errorService: ErrorService,
              @Inject(MAT_DIALOG_DATA) public data: {id: number, relatedTo: string}) {
    this.id = data.id;
    this.relatedEntity = data.relatedTo;
  }

  ngOnInit() {
    this.executeGetNote();
  }

  getNoteForTrip() {
    this.noteService.getNoteByTripId({tripId: this.id}).subscribe({
      next: (note) => {
        this.setNote(note);
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    });
  }

  private getNoteForActivity() {
    this.noteService.getNoteByActivityId({activityId: this.id}).subscribe({
      next: (note) => {
        this.setNote(note);
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    });
  }

  private getNoteForDay() {

  }

  setNote(note: NoteResponse) {
    this.noteResponse = note;
    this.noteRequest.note = this.noteResponse.note;
    this.noteRequest.entityId = this.id;
  }

  onClose() {
    this.dialog.getDialogById('note-dialog')?.close();
  }

  saveNoteForTrip() {
    this.errorMsg = [];
    const params: SaveNoteForTrip$Params = {body: this.noteRequest};
    this.noteService.saveNoteForTrip(params).subscribe({ next: () => {
        this.onClose();
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    });
  }

  saveNoteForActivity() {
    this.errorMsg = [];
    const params: SaveNoteForActivity$Params = {body: this.noteRequest};
    this.noteService.saveNoteForActivity(params).subscribe({ next: () => {
        this.onClose();
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    });
  }

  private saveNoteForDay() {

  }

  executeGetNote() {
    switch (this.relatedEntity) {
      case 'trip':
        this.getNoteForTrip();
        break;
      case 'activity':
        this.getNoteForActivity();
        break;
      case 'day':
        this.getNoteForDay();
        break;
      default:
        this.errorMsg = ['Invalid related entity'];
    }
  }

  executeNoteSave() {
    switch (this.relatedEntity) {
      case 'trip':
        this.saveNoteForTrip();
        break;
      case 'activity':
        this.saveNoteForActivity();
        break;
      case 'day':
        this.saveNoteForDay();
        break;
      default:
        this.errorMsg = ['Invalid related entity'];
    }
  }
}
