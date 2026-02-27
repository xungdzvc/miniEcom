import { Input } from "@angular/core";
import { Component } from "@angular/core";
@Component({
  selector: 'app-form-section',
  standalone: true,
  styleUrls : ['./form-section.component.css'],
  template: `
    <section class="form-section">
      <h3 class="section-title">{{ title }}</h3>
      <ng-content></ng-content>
    </section>
  `
})
export class FormSectionComponent {
  @Input() title = '';
}
