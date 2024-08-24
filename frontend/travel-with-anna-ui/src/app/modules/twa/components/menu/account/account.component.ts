import {Component, ElementRef, HostListener, OnInit, signal, ViewChild} from '@angular/core';
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardModule} from "@angular/material/card";
import {MatButton, MatButtonModule} from "@angular/material/button";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {UserService} from "../../../../../services/services/user.service";
import {MatToolbarRow} from "@angular/material/toolbar";
import {NgForOf, NgIf, NgOptimizedImage} from "@angular/common";
import {PasswordComponent} from "../password/password.component";
import {MatIcon} from "@angular/material/icon";
import {MatDivider} from "@angular/material/divider";
import {UserCredentials} from "../../../../../services/models/user-credentials";
import {Router} from "@angular/router";
import {SharedService} from "../../../../../services/shared/shared.service";
import {MatTooltip} from "@angular/material/tooltip";
import {AuthService} from "../../../../../services/auth/auth.service";
import {ImageFileService} from "../../../../../services/image-file-service/image-file-service";
import {UserInformationService} from "../../../../../services/user-information/user-information-service";

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatCardActions,
    MatButton,
    MatCardModule,
    MatButtonModule,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    FormsModule,
    MatToolbarRow,
    NgIf,
    MatIcon,
    MatDivider,
    NgForOf,
    MatSuffix,
    MatTooltip,
    NgOptimizedImage
  ],
  templateUrl: './account.component.html',
  styleUrl: './account.component.scss',

})
export class AccountComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef;
  userCredentials: UserCredentials = {userName: '', email: '', password: ''};
  errorMsg: Array<string> = [];
  isEdit: boolean = false;
  selectedFile: File | null = null;
  avatarImg: string | null = null;
  stringImg: string | ArrayBuffer = '';

  constructor(
    public router: Router,
    public dialogRef: MatDialogRef<AccountComponent>,
    public dialog: MatDialog,
    private sharedService: SharedService,
    private userService: UserService,
    private authService: AuthService,
    private userInformationService: UserInformationService,
    private imageFileService: ImageFileService
  ) {}

  hide = signal(true);
  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    this.onClose();
  }

  @HostListener('document:keydown.enter', ['$event'])
  onEnterKeydownHandler(event: KeyboardEvent): void {
    if (this.isEdit) {
      this.saveUserCredentials();
    } else {
      this.edit();
    }
  }

  ngOnInit(): void {
    this.errorMsg = [];
    this.sharedService.getImage().subscribe((image) => {
      this.avatarImg = image;
    });
    this.userCredentials.userName = this.userInformationService.getUserName() as string;
    this.userCredentials.email = this.userInformationService.getEmail() as string;
    this.userCredentials.role = this.userInformationService.getRole() as string;
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if (file.type.startsWith('image/jpg') || file.type.startsWith('image/jpeg')) {
        this.selectedFile = file;
        this.saveUserAvatar(file).then(r => console.log('Avatar saved'));
        this.sharedService.updateAvatarImg(URL.createObjectURL(file));
      } else {
        this.errorMsg.push('Invalid file type. Only images are allowed.');
      }
    } else {
      this.errorMsg.push('No file selected');
    }
  }

  triggerFileInput() {
    if (this.fileInput && this.fileInput.nativeElement) {
      this.fileInput.nativeElement.click();
    } else {
      console.error('File input element is not available.');
    }
  }

  async saveUserAvatar(file: File) {
    this.errorMsg = [];
    try {
      this.imageFileService.uploadAvatar(file).subscribe();
      this.stringImg = await this.imageFileService.convertBase64ToString(file);
      this.sharedService.storeImage(this.stringImg);
    } catch (err) {
      console.error('Error occurred:', err);
      this.errorMsg.push('Failed to upload avatar or convert image.');
    }
  }

  onClose(): void {
    this.dialogRef.close();
  }

  edit() {
    this.userCredentials.password = '';
    this.isEdit = true;
  }

  saveUserCredentials() {
    this.errorMsg = [];
    this.userService.update({body: this.userCredentials})
      .subscribe({
        next: (response) => {
          this.authService.setToken(response.token as string);
          this.userInformationService.setUserCredentials(
            response.userName as string, response.email as string, response.role as string);
          this.sharedService.updateUserName(this.userCredentials.userName as string);
          this.isEdit = false;
          this.userCredentials.password = '';
        },
        error: (err) => {
          console.log(err);
          if (err.error.errors && err.error.errors.length > 0) {
            this.errorMsg = err.error.errors;
          } else {
            this.errorMsg.push('Unexpected error occurred');
          }
        }
      })
  }

  managePassword() {
    this.dialogRef.close();
    this.dialog.open(PasswordComponent);
  }

  deleteAccount() {
    this.router.navigate(['delete-account']).then(r => this.dialogRef.close());
  }
}
