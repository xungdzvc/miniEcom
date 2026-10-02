import { Routes } from "@angular/router";
import { UserAdminListComponent} from './pages/user-list/user-list.component';
import { UserEditComponent } from "./pages/user-edit/user-edit.component";
export const USER_ROUTES :Routes =[
    { path: '', component: UserAdminListComponent },
      { path: ':id', component: UserEditComponent }
]