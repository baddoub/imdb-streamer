import {Component, Input, OnInit} from '@angular/core';
import {Feature, FeatureCollection} from 'geojson';
import * as _ from 'lodash';
import {from, zip} from 'rxjs';
import * as constants from './models/constants';
import * as d3 from 'd3';
import {StatesService} from './services/states/states.service';
import {PowerPlantsService} from './services/power-plants/power-plants.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit {
  title = 'g42-test';

  @Input() limitStates!: number;
  @Input() limitPP!: number;

  constructor(private statesService: StatesService, private powerPlantsService: PowerPlantsService) {
  }

  ngOnInit(): void {
    this.initMap();
  }

  displayPowerPlants(limit: number): void {
    this.powerPlantsService.loadTopPowerPlants(limit)
      .subscribe(data => {
          const svg = d3.select('svg');
          const tooltip = d3.select('#tooltip-pp');
          svg.selectAll('circle').remove()
          svg.selectAll('.shapes')
            .data(data)
            .enter()
            .append(() => {
              return document.createElementNS('http://www.w3.org/2000/svg', 'circle');
            })

          svg.selectAll('circle')
            .data(data)
            .attr('class', 'circle')
            .attr('cx', (d) => {
              const projection = constants.PROJECTION([d.longitude, d.latitude]);
              return projection ? projection[0] : null;
            })
            .attr('cy', (d) => {
              const projection = constants.PROJECTION([d.longitude, d.latitude]);
              return projection ? projection[1] : null;
            })
            .attr('r', '12')
            .on('mouseover', (e, d) => {
              tooltip.transition()
                .duration(200)
                .style('opacity', .9);
              tooltip.html(d.name)
                .style('left', (e.pageX) + 'px')
                .style('top', (e.pageY - 28) + 'px');
            })
            .on('mouseout', (d) => {
              tooltip.transition()
                .duration(500)
                .style('opacity', 0);
            });
        },
        error => console.log(`error ${JSON.parse(error)}`)); // TODO better error handling
  }

  displayStates(limit: number): void {
    zip(from(d3.json<FeatureCollection>('/assets/us-states.json')),
      this.statesService.loadStatesWithTopNetGeneration(limit))
      .subscribe(([topology, data]) => {
          const mergedData = _(topology?.features)
            .keyBy('properties.name')
            .merge(_.keyBy(data, 'state'))
            .values()
            .value();
          const svg = d3.select('svg');
          const tooltip = d3.select('#tooltip-states');
          svg.selectAll('path')
            .data(mergedData)
            .classed('state', false)
            .attr('fill', (d) => {
              return d.percentage ? constants.COLORSCALE(d.netGeneration) : 'rgb(190, 190, 190)';
            })
            .on('click', (e, d) => {
              if (d.percentage) {
                tooltip.style('hidden', false)
                  .style('top', (e.pageY) + 'px')
                  .style('left', (e.pageX + 10) + 'px')
                  .html(`${d.state} ${d.netGeneration.toLocaleString('en')}KWH ${d.percentage}%`);
              }
            });

          svg.selectAll('text')
            .data(mergedData)
            .enter()
            .append('svg:text')
            .text((d) => {
              return `${d.properties?.name}`;
            })
            .attr('x', (d) => {
              return constants.GEOPATH.centroid(d)[0];
            })
            .attr('y', (d: Feature) => {
              return constants.GEOPATH.centroid(d)[1];
            })
            .attr('text-anchor', 'middle')
            .attr('font-size', '6pt');
        },
        error => console.log(`error ${JSON.parse(error)}`));
  }

  private initMap(): void {
    from(d3.json<FeatureCollection>('/assets/us-states.json')).subscribe(topology => {
      const svg = d3.select('#svg-cont')
        .append('svg')
        .attr('width', constants.WIDTH)
        .attr('height', constants.HEIGHT);

      svg.selectAll('path')
        .data(topology?.features as Array<Feature>)
        .enter()
        .append('path')
        .attr('d', constants.GEOPATH)
        .attr('stroke', '#ffffff')
        .attr('class', 'state');

      const svgL = d3.select('#legend-cont')
        .append('svg')
        .attr(
          'width', 200)
        .attr(
          'height', 600);
      const l = svgL.selectAll('g.legend')
        .data(constants.DOMAIN)
        .enter().append('g')
        .attr('class', 'legend');

      l.append('rect')
        .attr('x', 20)
        .attr('y', (d, i) => {
          return constants.LEGEND - (i * constants.LSH) - 2 * constants.LSH;
        })
        .attr('width', constants.LSW)
        .attr('height', constants.LSH)
        .style('fill', (d, i) => {
          return constants.COLORSCALE(d);
        })
        .style('opacity', 0.8);

      l.append('text')
        .attr('x', 50)
        .attr('y', (d, i) => {
          return constants.LEGEND - (i * constants.LSH) - constants.LSH - 4;
        })
        .text((d, i) => {
          return constants.LEGENDLABELS[i];
        });
    });
  }
}

