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
			
			Document doc = ConfigFile.getConfig();
			
			String xqcookiename = "";
			
			String xqcookietoken = "";
			
			String emcookiename = "";
			
			String emcookievalue = "";
			
			double convert = ConfigConstant.PRICETRENDCONVERTPOINT;
			
			if (doc != null) {
				
				xqcookiename = doc.getString("xqcookiename");
				
				xqcookietoken = doc.getString("xqcookietoken");
				
				emcookiename = doc.getString("emcookiename");
				
				emcookievalue = doc.getString("emcookievalue");

				convert = doc.getDouble("pricetrendconvertpoint");
			}

			Stock.xqcookiename = xqcookiename;
			
			Stock.xqcookietoken = xqcookietoken;
			
			Stock.emcookiename = emcookiename;
			
			Stock.emcookievalue = emcookievalue;
			
			//update the total rzrq
			
			//update the rzrq of the stock
			
			log.InsertLog("\n------update rzrq of stocks---------\n");
			
			Stock.updateCurrentRZRQAllStock(db, 1, log);
			
			log.InsertLog("\n------update rzrq ---------\n");
			
			Stock.updateCurrentRZRQ(db, 1);
			
			//update the trade price 
			
			log.InsertLog("\n------update all stocks price---------\n");
			
			if (ldate.getDayOfWeek() == DayOfWeek.SATURDAY) {
				
				Stock.updateCurrentPrice(db, "2017", convert, log, 0);
				
			} else {
				
				Stock.updateCurrentPrice(db, "2017", convert, log, 1);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}

}
