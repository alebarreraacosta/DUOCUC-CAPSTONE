import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FormsReactiveService } from '../../service/forms-reactive.service';

@Component({
  selector: 'app-form-usuario',
  templateUrl: './form-usuario.component.html',
  styleUrls: ['./form-usuario.component.scss']
})
export class FormUsuarioComponent implements OnInit {
  
  @Input() form!: FormGroup;
  @Output() formSubmitted: EventEmitter<FormGroup> = new EventEmitter<FormGroup>();
  constructor(public fb :FormsReactiveService){}

  ngOnInit(): void {
    this.form = this.fb.crearFormularioUsuario();
  }


  onSubmit(): void {
    this.formSubmitted.emit(this.form);
  };

}
