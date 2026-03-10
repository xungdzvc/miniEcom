import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { environment } from '../../../../../../enviroments/environment';import { switchMap, take } from 'rxjs';
import { ProductService } from '../../../../shared/data-access/products/product.service';
import { ProductViewerDetail } from '../../../../shared/models/core/product/product-viewer-detail.model';
import { ProductViewerListDetail } from '../../../../shared/models/core/product/product-viewer-list.model';
import { CartService } from '../../../../shared/data-access/cart.service';
import { AuthService } from '../../../../core/services/auth.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { AuthUser } from '../../../../shared/models/core/user/user-auth.model';
import { CheckoutComponent } from '../../checkout/checkout-modal.component';
import { SafeResourceUrl,DomSanitizer } from '@angular/platform-browser';
import { Review } from '../../../../shared/models/review-request.model';
import { ReviewService } from '../../../../shared/data-access/products/review.service';
import { FormsModule } from '@angular/forms';
import { ReviewResponse } from '../../../../shared/models/review-list.models';
import {  Router } from '@angular/router';
type ProductTab = 'overview' | 'tech' | 'guide' | 'media' | 'reviews' |'demo'|'demo2';


@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule,CheckoutComponent,FormsModule],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css'],
})
export class ProductDetailComponent implements OnInit, OnDestroy {
  
  tab: ProductTab = 'overview';
  fileBaseUrl = environment.fileBaseUrl;
  isOpenBuyNow = false;
  currentUser: AuthUser | null = null;
  product: ProductViewerDetail | null = null ;
  reviewsData: ReviewResponse[] = [];
  selectedImg: string | null = null;
  galleryImages: string[] = [];
  activeIndex = 0;
  autoMs = 3500;
  private timer: any = null;
  safeYoutubeUrl: SafeResourceUrl | null = null;
 
  canRate: boolean = false;
  userRating: number = 0;
  userComment: string = '';

  relatedProducts: ProductViewerListDetail[] = [];
  constructor(private route: ActivatedRoute,
    private productService : ProductService,
    private authService : AuthService,
    private cartService :CartService,
    private notify: NotificationService,
    private sanitizer: DomSanitizer,
    private reviewService: ReviewService,
  private router: Router) {}
    
  ngOnInit(): void {
    this.loadProductBySlug();
    this.startAuto();
  }

  viewDetail(slug : string){
      this.productService.updateViewProductBySlug(slug)
      .pipe(take(1))
      .subscribe({
      error: (e) => console.log('update view failed', e)
    });
      this.router.navigate(['/products', slug]);
    }
    
  loadReviews(){
    if(!this.product?.id) return;
    this.reviewService.getReviewByProductId(this.product.id).subscribe({
      next : res =>{
        this.reviewsData = res.data;
      },
      error : ()=>{
        
      }
    })
  }
  checkPermission() {
    if (this.authService.isLoggedIn()) {
       this.reviewService.checkCanRate(this.product?.id).subscribe(res => {
          this.canRate = res; 
       });
    }
  }
  submitReview() {
    const payload: Review = {
       productId: this.product?.id ||0,
       comment: this.userComment,
       rate: this.canRate ? this.userRating : 0 

    };

    this.reviewService.updateReviewByProductId(this.product?.id?.toString() || '', payload).subscribe(() => {
       this.notify.success('Cảm ơn bạn đã đánh giá sản phẩm!');
        this.userComment = '';
        this.userRating = 0;
        this.canRate = false;
       this.loadReviews();
    });
  }

  loadProductBySlug(){
    const slug = this.route.snapshot.paramMap.get('slug') ?? '';
    this.route.paramMap.pipe(
      switchMap(params => {
        const slug = params.get('slug') ?? '';
        return this.productService.getProductBySlug(slug);
      })
    ).subscribe(res => {
      this.product = res.data;
      this.galleryImages = this.buildGalleryImages(res.data);
      this.safeYoutubeUrl = this.getEmbedUrl(this.product?.youtubeUrl ?? '');
      this.loadProductByCategoryId();
      this.loadReviews();
      this.checkPermission();
    });
  }
  addProductToCart(){
    const user = this.authService.getCurrentUser();
    if(!user){
      this.notify.error("Bạn cần đăng nhập tài khoản");
      return;
    }
    const slug = this.route.snapshot.paramMap.get('slug') ?? '';
    if(!slug) return;
    this.cartService.addProductToCart(slug).subscribe({
      next: () => {
        this.cartService.refreshCartCount().subscribe();
        this.notify.success("Thêm vào giỏ hàng thành công");
      },
      error: (err) => {
        this.notify.error(err.error?.message || "Thêm thất bại");
      }
    });

  }
  
