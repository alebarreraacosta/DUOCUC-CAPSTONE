export function transformValoresAPesos(value:number|null){
    return value!=0 ? "$"+value!.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".").trim() : "$0".trim();
}


