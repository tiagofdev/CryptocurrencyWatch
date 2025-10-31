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


@Component({
  selector: 'dashboard',
  templateUrl: './dashboard.component.html'

})
export class DashboardComponent {
  // format = '%d-%m-%y'
  FORMAT24H = '%H:%M';
  FORMAT1WM = '%d';
  FORMAT1Y = '%m';
  ONEHOUR = 60 * 60 * 1000;
  ONEDAY = 60 * 60 * 1000 * 24;
  ONEWEEK = 60 * 60 * 1000 * 24 * 7;
  ONEMONTH = 60 * 60 * 1000 * 24 * 30;
  ONEYEAR = 60 * 60 * 1000 * 24 * 365;
  searchText = "";

  gradient: string = 'linear-gradient(to bottom, #FFD700FF, #FFE581FF)';

  optionSelectedTab : string = "history";
  optionSelectedCurrency : string;
  optionSelectedGraph : string;
  optionSelectedRange : string;
  optionSelectedProjection : string;


  graph : any;
  start : any;
  end : any;

  currencies$!: Observable<AppState<Response>>;
  price$ : Observable<Response>;
  historical$ : Observable<Response>;
  alerts$ : Observable<Response>;

  currencySubscriber: Currency[];
  priceSubscriber: Price[] | undefined;
  historicalSubscriber: Historical[];
  alertsSubscriber: Alert[];

  filteredCurrency : Currency;
  filteredPrices : Price[];
  filteredHistorical : Historical[];
  filteredRangeSize : number;
  volume24h : string;
  supply : string;
  marketCap : string;
  average : string;
  percentage : string;
  currentPrice : number;

  sma : [];
  ema : [];
  lregression : [];

  username: string = "";
  priceValue = 0;
  subscribed : any;
  optionSelectedCriteria : string;
  optionSelectedOperator : string;
  alertMsg: string = "";

  @Input() controller: Controller;
  @Input() router: Router;

  /****************************************************************************************/
  /**
   * Constructor
   * @param controller
   * @param router
   * @param sharedData
   */
  constructor(controller: Controller, router: Router, private sharedData: SharedData) {
    this.controller = controller;
    this.router = router;
    this.price$ = this.controller.getPrices$();
    this.historical$ = this.controller.getHistorical$()
    this.alerts$ = new Observable<Response>();
    this.getCurrencies();

    this.currencySubscriber = [];
    this.priceSubscriber = [];
    this.historicalSubscriber = [];
    this.alertsSubscriber = [];
    this.filteredCurrency = new Currency();
    this.filteredPrices = [];
    this.filteredHistorical = [];
    this.filteredRangeSize = 1;
    this.optionSelectedCurrency = "";
    this.optionSelectedRange = "";
    this.optionSelectedGraph = "";
    this.optionSelectedProjection = "sma"
    this.optionSelectedCriteria = "";
    this.optionSelectedOperator = "";
    this.marketCap = "";
    this.volume24h = "";
    this.supply = "";
    this.average = "";
    this.percentage = "";
    this.currentPrice = 0;
    this.sma = [];
    this.ema = [];
    this.lregression = [];

  }

