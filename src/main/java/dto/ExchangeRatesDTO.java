package dto;

import entity.Currencies;

public class ExchangeRatesDTO {

    private Currencies baseCurrency;
    private Currencies targetCurrency;
    private double rate;
    private double amount;
    private double convertedAmount;

    public ExchangeRatesDTO(Currencies baseCurrency, Currencies targetCurrency, double rate, double amount, double convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

}
