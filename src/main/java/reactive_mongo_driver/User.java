package reactive_mongo_driver;

import org.bson.Document;

public class User {
    private final String name;
    private final String walletType;

    public User(Document doc) {
        this(doc.getString("name"), doc.getString("walletType"));
    }

    public User(String name, String walletType) {
        this.name = name;
        this.walletType = walletType;
    }

    public String getName() {
        return name;
    }

    public String getWalletType() {
        return walletType;
    }
}