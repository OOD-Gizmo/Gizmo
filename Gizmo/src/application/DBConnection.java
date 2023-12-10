package application;

import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;

public final class DBConnection {
	
	private static MongoClient mongoClient;
	private static MongoDatabase database;
	private static boolean isConnected = false;
	
	public static void initClient() {
		try {
			if(!isConnected) {
				mongoClient = MongoClients.create("mongodb+srv://parichay:KrsvIYxf9CQyBlJM@cluster0.ttlnvn7.mongodb.net/?retryWrites=true&w=majority");
				database = mongoClient.getDatabase("Gizmo");
				isConnected = true;
				System.out.println("Mongo Client connected!");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static void closeConnection() {
		if(isConnected) {
			mongoClient.close();
		}
	}
	
	public static boolean isConnected() {
		return isConnected;
	}
	
	public static MongoClient getClient() {
		return mongoClient;
	}
	
	public static MongoCollection<Document> getCollection(String collectionName) {
		return database.getCollection(collectionName);
	}
}
