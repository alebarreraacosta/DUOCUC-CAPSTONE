import { NgModule } from '@angular/core';
import { ConvertRolStringPipe } from './convert-rol-string.pipe';


@NgModule({
  declarations:[
    ConvertRolStringPipe
  ],
  exports: [
    ConvertRolStringPipe
  ]
})
export class PipeTransformsModule { }
