package autofinanceschedule.base;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;

import org.bson.Document;

import autofinanceschedule.func.Stock;
import autofinanceschedule.log.SimpleLog;
import autofinanceschedule.log.logbase;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		try {
			
			LocalDate ldate = LocalDate.now();
			
			//as sunday and monday not to update the data
			
			if (ldate.getDayOfWeek() == DayOfWeek.SUNDAY || ldate.getDayOfWeek() == DayOfWeek.MONDAY) {
				
				return;
				
			}
			MainDB db = new MainDB(true);
			
			logbase log = new SimpleLog(ConfigConstant.OPERATIONLOG_PHYADDRESS);
			
			log.InsertLog("\n---------------update at " + (new Date()).toString() + "----------------------\n");
			
			log.InsertLog("\n------update all stocks price---------\n");
			
			Document doc = ConfigFile.getConfig();
			
			String xqcookiename = "";
			
			String xqcookietoken = "";
			
			if (doc != null) {
				
				xqcookiename = doc.getString("xqcookiename");
				
				xqcookietoken = doc.getString("xqcookietoken");
				
			}

			Stock.xqcookiename = xqcookiename;
			
			Stock.xqcookietoken = xqcookietoken;
			
			//update the trade price 
			
			Stock.updateCurrentPrice(db, "2017", log);
			
			//update the total rzrq
			
			log.InsertLog("\n------update rzrq ---------\n");
			
			Stock.updateCurrentRZRQ(db, 1);
			
			//update the rzrq of the stock
			
			log.InsertLog("\n------update rzrq of stocks---------\n");
			
			Stock.updateCurrentRZRQAllStock(db, 1, log);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		

	}

}
