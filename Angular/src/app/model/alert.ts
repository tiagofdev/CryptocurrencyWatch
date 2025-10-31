
export class Alert {

  username : string;
  currency: string;
  alertCriteria: boolean;
  operator: boolean;
  threshold: number;
  alertedToday: boolean;


  constructor(username: string, currency: string, alertCriteria: boolean, operator: boolean, threshold: number, alertedToday: boolean) {
    this.username = username;
    this.currency = currency;
    this.alertCriteria = alertCriteria;
    this.operator = operator;
    this.threshold = threshold;
    this.alertedToday = alertedToday;
  }
}
