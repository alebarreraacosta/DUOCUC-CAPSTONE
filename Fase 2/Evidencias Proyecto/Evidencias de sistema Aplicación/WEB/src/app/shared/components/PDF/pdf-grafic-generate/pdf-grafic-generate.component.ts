import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { Chart, registerables } from 'chart.js';
import { transformValoresAPesos } from 'src/app/utils/utils-functions';

@Component({
  selector: 'app-pdf-generator',
  template: `
    <div #pdfContent style="display: none;">
      <canvas #chartBodega width="400" height="200"></canvas>
      <canvas #chartSap width="400" height="200"></canvas>
    </div>
    <ngx-extended-pdf-viewer *ngIf="pdfBase64" 
                             [src]="'data:application/pdf;base64,' + pdfBase64" 
                             useBrowserLocale="true" 
                             height="80vh"
                             [filenameForDownload]="reporteNombre"
                             [showToolbar]="true"
                            [showZoomButtons]="true"
                            [showDownloadButton]="true"
                            [showPrintButton]="true"
                            [showPagingButtons]="true"
                            [showOpenFileButton]="false"         
                            [showPropertiesButton]="false"       
                            [showSidebarButton]="false"          
                            [showFindButton]="false"             
                            [showPresentationModeButton]="false" 
                            [showSecondaryToolbarButton]="false" 
                            [showRotateButton]="false"           
                            [showScrollingButton]="false"        
                            [showSpreadButton]="false" 
                            [showHandToolButton]="false"
                            [showTextEditor]="false"             
                            [showStampEditor]="false"             
                            [showHighlightEditor]="false"         
                            
                            >
    </ngx-extended-pdf-viewer>
  `,
})
export class PdfGeneratorComponent {

  @Input() datosBodega!: any[];
  @Input() datosSap!: any[];
  @Input() titulo!: string;
  @Input() logoUrl!: string;
  
  @ViewChild('pdfContent', { static: false }) pdfContent!: ElementRef;
  @ViewChild('chartBodega', { static: false }) chartBodega!: ElementRef;
  @ViewChild('chartSap', { static: false }) chartSap!: ElementRef;
  pdfBase64: string | undefined;
  chartsGenerated = 0;
  reporteNombre: string = "reporteCIG";

  constructor() {
    Chart.register(...registerables);
  }

  ngAfterViewInit() {
    this.generateCharts();
  }

  generateCharts() {
    const datosBodegaTop5 = this.datosBodega.slice(0, 2);
    const datosSapTop5 = this.datosSap.slice(0, 2);

    // Gráfico de inventario Bodega
    new Chart(this.chartBodega.nativeElement, {
      type: 'bar',
      data: {
        labels: datosBodegaTop5.map(item => item.descripcion),
        datasets: [{
          label: 'Inventario Bodega',
          data: datosBodegaTop5.map(item => item.cantidad),
          backgroundColor: 'rgba(75, 192, 192, 0.5)',
        }],
      },
      options: {
        responsive: false,
        scales: {
          y: {
            title: {
              display: true,
              text: 'Cantidades'
            }
          },
          x: {
            title: {
              display: true,
              text: 'Productos'
            }
          }
        },
        animation: {
          onComplete: () => this.checkChartsCompletion()
        }
      }
    });

    // Gráfico de inventario SAP
    new Chart(this.chartSap.nativeElement, {
      type: 'bar',
      data: {
        labels: datosSapTop5.map(item => item.descripcion),
        datasets: [{
          label: 'Inventario SAP',
          data: datosSapTop5.map(item => item.cantidad),
          backgroundColor: 'rgba(153, 102, 255, 0.5)',
        }],
      },
      options: {
        responsive: false,
        scales: {
          y: {
            title: {
              display: true,
              text: 'Cantidades'
            }
          },
          x: {
            title: {
              display: true,
              text: 'Productos'
            }
          }
        },
        animation: {
          onComplete: () => this.checkChartsCompletion()
        }
      }
    });
  }

