package autofinanceschedule.base;

import autofinanceschedule.func.Stock;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		MainDB db = new MainDB(true);

		try {
		
			//update the trade price 
			
			Stock.updateCurrentPrice(db, "2017");
			
			//update the total rzrq
			
			Stock.updateCurrentRZRQ(db, 1);
			
			
			//update the rzrq of the stock
			
			Stock.updateCurrentRZRQAllStock(db, 1);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
