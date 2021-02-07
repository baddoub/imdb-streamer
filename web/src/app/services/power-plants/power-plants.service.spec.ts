import { TestBed } from '@angular/core/testing';

import { PowerPlantsService } from './power-plants.service';

describe('PowerPlantsService', () => {
  let service: PowerPlantsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PowerPlantsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
