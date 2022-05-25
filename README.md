##Projekt PPJ

Cílem projektu je vytvořit aplikaci pro ukládání a zobrazování meteorologických dat, která jsou průběžně stahována z http://www.openweathermap.com

Požadavky na technické řešení:
1. Maven pro sestavení
2. Spring Boot 
3. Verzování přes Git

### Datový model(persistence)
- Stát
- Město
- Měření pro město
  - Expirace záznamů dle konfigurace(např. defaultně 14 dní)

Pro ukládání státu a města zvolte relační databázi. Pro ukládání měření zvolte vhodnou NoSQL databázi.

### API
Aplikace bude poskytovat MVC a REST API pro přímou komunikaci.
### REST
Aplikace bude obsahovat REST rozhraní pro přidávání, editaci a mazání států, měst a měření.A dále zobrazení aktuálních hodnot a průměru za poslední den, týden a 14 dní.
### Testování
Součástí řešení budou testy pro všechny operace volané přes REST API.
### Konfigurace
Musí být možno provádět externí konfiguraci –tj. veškerá konfigurace do properties souborů.
### Logování
Aplikace by měla využívat logovací systém Logback svýpisem do souboru (např. log.out). V případě chyby Vám bude zaslán pouze soubor log.out –výstup zkonzole pouze vpřípadě, že neprojdou testy.
### Sestavení
Výsledkem kompilace pomocí nástroje Maven musí být samostatně spustitelná webová aplikace –mimo IDE.
### CSV export a import
Měření pro jednotlivá města bude možné dávkově importovat a exportovat ve formátu CSV jako upload/download souboru.
### Data
Data je možné získávat zlibovolného veřejně dostupného API, například –sbezplatným přístupem při dodržení limitu 60 volání za sekundu.

### Další požadavky
1. Interval aktualizace dat by měl být konfigurovatelný (i sohledem na API limit)
2. Aplikaci by mělo být možné pustit vtzv. read-only modu, tj. lze provádět jen operace zobrazení a čtení dat a vypnutou aktualizací.
3. Konfigurovatelná expirace záznamů
