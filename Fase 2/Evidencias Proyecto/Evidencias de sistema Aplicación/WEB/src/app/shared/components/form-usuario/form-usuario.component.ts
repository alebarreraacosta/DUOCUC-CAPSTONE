import {  Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FormsReactiveService } from '../../service/forms-reactive.service';

@Component({
  selector: 'app-form-usuario',
  templateUrl: './form-usuario.component.html',
  styleUrls: ['./form-usuario.component.scss']
})
export class FormUsuarioComponent implements OnInit, OnChanges {
  
  @Input() form!: FormGroup;
  @Input() isHabilitaActualizar:boolean=false;
  @Output() formSubmitted: EventEmitter<FormGroup> = new EventEmitter<FormGroup>();
  @Output() formUpdate: EventEmitter<FormGroup> = new EventEmitter<FormGroup>();
  @Output() cancelar: EventEmitter<void> = new EventEmitter<void>();
  constructor(public fb :FormsReactiveService){}

  ngOnInit(): void {}

  onSubmit(): void {
    this.formSubmitted.emit(this.form);
  };

  onUpdate(): void {
    this.formUpdate.emit(this.form);
  };

  cancel(){
    this.cancelar.emit();
  }

  ngOnChanges(changes: SimpleChanges){
    if(this.form){
      if (changes['isHabilitaActualizar']) {
        if (this.isHabilitaActualizar) {
          this.form.get('contrasena')?.disable()
        } else {
          this.form.get('contrasena')?.enable();
        }
      }

    }
  }

}
