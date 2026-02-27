import { Routes } from '@angular/router';
import { CategoryFormComponent } from './pages/category-form/category-form.component';
import { CategoryListComponent } from './pages/category-list/category-list.component';

export const CATEGORY_ROUTES: Routes = [
  { path: '', component: CategoryListComponent },
  { path: 'add', component: CategoryFormComponent },
  { path: 'edit/:id', component: CategoryFormComponent }
];
