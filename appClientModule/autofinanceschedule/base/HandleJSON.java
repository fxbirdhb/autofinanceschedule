package autofinanceschedule.base;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.bson.Document;

import com.mongodb.client.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class HandleJSON {

	public HandleJSON() {
		// TODO Auto-generated constructor stub
	}
	
	public static String GetJsonData(Object jsondata){
		
		String strJsonData = null;
		
		JSONObject jsonObject = JSONObject.fromObject(jsondata);
		strJsonData = jsonObject.toString();

		return strJsonData;
	}
	
	public static String GetJsonArray(Object jsondata) {
		
		String strJsonData = null;
		
		JSONArray array = JSONArray.fromObject(jsondata);
		
		strJsonData = array.toString();
		
		return strJsonData;
	}
	
	
	/**
	 *change the data ResultSet into JSON
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static String ResultSetToJson(ResultSet rs) throws SQLException {
		
		JSONArray array = new JSONArray();
		
		ResultSetMetaData metaData = rs.getMetaData();
		
		int columnCount = metaData.getColumnCount();
		
		while (rs.next()) {
			
			JSONObject jsonObj = new JSONObject();
			
			//create the jsonObject including value and type
			
			for ( int i = 1; i <= columnCount; i ++) {
				
				String columnName = metaData.getColumnLabel(i);
				
				String value = rs.getString(columnName);
				
				jsonObj.put(columnName, value);

			}
			
			array.add(jsonObj);
		}
		
		return array.toString();
	}
	
	/**
	 * change the document collection into a JSON array
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static String TDocumentToJson(FindIterable<Document> rs) throws SQLException {
		
		JSONArray array = new JSONArray();
		
		for (MongoCursor<Document> valuei = rs.iterator(); valuei.hasNext();) {
			
			Document value = valuei.next();
			
			array.add(value.toJson());
		}
		
		return array.toString();
	}
}
