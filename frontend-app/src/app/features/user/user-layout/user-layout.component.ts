import { Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterLink, RouterOutlet } from '@angular/router';
import { ThemeService } from '../../../core/services/theme.service';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { SearchService } from '../../../shared/data-access/search.service';
import { Subject } from 'rxjs/internal/Subject';
import { debounceTime } from 'rxjs/internal/operators/debounceTime';
import { distinctUntilChanged } from 'rxjs/internal/operators/distinctUntilChanged';
import { switchMap } from 'rxjs/internal/operators/switchMap';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { ElasticSearchItem } from '../../../shared/models/elastic.model';
import { environment } from '../../../enviroments/environment';
import { FooterComponent } from '../../../shared/footer/footer.component';
@Component({
  selector: 'app-user-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink,FormsModule,FooterComponent ],
  templateUrl: './user-layout.component.html',
  styleUrls: ['./user-layout.component.css'],
})
export class UserLayoutComponent  implements OnInit {

  menuOpen = false;
  guestMenuOpen = false;
  
  keyword = '';
  suggests: ElasticSearchItem[] = [];
  isLoggedIn = false;

  displayName = "user";

  @ViewChild('searchBox') searchBox!: ElementRef;
  theme: 'light' | 'dark' = 'light';

  fileBaseUrl =  environment.fileBaseUrl;
  constructor(private themeService: ThemeService,
     public auth: AuthService,
      private router: Router,
       private noti: NotificationService,
      private searchService: SearchService) {
    const mode = this.themeService.getMode();
    this.themeService.apply(mode);
    this.themeService.watchSystemChanges(true);
    // reflect current applied theme for the toggle icon
    this.theme = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
    window.addEventListener('click', () => (this.menuOpen = false));
    
    }


  private search$ = new Subject<string>();

  ngOnInit() {
    this.search$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(k => {
        const keyword = k.trim();
        if (!keyword) return of([]);
        return this.searchService.search(keyword, 'relevance');
      })
      )
      .subscribe(res => {
      this.suggests = (res ?? []).slice(0, 6);
    });
  }

  goDetail(id: string | undefined) {
    this.menuOpen = false;
    this.guestMenuOpen = false;
    this.clearSearch();
    this.router.navigate(['/products', id]);
  }

  getTypedPart(name: string): string {
    const k = this.keyword.toLowerCase();
    const n = name.toLowerCase();

    if (!n.startsWith(k)) return '';
    return name.slice(0, this.keyword.length);
  }

  getRemainPart(name: string): string {
    const k = this.keyword.toLowerCase();
    const n = name.toLowerCase();

    if (!n.startsWith(k)) return name;
    return name.slice(this.keyword.length);
  }
  onSearch() {
  this.search$.next(this.keyword);
}
  goSearch() {
  this.menuOpen = false;
  this.guestMenuOpen = false;
  this.router.navigate(['/search'], {
    queryParams: { q: this.keyword }
  });
}

getFileUrl(path?: string): string {
    if (!path) return 'https://placehold.co/1200x600';

    // backend đôi khi trả full URL
    if (path.startsWith('http')) return path;

    const base = this.fileBaseUrl.replace(/\/$/, '');
    const p = path.replace(/^\//, '');
    return `${base}/${p}`;
  }

 @HostListener('document:mousedown', ['$event'])
  handleClickOutside(event: MouseEvent) {
    if (!this.searchBox?.nativeElement.contains(event.target)) {
      this.clearSearch();
    }
  }

  clearSearch() {
    this.suggests = [];
    this.keyword = '';
  }
  toggleTheme() {
    this.theme = this.theme === 'light' ? 'dark' : 'light';
    this.themeService.setMode(this.theme);
  }

  @HostListener('document:click')
  clickOut() {
    this.menuOpen = false;
    this.guestMenuOpen = false;
  }
  toggleMenu(evt: MouseEvent) {
    evt.stopPropagation();
    this.menuOpen = !this.menuOpen;
    this.guestMenuOpen = false;
  }
  toggleGuestMenu(evt: MouseEvent) {
    evt.stopPropagation();
    this.guestMenuOpen = !this.guestMenuOpen;
    this.menuOpen = false;
  }
  isLogin(){
    return this.auth.isLoggedIn();
  }

  selectSuggest(suggest: string) {
    this.menuOpen = false;
    this.guestMenuOpen = false;
  }

  getDisplayName() : String{
    if(this.isLogin()){
      return this.auth.getCurrentUser()?.fullName ?? this.displayName;
    }
    return this.displayName;    
  }
  getDisplayRole() : String{
    if(this.isLogin()){
      if(this.auth.getCurrentUser()?.roles.includes('ADMIN')){
        return "Admin";
      }
      if(this.auth.getCurrentUser()?.roles.includes('MANAGER')){
        return "Manager";
      }
      return "Member";
    }
    return this.displayName;    
  }
  get isAdmin() { return this.auth.getCurrentUser()?.roles.includes('ADMIN') ?? false; }
  get isManager() { return this.auth.getCurrentUser()?.roles.includes('MANAGER') ?? false; }
  logout() {
    this.auth.logout().subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (e) => {
        this.noti.error(e?.error?.message ?? 'Đăng xuất không thành công, vui lòng thử lại');
      }
    });
  }
}
