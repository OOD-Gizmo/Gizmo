package application;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DBConnection {

	public static void main(String[] args) {
		System.out.println("ABCD");
		try(MongoClient mongoClient = MongoClients.create("mongodb+srv://parichay:KrsvIYxf9CQyBlJM@cluster0.ttlnvn7.mongodb.net/?retryWrites=true&w=majority")) {
			MongoDatabase database = mongoClient.getDatabase("Gizmo");
			MongoCollection<Document> collection = database.getCollection("Users");
			Document doc = collection.find().first();
            if (doc != null) {
                System.out.println(doc.toJson());
            } else {
                System.out.println("No matching documents found.");
            }
		} catch(Exception e) {}

	}

}
