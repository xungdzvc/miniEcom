import { Input } from "@angular/core";
import { Component } from "@angular/core";

@Component({
  selector: 'app-form-actions',
  standalone: true,
  styleUrls : ['./form-actions.component.css'],
  template: `
    <div class="form-actions">
      <button class="btn-submit" [disabled]="disabled">
        {{ submitText }}
      </button>
    </div>
  `
})
export class FormActionsComponent {
  @Input() submitText = 'Lưu';
  @Input() disabled = false;
}
