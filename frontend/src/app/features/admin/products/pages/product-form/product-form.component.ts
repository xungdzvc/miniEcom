import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

import { CategoryService } from '../../../../../shared/data-access/category.service';
import { ProductAddOrUpdate } from '../../../../../shared/models/core/product/product-add-or-update.model';
import { Category } from '../../../../../shared/models/cartegory.model';

import { ProductAdminService } from '../../../../../shared/data-access/products/admin-product.service';
import { FormLayoutComponent } from '../../../shared/form/form-layout/form-layout.component';
import { FormSectionComponent } from '../../../shared/form/form-section/form-section.component';
import { FormFieldComponent } from '../../../shared/form/form-field/form-field.component';
import { FormActionsComponent } from '../../../shared/form/form-actions/form-actions.component';
import { ImageUploaderComponent } from '../../../shared/ui/image-uploader/image-uploader.component';
import { AdminToastService } from '../../../shared/services/admin-toast.service';
import { environment } from '../../../../../../../enviroments/environment';@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormLayoutComponent,
    FormSectionComponent,
    FormFieldComponent,
    FormActionsComponent,
    ImageUploaderComponent
  ],
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent {
  productForm!: FormGroup;
  categories: Category[] = [];

  mode: 'add' | 'edit' = 'add';
  productId: number | null = null;

  thumbnailFile: File | null = null;
  galleryFiles: File[] = [];

  thumbnailExisting: string[] = [];
  galleryExisting: string[] = [];

  readonly fileBaseUrl = environment.fileBaseUrl;

  constructor(
    private fb: FormBuilder,
    private productService: ProductAdminService,
    private categoryService: CategoryService,
    private route: ActivatedRoute,
    private router: Router,
    private toast: AdminToastService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadCategories();

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.mode = 'edit';
      this.productId = +idParam;
      this.loadProduct();
    }
  }

  private initForm(): void {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(5)]],
      categoryId: [null, Validators.required],
      description: ['', Validators.required],
      installTotoirial: [''],
      status: [true],
      price: [0, [Validators.required, Validators.min(0)]],
      quantity: [1, [Validators.required, Validators.min(1)]],
      discount: [0, [Validators.min(0), Validators.max(100)]],
      newPricewithDiscount: [0, [Validators.min(0)]],
      thumbnail: ['', Validators.required],
      downloadUrl: [''],
      youtubeUrl: [''],
      demoUrl: [''],
      imageUrls: [[]]
    });

    this.productForm.valueChanges.subscribe(() => this.recalc());
    this.recalc();
  }

  private recalc(): void {
    const price = Number(this.productForm.get('price')?.value ?? 0);
    const discount = Number(this.productForm.get('discount')?.value ?? 0);
    const newPrice = Math.max(price * (1 - discount / 100), 0);

    this.productForm.get('newPricewithDiscount')?.setValue(Math.round(newPrice), {
      emitEvent: false
    });
  }

  private loadProduct(): void {
    if (!this.productId) return;

    this.productService.getProductById(this.productId).subscribe({
      next: res => {
        const product = res.data as ProductAddOrUpdate;
        this.patchForm(product);

        this.thumbnailExisting = product.thumbnail ? [product.thumbnail] : [];
        this.galleryExisting = product.imageUrls ?? [];

        this.thumbnailFile = null;
        this.galleryFiles = [];
      },
      error: err => console.error('Lỗi tải sản phẩm:', err)
    });
  }

  private patchForm(product: ProductAddOrUpdate): void {
    this.productForm.patchValue({
      name: product.name,
      categoryId: product.categoryId,
      description: product.description ??'',
      installTotoirial: product.installTotoirial ?? '',
      status: product.status ?? true,
      price: product.price?? 0,
      quantity: product.quantity ?? 999,
      thumbnail: product.thumbnail ?? '',
      downloadUrl: product.downloadUrl ?? '',
      youtubeUrl: product.youtubeUrl ?? '',
      demoUrl: product.demoUrl ?? '',
      discount: product.discount ?? 0,
      imageUrls: product.imageUrls ?? []
    });
  }

  private loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: data => (this.categories = data),
      error: err => console.error('Lỗi tải danh mục:', err)
    });
  }

  async onSubmit(): Promise<void> {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    const formValue = this.productForm.value;

    try {
      const thumbnail = this.thumbnailFile
        ? await this.uploadToS3(this.thumbnailFile)
        : (this.thumbnailExisting[0] ?? '');

      const uploadedGallery: string[] = [];
      for (const file of this.galleryFiles) {
        uploadedGallery.push(await this.uploadToS3(file));
      }

      const imageUrls: string[] = [...this.galleryExisting, ...uploadedGallery];

      const payload = {
        ...formValue,
        thumbnail,
        imageUrls
      };

      if (this.mode === 'edit' && this.productId) {
        this.updateProduct(payload);
      } else {
        this.createProduct(payload);
      }
    } catch (err) {
      console.error('Upload lỗi:', err);
      this.toast.error('Upload ảnh thất bại. Hãy thử lại.');
    }
  }

  private createProduct(data: any): void {
    this.productService.addProduct(data).subscribe({
      next: () => {
        this.toast.success('Tạo sản phẩm thành công!');
        this.router.navigate(['/admin/products']);
      },
      error: err => {
        console.error('Lỗi tạo sản phẩm:', err);
        this.toast.error('Lỗi tạo sản phẩm. Hãy thử lại.');
      }
    });
  }

  private updateProduct(data: any): void {
    this.productService.updateProduct(this.productId!, data).subscribe({
      next: () => {
        this.toast.success('Cập nhật sản phẩm thành công!');
        this.router.navigate(['/admin/products']);
      },
      error: err => {
        console.error('Lỗi cập nhật sản phẩm:', err);
        this.toast.error('Lỗi cập nhật sản phẩm. Hãy thử lại.');
      }
    });
  }

  onThumbnailExistingChange(names: string[]): void {
    this.thumbnailExisting = names;
    if (!this.thumbnailFile) {
      this.productForm.patchValue({ thumbnail: names[0] ?? '' });
    }
  }

  onThumbnailFilesChange(files: File[]): void {
    this.thumbnailFile = files[0] ?? null;
    if (this.thumbnailFile) {
      this.productForm.patchValue({ thumbnail: this.thumbnailFile.name });
    } else {
      this.productForm.patchValue({ thumbnail: this.thumbnailExisting[0] ?? '' });
    }
  }

  onGalleryExistingChange(names: string[]): void {
    this.galleryExisting = names;
    this.productForm.patchValue({ imageUrls: names });
  }

  onGalleryFilesChange(files: File[]): void {
    this.galleryFiles = this.mergeUniqueFiles(this.galleryFiles, files);
  }

  private mergeUniqueFiles(current: File[], incoming: File[]): File[] {
    const key = (f: File) => `${f.name}__${f.size}__${f.lastModified}`;
    const map = new Map<string, File>();
    for (const f of current) map.set(key(f), f);
    for (const f of incoming) map.set(key(f), f);
    return Array.from(map.values());
  }

  private uploadToS3(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      this.productService.uploadS3(file).subscribe({
        next: res => resolve(res.url),
        error: err => reject(err)
      });
    });
  }
}
