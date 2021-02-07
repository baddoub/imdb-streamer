export interface StateNetGeneration {
  state: string;
  netGeneration: number;
  percentage: number;
}

export interface PowerPlant {
  name: string;
  longitude: number;
  latitude: number;
  netGeneration: number;
}
