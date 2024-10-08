import { Component } from '@angular/core';
import { SpinnerService } from '../../service/spinner.service';

@Component({
  selector: 'app-spinner',
  template: `
    <ngx-spinner bdColor="rgba(0,0,0,0.8)" size="medium" color="#fff" type="ball-pulse-sync" [fullScreen]="true">
      <div class="spinner-text" *ngIf="spinnerService.getMessage()">
        {{ spinnerService.getMessage() }}
      </div>
    </ngx-spinner>
  `,
  styles: [`
    .spinner-text {
      color: white;
      font-size: 18px;

    }
  `]
})
export class SpinnerComponent {
  constructor(public spinnerService: SpinnerService) {}
}
