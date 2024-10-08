import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './core/public/login/login.component';
import { AuthGuardLogin } from './core/public/guard/auth-login.guard';

const routes: Routes = [
  {
    path: '',
    component: LoginComponent,
    canActivate:[AuthGuardLogin]
  },
  {
    path: 'private',
    loadChildren: () =>
      import('../app/core/private/private-routing.module').then(
        (m) => m.PrivateRoutingModule
      ),
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