  /****************************************************************************************/
  /**
   * OnInit
   * Set up observable variables
   */
  ngOnInit(): void {


    this.optionSelectedCurrency = "Bitcoin";

    // this.price$.subscribe( response => { this.priceSubscriber = response.data.results});
    this.price$.subscribe(response => {
      // @ts-ignore
      this.priceSubscriber = response.data.results.map((obj: Price) =>
        new Price(new Date(obj.collectedAt), new Decimal(obj.priceUsd), obj.name)
      );
      // This function is called here, inside the subscription, because it waits for subscription to finish to then
      // execute the next line
      this.optionSelectedGraph = "curve";
      this.optionSelectedRange = "24h";
      this.updateDataSelectedCurrency();
      this.optionSelectedCriteria = "priceThreshold";
      this.optionSelectedOperator = "greaterThan";
    });

    this.historical$.subscribe(response => {
      // @ts-ignore
      this.historicalSubscriber = response.data.results.map((obj: Historical) =>
        new Historical(obj.name, obj.date, new Decimal(obj.openPrice), new Decimal(obj.closePrice), new Decimal(obj.lowPrice),
          new Decimal(obj.highPrice), new Decimal(obj.volumeUsd24Hr), new Decimal(obj.supply)));
    })


    this.currencies$.subscribe(state => {
      // @ts-ignore
      this.currencySubscriber = state.response?.data.results.map((obj: Currency) =>
        new Currency(obj.name, obj.symbol, obj.explorer, obj.icon, obj.rank));

    })

    this.sharedData.data$.subscribe((data) => {
      this.username = data;
      console.log("init username getting data: ", this.username);
      this.alerts$ = this.controller.getAlerts$(this.username);
      this.alerts$.subscribe(response => {
        let array = response.data.results
        array.forEach( ob => {
          console.log("res currenncy: ", ob.currency);
        })

        this.alertsSubscriber = response.data.results.map((obj: Alert) =>
          new Alert(obj.username, obj.currency, obj.alertCriteria, obj.operator, obj.threshold, obj.alertedToday))
      });

    })



    // Add more initialization code here

  }
  /****************************************************************************************/

  /**
   * currencies observable from the controller
   */

  getCurrencies(): Observable<AppState<Response>> {
    this.currencies$ = this.controller.getCurrencies$()
      .pipe(
        map(results => {
          return { dataState: DataState.LOADED_STATE, response: results }
        }),
        startWith({ dataState: DataState.LOADING_STATE }),
        catchError((err: string) => {
          return of({ dataState: DataState.ERROR_STATE, error: err })
        })
      );
    return this.currencies$;
  }


  /****************************************************************************************/
  // Search Text


  /*
  searchText$ = new BehaviorSubject<string>(''); // Manages the search input
  filteredCurrencies$!: Observable<AppState<Response>>;

  this.filteredCurrencies$ = combineLatest([this.currencies$, this.searchText$]).pipe(
    map(([state, searchText]) => {
      if (state.dataState !== DataState.LOADED_STATE || !state.response?.data?.results) {
        return state; // Return original state if not loaded
      }

      // Filter results based on search text
      const filteredResults = state.response.data.results.filter((option: any) =>
        option.name.toLowerCase().includes(searchText.toLowerCase())
      );

      return {
        ...state,
        response: {
          ...state.response,
          data: { ...state.response.data, results: filteredResults },
        },
      };
    })
  );

  onSearch(text: string) {
    this.searchText$.next(text); // Update the search text
  }
*/
  /****************************************************************************************/


  /****************************************************************************************/

  /**
   * Update selected currency and call showCurve() to update graph with the new selected currency
   */
  updateDataSelectedCurrency() : void {
    // First let's filter the data only for the selected currency
    // @ts-ignore
    this.filteredPrices = this.priceSubscriber.filter(currency =>
      currency.name === this.optionSelectedCurrency);
    this.filteredHistorical = this.historicalSubscriber.filter(currency =>
      currency.name === this.optionSelectedCurrency);
    this.filteredCurrency = this.currencySubscriber.filter( currency =>
      currency.name === this.optionSelectedCurrency)[0];
    // Update Graph
    this.updateGraph();
    const volume24h = this.filteredHistorical[this.filteredHistorical.length-1].volumeUsd24Hr.toNumber();
    const supply = this.filteredHistorical[this.filteredHistorical.length-1].supply.toNumber();
    const marketCap = this.filteredPrices[this.filteredPrices.length-1].priceUsd.toNumber() * supply;
    this.volume24h = this.shortenNumber(volume24h);
    this.supply = this.shortenNumber(supply);
    this.marketCap = this.shortenNumber(marketCap);
    this.currentPrice = this.filteredPrices[this.filteredPrices.length-1].priceUsd.toNumber();
    this.hasAlertForSelectedCurrency();
  }

