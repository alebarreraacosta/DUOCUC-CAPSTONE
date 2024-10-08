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
