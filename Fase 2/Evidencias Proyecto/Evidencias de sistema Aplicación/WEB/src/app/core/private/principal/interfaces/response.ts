export interface MesesInventarios
{
    exito: boolean,
    mesesInventarios: SelectorMesAnnioInventarioResponse[]
}

export interface SelectorMesAnnioInventarioResponse{
    MesAnnio: string;
    Annio: string;
    Mes: string;
}


export interface SelectorMesAnnioResponse{
    mesAnnio:string;
    annio: string;
    mes: string;
}


export interface GraficoMesAnnioResponse{
    idProducto:number;
    codigoProducto:string;
    cantidad:number;
    descripcion: string;
}

export interface ArticulosPorMes{
    exito:boolean;
    articulos: SelectorProductoPorMesResponse[];
}
export interface SelectorProductoPorMesResponse{
    CodigoProducto:string;
    Descripcion:string;
}

export interface DatosGraficosPorMes{
    exito: boolean;
    datosGrafico: {
      DatosSap: DatoGrafico[],
      DatosFisico:DatoGrafico[]
    }
}

export interface DatoGrafico{
    IdProducto: number;
    CodigoProducto: string;
    Cantidad:  number;
    Descripcion: string;
    PrecioUnitario:  number;
    Total:  number;
}


export interface SelectorProductoResponse{
    codigoProducto:string;
    descripcion:string;
}

export interface GraficoProductoResponse{
    mes: string;
    precioUnitario: number;
    cantidad: number;
    total: number;
}


export interface GraficoProductoSeleccionado{
    exito:boolean;
    datosGraficoProducto: DatoGraficoProducto[];
}


export interface DatoGraficoProducto {
    Mes: string;
    PrecioUnitario: number;
    Cantidad:  number;
    Total:  number;
}