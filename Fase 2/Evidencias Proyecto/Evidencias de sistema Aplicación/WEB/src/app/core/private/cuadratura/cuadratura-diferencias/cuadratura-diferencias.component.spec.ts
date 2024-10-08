import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CuadraturaDiferenciasComponent } from './cuadratura-diferencias.component';

describe('CuadraturaDiferenciasComponent', () => {
  let component: CuadraturaDiferenciasComponent;
  let fixture: ComponentFixture<CuadraturaDiferenciasComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CuadraturaDiferenciasComponent]
    });
    fixture = TestBed.createComponent(CuadraturaDiferenciasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
