import { Input } from "@angular/core";
import { Component } from "@angular/core";
import { AbstractControl } from "@angular/forms";
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-form-field',
  standalone: true,
  styleUrls : ['./form-field.component.css'],
  imports :[CommonModule],
  template: `
    <div class="form-group">
      <label>
        {{ label }}
        <span *ngIf="required" class="required">*</span>
      </label>

      <ng-content></ng-content>

      <div *ngIf="control?.invalid && control?.touched" class="error-message">
        {{ errorMessage }}
      </div>
    </div>
  `
})
export class FormFieldComponent {
  @Input() label = '';
  @Input() required = false;
  @Input() errorMessage = 'Trường này là bắt buộc';
  @Input() control!: AbstractControl | null;
}