  getEmbedUrl(url: string): SafeResourceUrl | null {
    // Regex để lấy ID video từ các dạng link youtube khác nhau (ngắn, dài)
    const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/;
    const match = url.match(regExp);

    if (match && match[2].length === 11) {
      const videoId = match[2];
      // Tạo link embed
      const embedUrl = `https://www.youtube.com/embed/${videoId}`;
      // Báo cho Angular biết link này an toàn
      return this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
    }
    return null;
  }
  buyNow(){
    const user = this.authService.getCurrentUser();
    if(!user){
      this.notify.error("Bạn cần đăng nhập tài khoản");
      return;
    }
    this.currentUser = user;
    this.isOpenBuyNow = true;

  }
  loadProductByCategoryId() {
  if (!this.product) return;

  this.productService.getProductByCategoryId(this.product.categoryId).subscribe({
    next: (res) => {
      const list = res?.data ?? [];

      const currentId = this.product?.id; // nếu id của product bạn là field khác thì đổi lại

      const seen = new Set<number | string>();
      this.relatedProducts = list.filter((p: any) => {
        const id = p?.id; // nếu API trả productId / _id thì đổi lại ở đây
        if (id == null) return false;

        // ✅ bỏ sản phẩm đang xem (không bắt buộc)
        if (currentId != null && id === currentId) return false;

        // ✅ lọc trùng id
        if (seen.has(id)) return false;
        seen.add(id);
        return true;
      });
    },
    error: () => {}
  });
}

  ngOnDestroy(): void {
    this.stopAuto();
  }
  hasDiscount(): boolean {
    if(!this.product) return false;
    return !!this.product.discount && this.product.discount > 1;
  }

  calcNewPrice(price?: number | null, discount?: number | null) {
  const p = Number(price ?? 0);
  const d = Number(discount ?? 0);
  return Math.max(0, Math.round(p * (1 - d / 100)));
  }

  calcSaveAmount(price?: number | null, discount?: number | null) {
    const p = Number(price ?? 0);
    const d = Number(discount ?? 0);
    return Math.max(0, Math.round(p * (d / 100)));
  }

  calcDiscountPercent(price?: number | null, discount?: number | null) {
    return Math.max(0, Math.round(Number(discount ?? 0)));
  }


  // --------------------
  // Tabs
  // --------------------
  setTab(t: ProductTab) {
    this.tab = t;
  }

  // --------------------
  // Helpers
  // --------------------
  formatNumber(v?: number): string {
    return v != null ? v.toLocaleString() : '0';
  }

  

  getFileUrl(path?: string): string {
    if (!path) return 'https://placehold.co/1200x600';

    // backend đôi khi trả full URL
    if (path.startsWith('http')) return path;

    const base = this.fileBaseUrl.replace(/\/$/, '');
    const p = path.replace(/^\//, '');
    return `${base}/${p}`;
  }

  private buildGalleryImages(p: ProductViewerDetail): string[] {
      const imgs = (p.imageUrls ?? []).map(x => this.getFileUrl(x));
      if(p.thumbnail){
        imgs.unshift( this.getFileUrl( p.thumbnail));
      }
    // loại trùng
    return Array.from(new Set(imgs));
  }

  // --------------------
  // Gallery controls
  // --------------------
  startAuto() {
    this.stopAuto();
    if (this.galleryImages.length <= 1) return;

    this.timer = setInterval(() => {
      this.next(false);
    }, this.autoMs);
  }

  stopAuto() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  }

  next(restart = true) {
    if (!this.galleryImages.length) return;
    this.activeIndex = (this.activeIndex + 1) % this.galleryImages.length;
    if (restart) this.startAuto();
  }

  prev(restart = true) {
    if (!this.galleryImages.length) return;
    this.activeIndex =
      (this.activeIndex - 1 + this.galleryImages.length) % this.galleryImages.length;
    if (restart) this.startAuto();
  }

  go(i: number) {
    this.activeIndex = i;
    this.startAuto();
  }
}
