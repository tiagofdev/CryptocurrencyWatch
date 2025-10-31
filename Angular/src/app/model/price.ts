import Decimal from 'decimal.js';

export class Price {
  public collectedAt: Date;
  public priceUsd: Decimal;
  public name: string;

  constructor(collectedAt: any, priceUsd: Decimal, name: string) {

    this.priceUsd = priceUsd;
    this.name = name;
    const dataStr = collectedAt.toString();
    const validStr = dataStr.slice(0, 23);
    this.collectedAt = new Date(validStr);

  }
}
