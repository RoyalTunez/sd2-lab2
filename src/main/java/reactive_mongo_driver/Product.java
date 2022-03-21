package reactive_mongo_driver;

import org.bson.Document;

import java.util.Locale;

public class Product {
    private final String name;
    private final Double priceInRubles;

    public Product(Document doc) {
        this(doc.getString("name"), doc.getDouble("priceInRubles"));
    }

    public String getName() {
        return name;
    }

    public Double getPriceInRubles() {
        return priceInRubles;
    }

    public Product(String name, Double costInRubles) {
        this.name = name;
        this.priceInRubles = costInRubles;
    }

    public String toString(String walletType) {
        StringBuilder productString = new StringBuilder("product: ");

        productString.append(name);

        productString.append(" price ");

        // рубль ночью упал почему - то(

        switch (walletType.toLowerCase(Locale.ROOT)) {
            case "usd": {
                productString.append(priceInRubles / 132.1).append(" usd");
                break;
            }
            case "eur": {
                productString.append(priceInRubles / 145.91).append(" eur");
                break;
            }
            default: {
                productString.append(priceInRubles).append(" rub");
                break;
            }
        }

        return productString.toString();
    }
}