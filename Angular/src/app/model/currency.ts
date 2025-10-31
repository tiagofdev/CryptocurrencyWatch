import Decimal from 'decimal.js';

export class Currency {
  name: string;
  symbol: string;
  explorer: string;
  icon: any;
  rank: number;
  constructor(name?: string, symbol?: string, explorer?: string, icon?: any, rank?: number) {
    this.name = "";
    if (name != undefined) {
      this.name = name;
    }
    this.symbol = "";
    if (symbol != undefined) {
      this.symbol = symbol;
    }
    this.explorer = "";
    if (explorer != undefined) {
      this.explorer = explorer;
    }
    this.icon = [];
    if (icon != undefined) {
      this.icon = icon;
    }
    this.rank = 1001;
    if (rank != undefined) {
      this.rank = rank;
    }

  }


}
