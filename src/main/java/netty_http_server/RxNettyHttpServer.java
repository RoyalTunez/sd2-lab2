package netty_http_server;

import io.reactivex.netty.protocol.http.server.HttpServer;
import reactive_mongo_driver.Database;
import reactive_mongo_driver.Product;
import reactive_mongo_driver.User;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

public class RxNettyHttpServer {
    public static void main(final String[] args) {
        Database database = new Database("sd2-lab2-db");

        HttpServer.newServer(8080).start((req, resp) -> {

            List<String> parsedRequest = parseRequest(req.getDecodedPath());

            if (parsedRequest.isEmpty()) {
                return resp.writeString(Observable.just(
                                "Available: products/username\n" +
                                "           registration/username/wallettype\n" +
                                "           addproduct/productname/priceinrubles"));
            }

            Observable<String> response;

            String requestStart = parsedRequest.get(0);

            switch (requestStart) {
                case "products": {
                    if (parsedRequest.size() != 2) {
                        return resp.writeString(Observable.just("Should be only products/username"));
                    }

                    String userName = parsedRequest.get(1);

                    response = database.getUser(userName).singleOrDefault(new User("DefaultUser", "rub")).map(User::getWalletType).flatMap(walletType -> database.getProducts().map(product -> product.toString(walletType)));
                    break;
                }

                case "registration": {
                    if (parsedRequest.size() != 3) {
                        return resp.writeString(Observable.just("Should be only registration/username/wallettype"));
                    }

                    String name = parsedRequest.get(1);
                    String walletType = parsedRequest.get(2);

                    response = database.addUser(new User(name, walletType)).map(userRegistered -> {
                        if (userRegistered) {
                            return "User registered";
                        } else {
                            return "Something wrong. Can't register user";
                        }
                    });
                    break;
                }

                case "addproduct": {
                    if (parsedRequest.size() != 3) {
                        return resp.writeString(Observable.just("Should be only addproduct/productname/priceinrubles"));
                    }

                    String name = parsedRequest.get(1);
                    Double priceInRubles;

                    try {
                        priceInRubles = Double.valueOf(parsedRequest.get(2));
                    } catch (NumberFormatException e) {
                        return resp.writeString(Observable.just("Product price should be number"));
                    }

                    response = database.addProduct(new Product(name, priceInRubles)).map(productAdded -> {
                        if (productAdded) {
                            return "Product added";
                        } else {
                            return "Something wrong. Can't add product";
                        }
                    });
                    break;
                }

                default: {
                    response = Observable.just("Something goes wrong!");
                }
            }

            return resp.writeString(response);
        }).awaitShutdown();
    }

    private static List<String> parseRequest(String request) {
        List<String> parsed = new ArrayList<>();

        var requestSplitBySlash = request.split("/");

        for (String requestPart: requestSplitBySlash) {
            if (requestPart.isBlank()) {
                continue;
            }

            parsed.add(requestPart);
        }

        return parsed;
    }
}