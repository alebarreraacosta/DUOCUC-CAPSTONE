import { inject } from '@angular/core';
import {
  Router,
} from '@angular/router';
import { SessionGlobalService } from 'src/app/services/session-global.service';

export const AuthGuardLogin = () => {
  const sessionGlobal = inject(SessionGlobalService);
  const router = inject(Router);

  if(sessionGlobal.getDataUser()){
    router.navigate(['private/home']);
  }
  return true;
};
