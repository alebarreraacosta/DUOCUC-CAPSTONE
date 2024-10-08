import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CuadraturaComponent } from './cuadratura.component';

describe('CuadraturaComponent', () => {
  let component: CuadraturaComponent;
  let fixture: ComponentFixture<CuadraturaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CuadraturaComponent]
    });
    fixture = TestBed.createComponent(CuadraturaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
