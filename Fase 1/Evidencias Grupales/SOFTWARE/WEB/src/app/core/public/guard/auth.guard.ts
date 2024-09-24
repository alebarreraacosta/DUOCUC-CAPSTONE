import { inject } from '@angular/core';
import {
  Router,
} from '@angular/router';
import { SessionGlobalService } from 'src/app/services/session-global.service';

export const AuthGuard = () => {
  const sessionGlobal = inject(SessionGlobalService);
  const router = inject(Router);

  return sessionGlobal.getDataUser() ? true : router.navigate(['/login']);
};
