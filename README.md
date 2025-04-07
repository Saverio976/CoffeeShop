# COFFEESHOP

You can compile the prject or use the compiled [CoffeeShop-1.0.jar](./CoffeeShop-1.0.jar) file provided.

We are using [maven](https://maven.apache.org/install.html).

## Compile

```
mvn package
```

## Usage

```bash
java -jar ./target/CoffeeShop-1.0-SNAPSHOT.jar
# or, if you want to use the provided jar file:
java -jar ./CoffeeShop-1.0.jar
```

## Tests

```
mvn test
```

## Config files

### Menu Items

[menu.csv](./src/main/resources/data/menu.csv)

This file is added in the jar file. If you modify it, you need to compile the project again.

### Orders

[orders.csv](./src/main/resources/data/orders.csv)

This file is added in the jar file. If you modify it, you need to compile the project again.

## Output files

### Log

[coffee_shop_log.txt](./data/coffee_shop_log.txt)

### Coffee Shop Report

[coffee_shop_report.txt](./data/coffee_shop_report.txt)
