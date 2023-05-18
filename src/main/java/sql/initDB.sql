INSERT INTO Currencies (Code, FullName, Sign) VALUES
('USD', 'US Dollar', '$'),
('EUR', 'Euro', '€'),
('RUB', 'Russian Ruble', '₽'),
('CNY', 'Yuan Renminbi', '元');



INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES
(1, 2, 0.92056),
(2, 1, 1.09),
(1, 3, 77.2),
(3, 1, 0.012953),
(4, 1, 0.14352),
(2, 3, 84.25),
(2, 4, 7.57);