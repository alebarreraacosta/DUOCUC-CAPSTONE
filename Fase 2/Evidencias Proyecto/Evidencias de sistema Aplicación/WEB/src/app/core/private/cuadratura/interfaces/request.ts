export interface InventarioResponse{
    codigo: string;
    fecha:string;
    estado: string;
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