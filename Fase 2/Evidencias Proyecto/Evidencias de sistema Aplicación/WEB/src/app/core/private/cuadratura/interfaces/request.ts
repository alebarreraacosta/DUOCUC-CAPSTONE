
export interface ListadoInventario{
    exito:boolean;
    listadoInventario: InventarioResponse[];
}

export interface InventarioResponse{
    CodigoInventario: string;
    Fecha:string;
    Estado: string;
}

export interface DetalleInventario{
    exito:boolean;
    datos:DetalleInventarioResponse[]
}


export interface DetalleInventarioResponse {
    CodArticulo:string;
    Descripcion:string;
    Unidad:string;
    StockSAP:number;
    StockBodega:number;
    PrecioUnitario:number
    PrecioTotal:number
    IsCuadrado:boolean
}

export interface VerProductoCuadrado{
  exito: boolean;
  datos: {
    codigoInventario: string;
    codigoProducto: string;
    cantidadACuadrar: number;
    descripcion: string;
    pdfBase64: string;
  }

}