  buildPriceGraph(difference: any, format: string, graph: string) : void {
    this.start = new Date(this.end - difference);
    let rangeData: Price[];
    if (difference <= this.ONEMONTH) {
      rangeData = this.filteredPrices.filter(price =>
        price.collectedAt >= this.start && price.collectedAt <= this.end);
    } else {
      let filteredRange = this.filteredHistorical.filter(hist =>
        hist.date >= this.start && hist.date <= this.end);
      // Converting Historical[] to Price[]
      rangeData = filteredRange.map(d => new Price(d.date, d.closePrice, ""));
    }

    // While I'm here, I'll also use rangeData to update the stats panel for Average and Change in that Date Range
    // Update Average
    const average = this.calculateAverage(rangeData.map((obj:Price) => obj.priceUsd.toNumber()));
    this.average = this.shortenNumber(Math.floor(average));
    // Update Change
    const lastPrice = rangeData[rangeData.length-1].priceUsd.toNumber();
    const firstPrice = rangeData[0].priceUsd.toNumber();
    const change = lastPrice - firstPrice;
    const percentageChange = (change / firstPrice * 100);
    this.percentage = percentageChange.toFixed(2);
    this.filteredRangeSize = rangeData.length;

    if (graph == "curve") {
      this.showCurve(rangeData, format);
    } else if (graph == "barplot") {
      this.showBarPlot(rangeData, format);
    }
  }

/**
   * Whenever buttons for graph types or range are clicked, they call this method to update the graph
   */
  updateGraph() : void {
  this.end = new Date();
    // If CURVE
    if (this.optionSelectedGraph == "curve") {
      // CURVE - 1H
      if (this.optionSelectedRange == "1h") {
        this.buildPriceGraph(this.ONEHOUR, this.FORMAT24H, "curve");
        // CURVE - 24H
      } else if (this.optionSelectedRange == "24h") {
        this.buildPriceGraph(this.ONEDAY,  this.FORMAT24H, "curve");
        // CURVE - 1 WEEK
      } else if (this.optionSelectedRange == "1w") {
        this.buildPriceGraph(this.ONEWEEK, this.FORMAT1WM, "curve");
        // CURVE - 1 MONTH
      } else if (this.optionSelectedRange == "1m") {
        this.buildPriceGraph(this.ONEMONTH, this.FORMAT1WM, "curve");
      } else {
        this.buildPriceGraph(this.ONEYEAR, this.FORMAT1Y, "curve")
      }
      // IF barplot
    }
    else if (this.optionSelectedGraph == "barplot") {
      // BARPLOT 1H
      if (this.optionSelectedRange == "1h") {
        this.buildPriceGraph(this.ONEHOUR, this.FORMAT24H, "barplot");
        // BARPLOT 24H
      } else if (this.optionSelectedRange == "24h") {
        this.buildPriceGraph(this.ONEDAY,  this.FORMAT24H, "barplot");
        // BARPLOT - 1 WEEK
      } else if (this.optionSelectedRange == "1w") {
        this.buildPriceGraph(this.ONEWEEK, this.FORMAT1WM, "barplot");
        // BARPLOT - 1 MONTH
      } else if (this.optionSelectedRange == "1m") {
        this.buildPriceGraph(this.ONEMONTH, this.FORMAT1WM, "barplot");
      } else {
        this.buildPriceGraph(this.ONEYEAR, this.FORMAT1Y, "barplot")
      }
    }
    else if (this.optionSelectedGraph == "candlestick") {
      this.start = new Date( this.end - this.ONEMONTH);
      let filteredList = this.filteredHistorical.filter(data =>
        data.date >= this.start && data.date <= this.end);
      this.showCandlestick(filteredList, this.FORMAT1WM);
    }

  }

