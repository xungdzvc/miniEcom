import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, OnDestroy } from '@angular/core';

type NewImageItem = { file: File; previewUrl: string };

@Component({
  selector: 'app-image-uploader',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './image-uploader.component.html',
  styleUrls: ['./image-uploader.component.css']
})
export class ImageUploaderComponent implements OnDestroy {
  @Input() label = 'Ảnh';
  @Input() required = false;
  @Input() multiple = false;
  @Input() accept = 'image/*';
  @Input() helper = '';
  @Input() existing: string[] = [];
  @Input() baseUrl = '';

  @Output() existingChange = new EventEmitter<string[]>();
  @Output() filesChange = new EventEmitter<File[]>();

  newItems: NewImageItem[] = [];
  isDragging = false;

  toPreviewUrl(value: string): string {
    if (!value) return '';
    if (/^https?:\/\//i.test(value) || value.startsWith('data:')) return value;
    return `${this.baseUrl}${value}`;
  }

  onDragOver(e: DragEvent): void {
    e.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(e: DragEvent): void {
    e.preventDefault();
    this.isDragging = false;
  }

  onDrop(e: DragEvent): void {
    e.preventDefault();
    this.isDragging = false;

    const files = Array.from(e.dataTransfer?.files ?? []);
    this.addFiles(files);
  }

  onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const files = input.files ? Array.from(input.files) : [];
    if (!files.length) return;

    this.addFiles(files);
    input.value = '';
  }

  private addFiles(files: File[]): void {
    const images = files.filter(f => this.isAcceptedImage(f));
    if (!images.length) return;

    if (!this.multiple) {
      this.clearNewItems(false);
      this.existing = [];
      this.existingChange.emit(this.existing);
    }

    const incoming = this.multiple ? images : images.slice(0, 1);

    const merged = this.mergeUniqueFiles(this.newItems.map(i => i.file), incoming);
    const map = new Map<string, NewImageItem>();

    for (const it of this.newItems) {
      map.set(this.fileKey(it.file), it);
    }

    for (const file of merged) {
      const k = this.fileKey(file);
      if (!map.has(k)) {
        map.set(k, { file, previewUrl: URL.createObjectURL(file) });
      }
    }

    this.newItems = Array.from(map.values());
    this.filesChange.emit(this.newItems.map(i => i.file));
  }

  private isAcceptedImage(file: File): boolean {
    if (file.type?.startsWith('image/')) return true;
    if (!file.name) return false;
    return /\.(png|jpe?g|gif|webp|bmp|svg)$/i.test(file.name);
  }

  private fileKey(f: File): string {
    return `${f.name}__${f.size}__${f.lastModified}`;
  }

  private mergeUniqueFiles(current: File[], incoming: File[]): File[] {
    const m = new Map<string, File>();
    for (const f of current) m.set(this.fileKey(f), f);
    for (const f of incoming) m.set(this.fileKey(f), f);
    return Array.from(m.values());
  }

  removeExisting(index: number): void {
    const next = [...(this.existing ?? [])];
    next.splice(index, 1);
    this.existing = next;
    this.existingChange.emit(this.existing);

    if (!this.multiple && this.newItems.length === 0) {
      this.filesChange.emit([]);
    }
  }

  removeNew(index: number): void {
    const item = this.newItems[index];
    if (item?.previewUrl?.startsWith('blob:')) {
      URL.revokeObjectURL(item.previewUrl);
    }

    const next = [...this.newItems];
    next.splice(index, 1);
    this.newItems = next;

    this.filesChange.emit(this.newItems.map(i => i.file));
  }

  private clearNewItems(emit = true): void {
    for (const it of this.newItems) {
      if (it.previewUrl?.startsWith('blob:')) {
        URL.revokeObjectURL(it.previewUrl);
      }
    }
    this.newItems = [];
    if (emit) this.filesChange.emit([]);
  }

  ngOnDestroy(): void {
    this.clearNewItems(false);
  }
}
