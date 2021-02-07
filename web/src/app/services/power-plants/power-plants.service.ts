import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {PowerPlant} from '../../models/model';
import {Observable} from 'rxjs';
import * as constants from '../../models/constants';

@Injectable({
  providedIn: 'root'
})
export class PowerPlantsService {

  constructor(private httpClient: HttpClient) {
  }


  loadTopPowerPlants(limit: number): Observable<PowerPlant[]> {
    return this.httpClient.get<PowerPlant[]>(`${constants.api}/power-plants/search?limit=${limit}`);
  }
}
