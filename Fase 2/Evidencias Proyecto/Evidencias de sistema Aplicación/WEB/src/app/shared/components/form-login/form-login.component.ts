import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FormsReactiveService } from '../../service/forms-reactive.service';

@Component({
  selector: 'app-form-login',
  templateUrl: './form-login.component.html',
  styleUrls: ['./form-login.component.scss']
})
export class FormLoginComponent implements OnInit {
  form!: FormGroup;

  @Output() formSubmitted: EventEmitter<FormGroup> = new EventEmitter<FormGroup>();
  constructor(public fb :FormsReactiveService){}

  ngOnInit(): void {
    this.form = this.fb.crearFormularioLogin();
  }


  onSubmit(): void {
    this.formSubmitted.emit(this.form);
  };

}
