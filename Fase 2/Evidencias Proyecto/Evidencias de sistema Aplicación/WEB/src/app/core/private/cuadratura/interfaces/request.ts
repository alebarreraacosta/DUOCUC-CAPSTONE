
export interface ListadoInventario{
    exito:boolean;
    listadoInventario: InventarioResponse[];
}

export interface InventarioResponse{
    CodigoInventario: string;
    Fecha:string;
    Estado: string;
}

export interface DetalleInventarioResponse {
    codArticulo:string;
    descripcion:string;
    unidad:string;
    stockSAP:number;
    stockBodega:number;
    precioUnitario:number
    precioTotal:number
    isCuadrado:boolean
}