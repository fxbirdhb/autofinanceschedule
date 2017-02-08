package autofinanceschedule.base;

import java.util.Date;

import autofinanceschedule.func.Stock;
import autofinanceschedule.log.SimpleLog;
import autofinanceschedule.log.logbase;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		try {
			
			MainDB db = new MainDB(true);
			
			logbase log = new SimpleLog(ConfigConstant.OPERATIONLOG_PHYADDRESS);
			
			log.InsertLog("\n---------------update at " + (new Date()).toString() + "----------------------\n");
			
			log.InsertLog("\n------update all stocks price---------\n");

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
