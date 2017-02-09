package autofinanceschedule.pojo;

import org.bson.Document;

import com.mongodb.client.model.UpdateOptions;

import autofinanceschedule.base.DBDATA;
import autofinanceschedule.base.MainDB;
import autofinanceschedule.func.Stock;

public class AddRZRQStock extends OperationAPIBase {

	@Override
	public String dealData(MainDB db, String datestamp) throws Exception {
		
		String base = "SH";
		
		String code = "";
		
		String name = "";
		
		int pagesize = 1;
		
		UpdateOptions option = new UpdateOptions();
		
		option.upsert(true);
		
		db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKRZRQ).updateOne(new Document("code", code), new Document()
				.append("$set", new Document()
						.append("code", code)
						.append("name", name)
						.append("base", base)), option);
		
		Stock.updateCurrentRZRQStock(db, code, name, base, pagesize);
		
		return "it is done";
		
	}

}
