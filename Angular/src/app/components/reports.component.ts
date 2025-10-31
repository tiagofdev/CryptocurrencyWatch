import {Component, HostListener, Input} from "@angular/core";
import {Controller} from "../controller/controller";
import {Router} from "@angular/router";
import {DataState} from "../interface/data-state.enum";
import {BehaviorSubject, combineLatest, map, Observable, of, startWith, Subscription} from "rxjs";
import {AppState} from "../interface/app-state";
import {catchError} from "rxjs/operators";
import {Response} from "../interface/response";
import {Price} from "../model/price";
import * as d3 from "d3";
import Decimal from "decimal.js";
import {difference, stackOrderInsideOut} from "d3";
import {Historical} from "../model/historical";
import {Currency} from "../model/currency";
import {NgForm} from "@angular/forms";
import {SharedData} from "../interface/sharedData";
import {Alert} from "../model/alert";
import { FormsModule } from '@angular/forms';
import {HttpClient} from "@angular/common/http";


@Component({
  selector: 'reports',
  templateUrl: './reports.component.html'

})
export class ReportsComponent {


  constructor(private controller: Controller, private router: Router,
              private sharedData: SharedData, private http: HttpClient) {  }



}
