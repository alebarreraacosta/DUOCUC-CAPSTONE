import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'transformarPesosPipe'
})
export class TransformarPesosPipe implements PipeTransform {

  transform(value: number): string {
    return value!=undefined || value ? "$"+value!.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".").trim() : "$0".trim();
  }

}
