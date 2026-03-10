import { Input } from "@angular/core";
import { Component } from "@angular/core";
@Component({
  selector: 'app-form-layout',
  standalone: true,
  styleUrls : ['./form-layout.component.css'],
  template: `
    <div class="form-layout">
      <h2 class="form-title">{{ title }}</h2>
      <ng-content></ng-content>
    </div>
  `
})
export class FormLayoutComponent {
  @Input() title = '';
}


