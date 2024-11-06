import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CuadrarProductoModalComponent } from './cuadrar-producto-modal.component';

describe('CuadrarProductoModalComponent', () => {
  let component: CuadrarProductoModalComponent;
  let fixture: ComponentFixture<CuadrarProductoModalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CuadrarProductoModalComponent]
    });
    fixture = TestBed.createComponent(CuadrarProductoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
