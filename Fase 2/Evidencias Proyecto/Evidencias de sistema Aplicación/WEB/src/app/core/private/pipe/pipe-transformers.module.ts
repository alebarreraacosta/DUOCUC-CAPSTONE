import { NgModule } from '@angular/core';
import { ConvertRolStringPipe } from './convert-rol-string.pipe';
import { TransformarPesosPipe } from './transformar-pesos.pipe';


@NgModule({
  declarations:[
    ConvertRolStringPipe,
    TransformarPesosPipe
  ],
  exports: [
    ConvertRolStringPipe,
    TransformarPesosPipe
  ]
})
export class PipeTransformsModule { }