  checkChartsCompletion() {
    this.chartsGenerated += 1;
    if (this.chartsGenerated === 2) {
      this.captureCanvasAsImage();
    }
  }

  async captureCanvasAsImage() {
    const pdf = new jsPDF('p', 'mm', 'a4');

    // Agregar logo y título
    try {
      if (this.logoUrl) {
        const logo = await this.loadImage(this.logoUrl);
        pdf.addImage(logo, 'JPG', 10, 10, 20, 20);
      }
    } catch (error) {
      console.error('Error cargando la imagen del logo:', error);
    }

    pdf.setFontSize(16);
    pdf.text(this.titulo || 'Reporte de Inventario', 40, 20);

    // Capturar gráfico de Bodega y SAP en alta resolución
    const imgBodega = this.captureHighResImage(this.chartBodega.nativeElement);
    const imgSap = this.captureHighResImage(this.chartSap.nativeElement);

    // Añadir gráficos en una sola fila
    pdf.setFontSize(16);
    pdf.text("Articulos de mayor valor", 10, 50);
    pdf.addImage(imgBodega, 'PNG', 10, 60, 80, 60);
    pdf.addImage(imgSap, 'PNG', 110, 60, 80, 60);

    // Título y tabla de Inventario Bodega
    pdf.line(10, 122, 200, 122);

    pdf.setFontSize(16);
    pdf.text("Inventario Bodega gestionado", 10, 130);
    autoTable(pdf, {
      startY: 140,
      head: [['ID Producto', 'Código', 'Cantidad', 'Valor Unitario', 'Descripción']],
      body: this.datosBodega.map(p => [
        p.idProducto,
        p.codigoProducto,
        p.cantidad,
        transformValoresAPesos(p.valorUnitario),
        p.descripcion
      ]),
    });

    // Verificar si queda espacio en la página para el siguiente título y tabla
    const finalY = (pdf as any).lastAutoTable.finalY || 140;
    let startY = finalY + 20;
    if (startY + 60 > pdf.internal.pageSize.height) { 
      pdf.addPage();
      startY = 20;
    }

    // Título y tabla de Inventario SAP
    pdf.text("Inventario SAP gestionado", 10, startY);
    autoTable(pdf, {
      startY: startY + 10,
      head: [['ID Producto', 'Código', 'Cantidad', 'Valor Unitario', 'Descripción']],
      body: this.datosSap.map(p => [
        p.idProducto,
        p.codigoProducto,
        p.cantidad,
        transformValoresAPesos(p.valorUnitario),
        p.descripcion
      ]),
    });

    // Convertir a Base64
    this.pdfBase64 = pdf.output('datauristring').split(',')[1];
  }

  captureHighResImage(canvas: HTMLCanvasElement): string {
    const scale = 2;
    const tempCanvas = document.createElement('canvas');
    tempCanvas.width = canvas.width * scale;
    tempCanvas.height = canvas.height * scale;

    const ctx = tempCanvas.getContext('2d');
    if (ctx) {
      ctx.scale(scale, scale);
      ctx.drawImage(canvas, 0, 0);
    }

    return tempCanvas.toDataURL('image/png', 1.0);
  }

  generatePdf() {
    const link = document.createElement('a');
    link.href = 'data:application/pdf;base64,' + this.pdfBase64;
    link.download = this.reporteNombre + '.pdf';
    link.click();
  }

  openPdfInViewer() {
    this.pdfBase64 = 'data:application/pdf;base64,' + this.pdfBase64;
  }

  private loadImage(url: string): Promise<HTMLImageElement> {
    return new Promise((resolve, reject) => {
      const img = new Image();
      img.crossOrigin = 'Anonymous';
      img.onload = () => resolve(img);
      img.onerror = reject;
      img.src = url;
    });
  }
}
