import {Component, Inject, OnInit} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatToolbarRow} from "@angular/material/toolbar";
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
    NgForOf,
    NgIf,
    FormsModule
  ],
  templateUrl: './note.component.html',
  styleUrl: './note.component.scss'
})
export class NoteComponent implements OnInit {
  errorMsg: Array<string> = [];
  noteRequest: NoteRequest = {linkedEntityType: ""};
  noteResponse: NoteResponse = {};

  constructor(private noteService: NoteService,
              public dialog: MatDialog,
              private errorService: ErrorService,
              @Inject(MAT_DIALOG_DATA) public data: {
                entityId: number,
                entityType: string
              }) {
    this.noteRequest.linkedEntityId = data.entityId;
    this.noteRequest.linkedEntityType = data.entityType;
  }

  ngOnInit() {
    if (this.noteRequest.linkedEntityId && this.noteRequest.linkedEntityType) {
      this.getNote();
    }
  }

  getNote() {
    this.noteService.getNote({
      entityId: this.noteRequest.linkedEntityId!,
      entityType: this.noteRequest.linkedEntityType})
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
    this.noteRequest.noteContent = this.noteResponse.note;
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
    if (this.noteRequest.linkedEntityId && this.noteRequest.linkedEntityType) {
      const params: SaveNote$Params = {body: this.noteRequest};
      this.noteService.saveNote(params).subscribe({
        next: () => {
          this.closeAfterSave()
        },
        error: (err) => {
          this.errorMsg = this.errorService.errorHandlerWithJson(err);
        }
      });
    }
  }
}
