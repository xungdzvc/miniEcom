import { Component, ElementRef, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ImageFallbackDirective } from '../../../shared/directives/image-fallback.directive';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterLink, RouterOutlet,RouterLinkActive } from '@angular/router';
import { ThemeService } from '../../../core/services/theme.service';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { SearchService } from '../../../shared/data-access/search.service';
import { Subject, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, takeUntil } from 'rxjs/operators';
import { FormsModule } from '@angular/forms';
import { ElasticSearchItem } from '../../../shared/models/elastic.model';
import { resolveFileUrl } from '../../../shared/utils/file-url.util';
import { FooterComponent } from '../../../shared/footer/footer.component';
import { CategoryService } from '../../../shared/data-access/category.service';
import { Category } from '../../../shared/models/cartegory.model';
import { CartService } from '../../../shared/data-access/cart.service';
@Component({
  selector: 'app-user-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, FormsModule, FooterComponent, RouterLinkActive, ImageFallbackDirective],
  templateUrl: './user-layout.component.html',
  styleUrls: ['./user-layout.component.css'],
})
export class UserLayoutComponent implements OnInit, OnDestroy {
  // Trạng thái các menu
  menuOpen = false;
  guestMenuOpen = false;
  categoryMenuOpen = false;
  mobileMenuOpen = false;
  cartItemCount = 0;  
  keyword = '';
  suggests: ElasticSearchItem[] = [];
  displayName = "user";

  @ViewChild('searchBox') searchBox!: ElementRef;
  theme: 'light' | 'dark' = 'light';
  readonly getFileUrl = resolveFileUrl;
  categories: Category[] = [];
  private search$ = new Subject<string>();
  private destroy$ = new Subject<void>();

  constructor(
    private themeService: ThemeService,
    public auth: AuthService,
    private router: Router,
    private noti: NotificationService,
    private searchService: SearchService,
    private categoryService: CategoryService,
    private cartService: CartService
  ) {
    const mode = this.themeService.getMode();
    this.themeService.apply(mode);
    this.themeService.watchSystemChanges(true);
    this.theme = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
  }

  ngOnInit() {
    this.loadCategories();
    if (this.isLogin()) {
      this.cartService.refreshCartCount().subscribe();
      this.loadCartCount();
    }
    this.search$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(k => {
          const keyword = k.trim();
          if (!keyword) return of([]);
          return this.searchService.search(keyword, 'relevance');
        }),
        takeUntil(this.destroy$)
      )
      .subscribe(res => {
        this.suggests = (res ?? []).slice(0, 6);
      });

    // Đóng menu mobile khi chuyển trang
    this.router.events
      .pipe(takeUntil(this.destroy$))
      .subscribe(event => {
        if (event instanceof NavigationEnd) {
          this.mobileMenuOpen = false;
        }
      });
  }

  // --- Click Outside & Menu Toggles ---
  @HostListener('document:click', ['$event'])
  clickOut(event: MouseEvent) {
    this.menuOpen = false;
    this.guestMenuOpen = false;
    this.categoryMenuOpen = false;
    // Tự động đóng phần search gợi ý nếu click ra ngoài
    if (this.searchBox && !this.searchBox.nativeElement.contains(event.target)) {
      this.clearSearch();
    }
  }
  loadCategories() {
    this.categoryService.getCategoriesForLayout().subscribe({
      next: (res) => {
        this.categories = res.data || []; 
      },
      error: (err) => {
        console.error('Lỗi khi tải danh mục Layout:', err);
      }
    });
  }
  loadCartCount() {
    this.cartService.cartCount$
      .pipe(takeUntil(this.destroy$))
      .subscribe(count => {
        this.cartItemCount = count;
      });
  }

  toggleMenu(evt: MouseEvent) {
    evt.stopPropagation();
    this.closeAllMenus();
    this.menuOpen = true;
  }

  toggleGuestMenu(evt: MouseEvent) {
    evt.stopPropagation();
    this.closeAllMenus();
    this.guestMenuOpen = true;
  }

  toggleCategoryMenu(evt: MouseEvent) {
    evt.stopPropagation();
    const isCurrentlyOpen = this.categoryMenuOpen; 
    
    this.closeAllMenus(); 
    this.categoryMenuOpen = !isCurrentlyOpen; 
  }

  toggleMobileMenu(evt: MouseEvent) {
    evt.stopPropagation();
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  // Hàm tiện ích để đóng tất cả popup menu cùng lúc
  private closeAllMenus() {
    this.menuOpen = false;
    this.guestMenuOpen = false;
    this.categoryMenuOpen = false;
  }

  // --- Logic Search ---
  onSearch() {
    this.search$.next(this.keyword);
  }

  goSearch() {
    this.closeAllMenus();
    this.router.navigate(['/search'], { queryParams: { q: this.keyword } });
  }

  goDetail(id: string | undefined) {
    this.closeAllMenus();
    this.clearSearch();
    this.router.navigate(['/products', id]);
  }

  clearSearch() {
    this.suggests = [];
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

  // --- Helpers ---
  toggleTheme() {
    this.theme = this.theme === 'light' ? 'dark' : 'light';
    this.themeService.setMode(this.theme);
  }

  isLogin() { return this.auth.isLoggedIn(); }
  get isAdmin() { return this.auth.getCurrentUser()?.roles.includes('ADMIN') ?? false; }
  get isManager() { return this.auth.getCurrentUser()?.roles.includes('MANAGER') ?? false; }

  getDisplayName(): String {
    if (this.isLogin()) {
      return this.auth.getCurrentUser()?.fullName ?? this.displayName;
    }
    return this.displayName;
  }

  getDisplayRole(): String {
    if (this.isLogin()) {
      if (this.isAdmin) return "Admin";
      if (this.isManager) return "Manager";
      return "Member";
    }
    return this.displayName;
  }

  logout() {
    this.auth.logout().subscribe({
      next: () => this.router.navigate(['/']),
      error: (e) => this.noti.error(e?.error?.message ?? 'Đăng xuất không thành công')
    });
  }
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

}