  /**
   * This function gets the width of the graph containing DIV
   * @private
   */
  private getWidth(): number {
    return document.getElementById('graphDiv')?.clientWidth || 800;
  }


  /**
   * This function listens for the resize event on the window object.
   * Then, it updates the graph width
   * @param event
   */
  @HostListener('window:resize', ['$event']) onResize(event?: Event): void {
    this.updateGraph();
  }


  /**
   *
   * @param dataP
   * @param format
   */
  showCurve(filteredRange: Price[], format: string) : void {
    //load the graph with the selected Currency
    const data = filteredRange.map(d => ({ date: d.collectedAt, value: d.priceUsd.toNumber() }));

    const margin = { top: 20, right: 30, bottom: 30, left: 40 };
    const width = this.getWidth() - margin.left - margin.right;
    const height = 400 - margin.top - margin.bottom;

    // Select or create the SVG element
    this.graph = d3.select('svg')
      .attr('width', width + margin.left + margin.right)
      .attr('height', height + margin.top + margin.bottom);

    // Clear existing content before appending new elements
    this.graph.selectAll('*').remove();

    const chartGroup = this.graph.append('g')
      .attr('transform', `translate(${margin.left},${margin.top})`);

    // Check if data is non-empty and valid
    if (data.length > 0) {
      // Create X scale
      const xScale = d3.scaleTime()
        .domain(d3.extent(data, d => d.date) as [Date, Date]) // Ensure proper type for `extent`
        .range([0, width]);

      // Create Y scale
      const yScale = d3.scaleLinear()
        .domain([d3.min(data, d => d.value) as number, d3.max(data, d => d.value) as number]) // Adjust domain to min and max
        .nice() // Adjust domain to round to a nice number
        .range([height, 0]);

      // Create line generator
      const line = d3.line<{ date: Date; value: number }>()
        .x(d => xScale(d.date)!)
        .y(d => yScale(d.value)!)
        .curve(d3.curveMonotoneX);

      // Add X-axis
      const xAxis = d3.axisBottom(xScale).tickFormat(d3.timeFormat(format) as any);
      chartGroup.append('g')
        .attr('transform', `translate(0,${height})`)
        .call(xAxis);

      // Add Y-axis
      chartGroup.append('g')
        .call(d3.axisLeft(yScale));

      // Add the line path
      chartGroup.append('path')
        .datum(data)
        .attr('fill', 'none')
        .attr('stroke', 'steelblue')
        .attr('stroke-width', 1.5)
        .attr('d', line);
    } else {
      console.error('No data available to plot the graph.');
      this.graph = undefined;
    }
  }


