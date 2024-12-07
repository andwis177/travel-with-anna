import {Component, Inject, OnInit} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatToolbarRow} from "@angular/material/toolbar";
import {MatDivider} from "@angular/material/divider";
import {NgForOf, NgIf} from "@angular/common";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {ErrorService} from "../../../../../../services/error/error.service";
import {NoteService} from "../../../../../../services/services/note.service";
import {NoteResponse} from "../../../../../../services/models/note-response";
import {FormsModule} from "@angular/forms";
import {NoteRequest} from "../../../../../../services/models/note-request";
import {SaveNote$Params} from "../../../../../../services/fn/note/save-note";

@Component({
  selector: 'app-note',
  standalone: true,
  imports: [
    MatIcon,
    MatIconButton,
    MatToolbarRow,
    MatDivider,
    NgForOf,
    NgIf,
    FormsModule
  ],
  templateUrl: './note.component.html',
  styleUrl: './note.component.scss'
})
export class NoteComponent implements OnInit {
  errorMsg: Array<string> = [];
  noteRequest: NoteRequest = {entityType: ""};
  noteResponse: NoteResponse = {};

  constructor(private noteService: NoteService,
              public dialog: MatDialog,
              private errorService: ErrorService,
              @Inject(MAT_DIALOG_DATA) public data: {
                entityId: number,
                entityType: string
              }) {
    this.noteRequest.entityId = data.entityId;
    this.noteRequest.entityType = data.entityType;
  }

  ngOnInit() {
    if (this.noteRequest.entityId && this.noteRequest.entityType) {
      this.getNote();
    }
  }

  getNote() {
    this.noteService.getNote({
      entityId: this.noteRequest.entityId!,
      entityType: this.noteRequest.entityType})
      .subscribe({
        next: (note) => {
          this.noteResponse = note;
        },
        error: (err) => {
          this.errorMsg = this.errorService.errorHandler(err);
        }
      });
  }

  setNote() {
    this.noteRequest.noteId = this.noteResponse.noteId;
    this.noteRequest.note = this.noteResponse.note;
  }

  onClose() {
    this.dialog.getDialogById('note-dialog')?.close();
  }

  closeAfterSave() {
    this.dialog.getDialogById('note-dialog')?.close();
  }

  saveNote() {
    this.errorMsg = [];
    this.setNote();
    if (this.noteRequest.entityId && this.noteRequest.entityType) {
      const params: SaveNote$Params = {body: this.noteRequest};
      this.noteService.saveNote(params).subscribe({
        next: () => {
          this.closeAfterSave()
        },
        error: (err) => {
          this.errorMsg = this.errorService.errorHandler(err);
        }
      });
    }
  }
}
