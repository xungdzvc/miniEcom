import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-terms',
  imports: [FooterComponent],
  templateUrl: './terms.component.html',
  styleUrls: ['./terms.component.css'],
})
export class TermsComponent implements OnInit {
  updatedAt = '2026';

  constructor(private title: Title) {}

  ngOnInit(): void {
    this.title.setTitle('Điều khoản dịch vụ | XungLord');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}