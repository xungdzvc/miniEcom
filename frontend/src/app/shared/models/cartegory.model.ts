export interface Category {
  id: number;
  name: string;
  parentId: number | null;
  createdAt?: string | Date | null;
  updatedAt?: string | Date | null;
}
