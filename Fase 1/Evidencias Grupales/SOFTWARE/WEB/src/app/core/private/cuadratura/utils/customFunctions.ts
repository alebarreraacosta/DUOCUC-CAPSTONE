import { MatPaginator } from '@angular/material/paginator';

export function localePaginator(paginator: MatPaginator | undefined) {
    if (paginator === undefined) {
      return;
    }
  
    paginator._intl.itemsPerPageLabel = 'Registros por página:';
    paginator._intl.firstPageLabel = 'Primera página';
    paginator._intl.lastPageLabel = 'Última página';
    paginator._intl.nextPageLabel = 'Siguiente';
    paginator._intl.previousPageLabel = 'Anterior';
    paginator._intl.getRangeLabel = (page, pageSize, length) => {
      let first = page * pageSize + 1;
      let last = (page + 1) * pageSize;
      if (last > length) {
        last = length;
      }
      if (first == last) {
        return first + ' de ' + length;
      }
      return first + ' - ' + last + ' de ' + length;
    };
  }