  /**
   *
   * @param
   * @param format
   */
  showBarPlot(filteredRange: Price[], format: string): void {

    const data = filteredRange.map(d => ({ date: d.collectedAt, value: d.priceUsd.toNumber() }));

    const margin = { top: 20, right: 30, bottom: 30, left: 40 };
    const width = this.getWidth() - margin.left - margin.right;
    const height = 400 - margin.top - margin.bottom;

    const svg = d3.select('svg')
      .attr('width', width + margin.left + margin.right)
      .attr('height', height + margin.top + margin.bottom);

    svg.selectAll('*').remove(); // Clear existing content

    const chartGroup = svg.append('g')
      .attr('transform', `translate(${margin.left},${margin.top})`);

    if (data.length === 0) {
      console.error('No data available to plot the graph.');
      return;
    }

    // Aggregation logic
    let barWidth = width / data.length;
    let aggregatedData = [...data];

    while (barWidth < 28) {
      const tempData = [];
      for (let i = 0; i < aggregatedData.length; i += 3) {
        const subset = aggregatedData.slice(i, i + 3);
        const avgValue = d3.mean(subset, d => d.value) || 0;

        const midDate = subset[0].date;
        tempData.push({ date: midDate, value: avgValue });
      }
      aggregatedData = tempData;
      barWidth = width / aggregatedData.length;
    }

    // Scales with padding
    const xScale = d3.scaleBand<Date>()
      .domain(aggregatedData.map(d => d.date))
      .range([0, width])
      .padding(0.2); // Add padding between bars

    const yScale = d3.scaleLinear()
      .domain([d3.min(aggregatedData, d => d.value) as number, d3.max(aggregatedData, d => d.value) as number]) // Adjust domain to min and max

      .nice()
      .range([height, 0]);

    const yMax = d3.max(aggregatedData, d => d.value) as number;
    const yMin = d3.min(aggregatedData, d => d.value) as number;
    const yDiff = yMax - yMin;

    const colorScale = d3.scaleSequential(d3.interpolateViridis)
      .domain([yMin, yMax]);

    // Axes
    chartGroup.append('g')
      .attr('transform', `translate(0,${height})`)
      .call(d3.axisBottom(xScale).tickFormat(d3.timeFormat(format) as any));

    chartGroup.append('g')
      .call(d3.axisLeft(yScale));

    // Bars
    chartGroup.selectAll('.bar')
      .data(aggregatedData)
      .enter()
      .append('rect')
      .attr('class', 'bar')
      .attr('x', d => xScale(d.date)!)
      .attr('y', d => yScale(d.value)!)
      .attr('width', xScale.bandwidth())
      .attr('height', d => height - yScale(d.value))
      .attr('fill', d => colorScale(d.value));

    // Labels on top of bars
    chartGroup.selectAll('.label')
      .data(aggregatedData)
      .enter()
      .append('text')
      .attr('x', d => xScale(d.date)! + xScale.bandwidth() / 2)
      .attr('y', d => yScale(d.value)! - 5)
      .attr('text-anchor', 'middle')
      .attr('font-size', '10px')
      .text(d => yDiff < 1 ? d.value.toFixed(2) : d.value.toFixed(0));

    // Legend
    const legendHeight = 10;
    const legendWidth = 100;

    const legendGroup = svg.append('g')
      .attr('transform', `translate(${margin.left},${margin.top + height + 20})`);

    const gradient = svg.append('defs')
      .append('linearGradient')
      .attr('id', 'legendGradient')
      .attr('x1', '0%')
      .attr('x2', '100%')
      .attr('y1', '0%')
      .attr('y2', '0%');

    gradient.append('stop')
      .attr('offset', '0%')
      .attr('stop-color', colorScale(0));

    gradient.append('stop')
      .attr('offset', '100%')
      .attr('stop-color', colorScale(d3.max(aggregatedData, d => d.value) || 1));

    legendGroup.append('rect')
      .attr('width', legendWidth)
      .attr('height', legendHeight)
      .style('fill', 'url(#legendGradient)');

    legendGroup.append('text')
      .attr('x', 0)
      .attr('y', legendHeight + 15)
      .attr('font-size', '10px')
      .text('Low');

    legendGroup.append('text')
      .attr('x', legendWidth)
      .attr('y', legendHeight + 15)
      .attr('text-anchor', 'end')
      .attr('font-size', '10px')
      .text('High');
  }


