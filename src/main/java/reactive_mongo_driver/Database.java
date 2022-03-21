package reactive_mongo_driver;

import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import com.mongodb.rx.client.Success;
import org.bson.Document;
import rx.Observable;

public class Database {
    private final MongoCollection<Document> users;
    private final MongoCollection<Document> products;

    public Database(String databaseName) {
        MongoDatabase database = MongoClients.create("mongodb://localhost:27017").getDatabase(databaseName);

        users = database.getCollection("users");

        products = database.getCollection("products");
    }

    public Observable<Boolean> addUser(User user) {
        String userName = user.getName();
        String walletType = user.getWalletType();

        Document userDocument = new Document("name", userName).append("walletType", walletType);

        return users.findOneAndDelete(Filters.eq("name", userName)).flatMap(x ->
            users.insertOne(userDocument).map(success -> true).singleOrDefault(false)
        );
    }

    public Observable<User> getUser(String userName) {
        Observable<Document> userDocument = users.find(Filters.eq("name", userName)).toObservable();

        return userDocument.map(User::new);
    }

    public Observable<Product> getProducts() {
        Observable<Document> productsDocument = products.find().toObservable();

        return productsDocument.map(Product::new);
    }

    public Observable<Boolean> addProduct(Product product) {
        if (product.getPriceInRubles() < 1e-9) {
            System.err.println("Price should be positive! Found " + product.getPriceInRubles());
            return Observable.just(false);
        }

        String productName = product.getName();

        Double productPriceInRubles = product.getPriceInRubles();

        Document userDocument = new Document("name", productName).append("priceInRubles", productPriceInRubles);

        Observable<Success> insertStatus = products.insertOne(userDocument);

        return insertStatus.map(success -> true).singleOrDefault(false);
    }
}
