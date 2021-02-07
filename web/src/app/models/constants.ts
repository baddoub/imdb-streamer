import * as d3 from 'd3';

export const api = 'http://localhost:9000';
export const WIDTH = 1000;
export const HEIGHT = 750;
export const DOMAIN = [0, 100000000, 200000000, 300000000, 400000000, 500000000, 600000000, 700000000, 800000000, 900000000];
export const COLORSCALE = d3.scaleThreshold()
  .domain(DOMAIN)
  .range(d3.schemeBlues[9] as Iterable<number>);
export const PROJECTION = d3.geoAlbersUsa()
  .scale(1300)
  .translate([WIDTH / 2, HEIGHT / 2]);
export const GEOPATH = d3.geoPath().projection(PROJECTION);

export const LEGENDLABELS = ['< 40000000KWH', '< 8000000KWH', '< 120000000KWH', '< 160000000KWH', '< 20000000KWH',
  '< 240000000KWH', '< 28000000KWH', '< 320000000KWH', '< 36000000KWH', '< 400000000KWH'];


export const LSW = 20;
export const LSH = 30;
export const LEGEND = HEIGHT - 300;