  /**
   * CANDLESTICK
   * @param filteredData
   * @param format
   */
  showCandlestick(filteredData: Historical[], format: string): void {
    const data = filteredData.map(d => ({
      date: new Date(d.date),
      open: d.openPrice.toNumber(),
      close: d.closePrice.toNumber(),
      low: d.lowPrice.toNumber(),
      high: d.highPrice.toNumber()
    }));

    const margin = { top: 20, right: 30, bottom: 30, left: 50 };
    const width = this.getWidth() - margin.left - margin.right;
    const height = 400 - margin.top - margin.bottom;

    // Select or create the SVG element
    const svg = d3.select('svg')
      .attr('width', width + margin.left + margin.right)
      .attr('height', height + margin.top + margin.bottom);

    svg.selectAll('*').remove(); // Clear existing content

    const chartGroup = svg.append('g')
      .attr('transform', `translate(${margin.left},${margin.top})`);

    if (data.length === 0) {
      console.error('No data available to plot the graph.');
      return;
    }

    // X and Y scales
    const xScale = d3.scaleBand<Date>()
      .domain(data.map(d => d.date))
      .range([0, width])
      .padding(0.2); // Add padding between bars

    const yScale = d3.scaleLinear()
      .domain([d3.min(data, d => d.low)!, d3.max(data, d => d.high)!])
      .nice()
      .range([height, 0]);

    // Axes
    const xAxis = d3.axisBottom(xScale).tickFormat(d3.timeFormat(format) as any);
    const yAxis = d3.axisLeft(yScale);



    chartGroup.append('g')
      .attr('transform', `translate(0,${height})`)
      .call(xAxis);

    chartGroup.append('g')
      .call(yAxis);

    // Candlesticks
    chartGroup.selectAll('.candle')
      .data(data)
      .enter()
      .append('line')
      .attr('class', 'candle')
      .attr('x1', d => xScale(d.date)!)
      .attr('x2', d => xScale(d.date)!)
      .attr('y1', d => yScale(d.high))
      .attr('y2', d => yScale(d.low))
      .attr('stroke', 'black')
      .attr('stroke-width', 1);

    chartGroup.selectAll('.body')
      .data(data)
      .enter()
      .append('rect')
      .attr('class', 'body')
      .attr('x', d => xScale(d.date)!)
      .attr('y', d => yScale(Math.max(d.open, d.close)))
      .attr('width', xScale.bandwidth())
      .attr('height', d => Math.abs(yScale(d.open) - yScale(d.close)))
      .attr('fill', d => (d.close > d.open ? 'green' : 'red')); // Green for upward, red for downward

    // Legend
    const legendGroup = svg.append('g')
      .attr('transform', `translate(${margin.left},${margin.top + height + 20})`);

    legendGroup.append('rect')
      .attr('width', 20)
      .attr('height', 10)
      .attr('fill', 'green')
      .attr('x', 0)
      .attr('y', 0);

    legendGroup.append('text')
      .attr('x', 25)
      .attr('y', 10)
      .attr('font-size', '10px')
      .text('Close > Open (Increase)');

    legendGroup.append('rect')
      .attr('width', 20)
      .attr('height', 10)
      .attr('fill', 'red')
      .attr('x', 150)
      .attr('y', 0);

    legendGroup.append('text')
      .attr('x', 175)
      .attr('y', 10)
      .attr('font-size', '10px')
      .text('Close < Open (Decrease)');
  }

  showForecastSMA(sma: number[], ema: number[]): void {

    const margin={top:20,right:30,bottom:30,left:40};
    const width=this.getWidth()-margin.left-margin.right;
    const height=400-margin.top-margin.bottom;

    const svg=d3.select('svg#svgProj')
      .attr('width',width+margin.left+margin.right)
      .attr('height',height+margin.top+margin.bottom);

    svg.selectAll('*').remove();//Clearexistingcontent

    const g = svg.append('g')
      .attr('transform', `translate(${margin.left},${margin.top})`);

    // Create the X scale based on the length of SMA and EMA arrays
    const x = d3.scaleLinear()
      .domain([0, Math.max(sma.length, ema.length) - 1])
      .range([0, width]);

    // Create the Y scale using the minimum and maximum values from both arrays
    const y = d3.scaleLinear()
      .domain([
        d3.min([...sma, ...ema]) as number,
        d3.max([...sma, ...ema]) as number
      ])
      .nice()
      .range([height, 0]);

    // const yScale=d3.scaleLinear()
    //   .domain([d3.min(sma,d=>d.value)asnumber,d3.max(aggregatedData,d=>d.value)asnumber])//Adjustdomaintominandmax
    //
    //   .nice()
    //   .range([height,0]);


    // Define the line generator for the paths
    const line = d3.line<number>()
      .x((d, i) => x(i))
      .y(d => y(d));

    // Append the SMA line
    g.append('path')
      .datum(sma)
      .attr('fill', 'none')
      .attr('stroke', 'steelblue')
      .attr('stroke-width', 1.5)
      .attr('d', line);

    // Append the EMA line
    g.append('path')
      .datum(ema)
      .attr('fill', 'none')
      .attr('stroke', 'red')
      .attr('stroke-width', 1.5)
      .attr('d', line);

    // Add the X-axis
    g.append('g')
      .attr('class', 'axis axis--x')
      .attr('transform', `translate(0,${height})`)
      .call(d3.axisBottom(x));

    // Add the Y-axis
    g.append('g')
      .attr('class', 'axis axis--y')
      .call(d3.axisLeft(y));
  }



