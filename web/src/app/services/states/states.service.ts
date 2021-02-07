import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {StateNetGeneration} from '../../models/model';
import * as constants from '../../models/constants';

@Injectable({
  providedIn: 'root'
})
export class StatesService {

  constructor(private http: HttpClient) {
  }

  loadStatesWithTopNetGeneration(limit: number): Observable<StateNetGeneration[]> {
    return this.http.get<StateNetGeneration[]>(`${constants.api}/states/search?limit=${limit}`);
  }
}
