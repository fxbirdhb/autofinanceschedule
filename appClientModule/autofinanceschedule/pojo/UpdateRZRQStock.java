package autofinanceschedule.pojo;

import java.util.Iterator;

import org.bson.Document;

import com.mongodb.client.FindIterable;

import autofinanceschedule.base.DBDATA;
import autofinanceschedule.base.MainDB;
import autofinanceschedule.func.Stock;

public class UpdateRZRQStock extends OperationAPIBase {

	@Override
	public String dealData(MainDB db, String datestamp) throws Exception {
		
		int pagesize = 1;
			
		//to deal all stock in the database
			
		FindIterable<Document> rs = db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKRZRQ).find();
			
		for (Iterator<Document> i = rs.iterator(); i.hasNext();) {
				
			Document item = i.next();
				
			String code = item.getString("code");
			
			String name = item.getString("name");
			
			String base = item.getString("base");
				
			Stock.updateCurrentRZRQStock(db, code, name, base, pagesize);
			
		}
			
		return "it is done";
	}

}