  shortenNumber(num: number): string {
    if (num >= 1_000_000 && num < 1_000_000_000) {
      return (num / 1_000_000).toFixed(2) + 'm';
    } else if (num >= 1_000_000_000 && num < 1_000_000_000_000) {
      return (num / 1_000_000_000).toFixed(2) + 'b';
    } else if (num >= 1_000_000_000_000) {
      return (num / 1_000_000_000_000).toFixed(2) + 't';
    } else {
      return num.toString();
    }
  }


  getProjections() {

    if (this.optionSelectedProjection == "sma") {
      const projections$ : Observable<Response> = this.controller.getProjectionsSMA$(this.optionSelectedCurrency);
      let projections : any[];

      projections$.subscribe(response => {
        projections = response.data.result;
        this.sma = projections[0];
        this.ema = projections[1];
        this.showForecastSMA(this.sma, this.ema);
      })

    } else {
      const projections$ : Observable<Response> = this.controller.getProjectionsLR$(this.optionSelectedCurrency);
      projections$.subscribe(response => {
        this.lregression = response.data.result;

        this.showForecastSMA(this.lregression, this.lregression);
      })
    }
  }

  calculateAverage(numbers: number[]): number {
    const sum = numbers.reduce((acc, num) => acc + num, 0);
    return (sum / numbers.length);
  }

  // private alertsSubject = new BehaviorSubject<string>(this.username);


  subscribeAlerts(form: NgForm) {
    let subscribe;
    if (form.value.subscribe) {
      subscribe = "1";
    } else {
      subscribe = "0";
    }

    let criteria = this.optionSelectedCriteria === "priceThreshold" ? "1" : "0";
    let operator = this.optionSelectedOperator === "greaterThan" ? "1" : "0";

    this.alerts$ = this.controller.saveAlerts$(this.username,
          this.optionSelectedCurrency,
          subscribe,
          criteria,
          operator,
          this.priceValue.toString());

    // If I don't subscribe, I will not get a response from the server
    this.alerts$.subscribe(response => {

      this.alertsSubscriber = response.data.results.map((obj: Alert) =>
        new Alert(obj.username, obj.currency, obj.alertCriteria, obj.operator, obj.threshold, obj.alertedToday));


    })

  }

  hasAlertForSelectedCurrency(): void {
    console.log("has alert? ");
    console.log("currency: ", this.optionSelectedCurrency);
    console.log("subscriber: ", this.alertsSubscriber);
    let alert = this.alertsSubscriber.find(a => a.currency === "    "+this.optionSelectedCurrency);
    if (alert != undefined) {
      let criteria = "";
      let operator = "";
      if (alert.alertCriteria) {
        criteria = "Price threshold";
      } else {
        criteria = "Percentage change";
      }
      if (alert.operator) {
        operator = "&gt";
      } else {
        operator = "&lt";
      }


      this.alertMsg = `You are currently subscribed to ${alert.currency}.<br>
        You will receive a notification to your email if:<br>
        ${criteria} is ${operator} than ${alert.threshold}`;

    } else {
      console.log("no alert");
      this.alertMsg = "";
    }

  }

  testEmail() {
    this.controller.test$();
  }
}
