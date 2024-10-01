import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'convertRolString'
})
export class ConvertRolStringPipe implements PipeTransform {

  transform(value: number): string {
    return value == 1 ? 'Administrador':'Desconocido';
  }

}
