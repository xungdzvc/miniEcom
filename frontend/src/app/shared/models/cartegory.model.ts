export interface Category {
  id: number;
  name: string;
  quantity?: number;
  parentId?: number | null;
  parentName?: string | null;
  createdAt?: string | Date | null;
  updatedAt?: string | Date | null;
}
