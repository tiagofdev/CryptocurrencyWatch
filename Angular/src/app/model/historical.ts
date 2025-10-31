import Decimal from "decimal.js";


export class Historical {
  public name: string;
  public date: Date;
  public openPrice: Decimal;
  public closePrice: Decimal;
  public lowPrice: Decimal;
  public highPrice: Decimal;
  public volumeUsd24Hr: Decimal;
  public supply: Decimal


  constructor(name: string, date: any, openPrice: Decimal, closePrice: Decimal, lowPrice: Decimal, highPrice: Decimal, volumeUsd24Hr: Decimal, supply: Decimal) {
    this.name = name;
    this.openPrice = openPrice;
    this.closePrice = closePrice;
    this.lowPrice = lowPrice;
    this.highPrice = highPrice;
    this.volumeUsd24Hr = volumeUsd24Hr;
    this.supply = supply;
    const dateStr = date.toString();
    const dateParts = dateStr.split("-");
    const year = parseInt(dateParts[0], 10);
    const month = parseInt(dateParts[1], 10) - 1;
    // JavaScript's months are 0-based
    const day = parseInt(dateParts[2], 10);
    this.date = new Date(year, month, day);
  }
}
