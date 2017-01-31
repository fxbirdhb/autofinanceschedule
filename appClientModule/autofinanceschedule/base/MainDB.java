package autofinanceschedule.base;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MainDB {
	
	private MongoDatabase db;
	private MongoClient mongoClient;
	private MongoCollection<Document> updatecollection;

	//private static List<MongoCredential> mongoCredential;

	
	public MainDB() {
		
	}
	
	public MainDB(boolean WithAuthentication) {
		// TODO Auto-generated constructor stub
		

		//initiate the MongoDB	
		if (WithAuthentication) {
			InitWithCredential();
		} else {
			InitUnCredential();
		}

		setDb(DBDATA.SOURCEDB_DB_FINANCEFORCE);
		
	}

	/**
	 * Initiate the MongoDB with without credentials
	 */
	private void InitUnCredential() {
		
		mongoClient = new MongoClient(SecurityData.SOURCEDB_MAIN_IP, SecurityData.SOURCEDB_MAIN_PORT);
		
	}
	
	/**
	 * Initiate the MongoDB with credentials
	 * @param DBName
	 */
	private void InitWithCredential() {
		
		/*
		ServerAddress serverAddress = new ServerAddress(SecurityData.SOURCEDB_MAIN_IP, SecurityData.SOURCEDB_MAIN_PORT);
		//ServerAddress serverAddress = new ServerAddress("112.126.81.202", 30427);
		List<ServerAddress> listServer = new ArrayList<ServerAddress>();
		listServer.add(serverAddress);
		
		MongoCredential  mongoCredential = MongoCredential.createCredential(SecurityData.SOURCEDB_MAIN_OPERATOR, DBDATA.SOURCEDB_DB_WORD, SecurityData.SOURCEDB_MAIN_OPERATORPWD.toCharArray());
		//MongoCredential  mongoCredential = MongoCredential.createCredential("autoadmin", "autoassistant", "ftyjFGHJ_1489Fly".toCharArray());
		List<MongoCredential> listCredential = new ArrayList<MongoCredential>();
		listCredential.add(mongoCredential);
		
		mongoClient = new MongoClient(listServer, listCredential);
		*/
		
		mongoClient = new MongoClient(SecurityData.SOURCEDB_MAIN_IP, SecurityData.SOURCEDB_MAIN_PORT);
	}


	/**
	 * @return the db
	 */
	public  MongoDatabase getDb() {
		return db;
	}

	/**
	 * @param db the db to set
	 */
	public void setDb(String DBName) {
		db = mongoClient.getDatabase(DBName);
	}

	/**
	 * @return the updatecollection
	 */
	public MongoCollection<Document> getUpdatecollection() {
		return updatecollection;
	}

	/**
	 * @param updatecollection the updatecollection to set
	 */
	public void setUpdatecollection(MongoCollection<Document> updatecollection) {
		this.updatecollection = updatecollection;
	}
}
