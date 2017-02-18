package autofinanceschedule.func;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.UpdateOptions;

import autofinanceschedule.base.*;
import autofinanceschedule.log.logbase;

public class Stock {

	static public String xqcookiename = ConfigConstant.XUEQIU_TOKEN_NAME;
	
	static public String xqcookietoken = ConfigConstant.XUEQIU_TOKEN;
	
	/**
	 * Update current prices of two share markets
	 * @param db
	 * @throws Exception
	 */
	static public void updateCurrentPrice(MainDB db, String datestamp, logbase log) throws Exception {
		
		updateForTrader(db, datestamp, DBDATA.SOURCEDB_COLLECTION_STOCKSHDAYDATA, "SH", log);
		
		updateForTrader(db, datestamp, DBDATA.SOURCEDB_COLLECTION_STOCKSZDAYDATA, "SZ", log);
	}
	
	/**
	 * 
	 * @param db
	 * @param datestamp
	 * @param trader
	 * @param base
	 * @throws Exception
	 */
	static public void updateForTrader(MainDB db, String datestamp, String trader, String base, logbase log) throws Exception {
	    
	    UpdateOptions option = new UpdateOptions();
		
		option.upsert(true);
		
		FindIterable<Document> rs = db.getDb().getCollection(trader).find();
		
		rs = rs.projection(new Document()
				.append("code", 1)
				.append("lastdate", 1));
		
		int updatecount = 0;
		
		LocalDate date = LocalDate.now();
		  
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		  
		String version = date.format(formatter);
		
		for(Iterator<Document> i = rs.iterator(); i.hasNext();) {
			
			Document item = i.next();
			
			String code = item.getString("code");
			
			Date lastdate = item.getDate("lastdate");
			
			long begin = lastdate.getTime();// - (24 * 3600 * 1000);
			
			long end = (new Date()).getTime();
			
			List<Document> normal = getCurrentPrice(db, trader, code, base, "1day", "normal", begin, end);

			if ((normal != null) && (!normal.isEmpty())) {
			
				List<Document> before = getCurrentPrice(db, trader, code, base, "1day", "before", begin, end);
				
				List<Document> after = getCurrentPrice(db, trader, code, base, "1day", "after", begin, end);
				
				Date newlastdate = normal.get(normal.size() - 1).getDate("time");
			
				db.getDb().getCollection(trader).updateOne(new Document("code", code), new Document()
						.append("$set", new Document()
								.append("lastdate", newlastdate)
								.append("version", version)
								.append("updatedate", new Date()))
						.append("$push", new Document()
								.append("normal", new Document()
										.append("$each", normal)
										.append("$sort", new Document("time", -1)))
								.append("before", new Document()
										.append("$each", before)
										.append("$sort", new Document("time", -1)))
								.append("after", new Document()
										.append("$each", after)
										.append("$sort", new Document("time", -1)))), option);
				
				updatecount++;
			
			} 
		}
		
		log.InsertLog("update the " + base + " market for " + updatecount + " stocks \n");
		
	}

    /**
     * 
     * @param db
     * @param trader
     * @param code
     * @param base
     * @param period
     * @param type
     * @param begin
     * @param end
     * @return
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	static public List<Document> getCurrentPrice(MainDB db, String trader, String code, String base, String period, String type, long begin, long end) throws Exception {
		
		List<Document> chartlist = new ArrayList<Document>();
		
		BasicCookieStore cookieStore = new BasicCookieStore();

	    BasicClientCookie cookie = new BasicClientCookie(xqcookiename, xqcookietoken);
	    
	    cookie.setDomain(".xueqiu.com");
	    
	    cookie.setPath("/");
	    
	    cookieStore.addCookie(cookie);
		
		String url = String.format(ConfigConstant.XUEQIU_DAYTRADE, base + code, period, type, begin, end);
		
		String WholePage = RetrieveWeb.GetPage(url, cookieStore);
		
		if (WholePage.equals("")) {
			
			return chartlist;
			
		}
		
		Document stockdoc = (Document)Document.parse(WholePage);

		chartlist = (ArrayList<Document>) stockdoc.get("chartlist");
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		DateTimeFormatter formatterch = DateTimeFormatter.ofPattern("EEE MMM dd hh:mm:ss x uuuu");
		
		int index = 0;
		
		for (Iterator<Document> j = chartlist.iterator(); j.hasNext();) {
			
			Document value = j.next();
			
			String tradedatestr = value.getString("time");
			
			LocalDate chdate = LocalDate.parse(tradedatestr, formatterch);
			
			chdate = chdate.plusDays(1);
			
			String tradedatech = chdate.toString();//.format(formatterch);
			
			Date tradedate = formatter.parse(tradedatech);
			
			chartlist.get(index).append("time", tradedate);
			
			index++;
			
		}
		
		return chartlist;
	}
	
	/**
	 * 
	 * @param db
	 * @param code
	 * @param base
	 * @param currentprice
	 */
	static public void computePriceTrend(MainDB db, String code, String base, Document priceset) {
		
		UpdateOptions option = new UpdateOptions();
		
		option.upsert(true);
		
		Document filter = new Document("code", code);
		
		FindIterable<Document> rs = db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKPRICETREND).find(filter);
		
		if (priceset.containsKey("close")) {
		
			double price = priceset.getDouble("close");
		
			if (rs.iterator().hasNext()) {
			
				Document item = rs.first();
			
				double minprice = 0.0;
				
				if (item.containsKey("minprice")) {
					
					minprice = item.getDouble("minprice");
					
				}
				
				double maxprice = 0.0;
				
				if (item.containsKey("maxprice")) {
					
					minprice = item.getDouble("maxprice");
					
				}
				
				int trend = item.getInteger("trend", 0);
				
				int during = item.getInteger("during", 0);
				
				Document newupdate = new Document();
				
				if (price > maxprice) {
					
					if (trend == 1) {
						
						double range = (price - minprice) / minprice;
						
						newupdate.append("maxprice", price)
						.append("during", during + 1)
						.append("range", range)
						.append("currentprice", price)
						.append("maxupdate", new Date())
						.append("updatedate", new Date());
						
						db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKPRICETREND).updateOne(filter, new Document()
								.append("$set", newupdate), option);
						
						return;
						
						
					} else {
						
						double range = (price - minprice) / minprice;
						
						newupdate.append("maxprice", price)
						.append("during", during + 1)
						.append("range", range)
						.append("trend", 1)
						.append("currentprice", price)
						.append("maxupdate", new Date())
						.append("updatedate", new Date());
						
						db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKPRICETREND).updateOne(filter, new Document()
								.append("$set", newupdate), option);
						
						return;
						
						
					}
				} else if (price < minprice) {
					
					if (trend == 0) {
						
						double range = (price - maxprice) / maxprice;
						
						newupdate.append("minprice", price)
						.append("during", during + 1)
						.append("range", range)
						.append("currentprice", price)
						.append("minupdate", new Date())
						.append("updatedate", new Date());
						
						db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKPRICETREND).updateOne(filter, new Document()
								.append("$set", newupdate), option);
						
						return;
						
					} else {
						
						double range = (price - maxprice) / maxprice;
						
						newupdate.append("minprice", price)
						.append("during", during + 1)
						.append("range", range)
						.append("trend", 0)
						.append("currentprice", price)
						.append("minupdate", new Date())
						.append("updatedate", new Date());
						
						db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKPRICETREND).updateOne(filter, new Document()
								.append("$set", newupdate), option);
						
						return;
						
						
					}
				}
			}
		
		}
		
	}
	/**
	 * Add tags for exact shares
	 * @param db
	 * @param tags
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	static public void AddStockTags(MainDB db, List<String> tags) throws Exception {

		BasicCookieStore cookieStore = new BasicCookieStore();

	    BasicClientCookie cookie = new BasicClientCookie(xqcookiename, xqcookietoken);
	    
	    cookie.setDomain(".xueqiu.com");
	    
	    cookie.setPath("/");
	    
	    cookieStore.addCookie(cookie);
	    
		String WholePage = RetrieveWeb.GetPage(ConfigConstant.XUEQIU_STOCKTYPE, cookieStore);
		
		Document doc = Document.parse(WholePage);
		
		List<Document> data = (ArrayList<Document>)doc.get("data");
		
		for (Iterator<Document> i = data.iterator(); i.hasNext();) {
			
			Document item = i.next();
			
			String bcode = item.getString("symbol");
			
			String base = bcode.substring(0, 2);
			
			String code = bcode.substring(2);
			
			String collection = "";
			
			if (base.toLowerCase().equals("sz")) {
				
				collection = DBDATA.SOURCEDB_COLLECTION_STOCKDATASZ;
				
			} else {
				
				collection = DBDATA.SOURCEDB_COLLECTION_STOCKDATASH;
				
			}
			
			db.getDb().getCollection(collection).updateOne(new Document("code", code), new Document()
					.append("$addToSet", new Document()
							.append("tags", new Document("$each", tags))));
		}
		
	}

	/**
	 * Update month datas of two markets
	 * @param db
	 * @throws Exception
	 */
	static public void updateMonthData(MainDB db, String datestamp) throws Exception {
		
		updateMonthDataForTrader(db, datestamp, DBDATA.SOURCEDB_COLLECTION_STOCKDATASH, "SH");
		
		updateMonthDataForTrader(db, datestamp, DBDATA.SOURCEDB_COLLECTION_STOCKDATASZ, "SZ");
	}
	
	/**
	 * 
	 */
	static public void updateMonthDataForTrader(MainDB db, String datestamp, String trader, String base) throws Exception {
		
		FindIterable<Document> rs = db.getDb().getCollection(trader).find(new Document("version", new Document("$ne", datestamp)));
		
		BasicCookieStore cookieStore = new BasicCookieStore();

	    BasicClientCookie cookie = new BasicClientCookie(xqcookiename, xqcookietoken);
	    
	    cookie.setDomain(".xueqiu.com");
	    
	    cookie.setPath("/");
	    
	    cookieStore.addCookie(cookie);
	    
	    UpdateOptions option = new UpdateOptions();
		
		option.upsert(true);
		
		for(Iterator<Document> i = rs.iterator(); i.hasNext();) {
			
			Document item = i.next();
			
			String code = item.getString("code");
			
			String url = String.format(ConfigConstant.XUEQIE_MONTHDATA_BEFORE, base + code);
			
			String WholePage = RetrieveWeb.GetPage(url, cookieStore);
			
			Document stockdoc = (Document)Document.parse(WholePage);
			
			List<Document> chartlist = (ArrayList<Document>) stockdoc.get("chartlist");
			
			db.getDb().getCollection(trader).updateOne(new Document("code", code), new Document()
					.append("$set", new Document()
							.append("monthdatabefore", chartlist)
							.append("version", datestamp)
							.append("updatedate", new Date())), option);
		}
		
	}
	
	
	/**
	 * 
	 * @param db
	 * @param datestamp
	 * @param endtime
	 * @throws Exception
	 */
	static public void updateWeekData(MainDB db, String datestamp, String endtime) throws Exception {
		
		updateWeekDataForTrader(db, datestamp, DBDATA.SOURCEDB_COLLECTION_STOCKDATASH, DBDATA.SOURCEDB_COLLECTION_STOCKSHWEEKDATA, "SH", endtime);
		
		updateWeekDataForTrader(db, datestamp, DBDATA.SOURCEDB_COLLECTION_STOCKDATASZ,DBDATA.SOURCEDB_COLLECTION_STOCKSZWEEKDATA, "SZ", endtime);
	}
	
	
	/**
	 * 
	 * @param db
	 * @param datestamp
	 * @param trader
	 * @param newtrader
	 * @param base
	 * @param endtime
	 * @throws Exception
	 */
	static public void updateWeekDataForTrader(MainDB db, String datestamp, String trader, String newtrader, String base, String endtime) throws Exception {
		
		FindIterable<Document> rs = db.getDb().getCollection(trader).find(new Document("version", new Document("$ne", datestamp)));
		
		BasicCookieStore cookieStore = new BasicCookieStore();

	    BasicClientCookie cookie = new BasicClientCookie(xqcookiename, xqcookietoken);
	    
	    cookie.setDomain(".xueqiu.com");
	    
	    cookie.setPath("/");
	    
	    cookieStore.addCookie(cookie);
	    
	    UpdateOptions option = new UpdateOptions();
		
		option.upsert(true);
		
		for(Iterator<Document> i = rs.iterator(); i.hasNext();) {
			
			Document item = i.next();
			
			String code = item.getString("code");
			
			String name = item.getString("name");
			
			String url = String.format(ConfigConstant.XUEQUE_WEEKDATA_BEFORE, base + code, endtime);
			
			String WholePage = RetrieveWeb.GetPage(url, cookieStore);
			
			Document stockdoc = (Document)Document.parse(WholePage);
			
			List<Document> chartlist = (ArrayList<Document>) stockdoc.get("chartlist");
			
			db.getDb().getCollection(newtrader).updateOne(new Document("code", code), new Document()
					.append("$set", new Document()
							.append("name", name)
							.append("code", code)
							.append("base", base)
							.append("weekdatabefore", chartlist)
							.append("version", datestamp)
							.append("updatedate", new Date())), option);
			
			db.getDb().getCollection(trader).updateOne(new Document("code", code), new Document()
					.append("$set", new Document()
							.append("version", datestamp)
							.append("updatedate", new Date())), option);
		}
		
	}

	/**
	 * Extract the certain year data for shares
	 * @param db
	 * @param year
	 * @throws Exception
	 */
	static public void updateMonthDataForExactYear(MainDB db, int year, String datestamp) throws Exception {
		
		updateMonthDataForExactYearForTrader(db, year, datestamp, DBDATA.SOURCEDB_COLLECTION_STOCKDATASH);
		
		updateMonthDataForExactYearForTrader(db, year, datestamp, DBDATA.SOURCEDB_COLLECTION_STOCKDATASZ);
		
	}
	
	/**
	 * 
	 * @param db
	 * @param year
	 * @param datestamp
	 * @param trader
	 * @throws Exception
	 */
	static public void updateMonthDataForExactYearForTrader(MainDB db, int year, String datestamp, String trader) throws Exception {
		
		FindIterable<Document> rs = db.getDb().getCollection(trader).find(new Document("version", new Document("$ne", datestamp)));
		
		UpdateOptions option = new UpdateOptions();
		
		option.upsert(true);
	
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd hh:mm:ss x uuuu");
		
		for(Iterator<Document> i = rs.iterator(); i.hasNext();) {
			
			double first = 0.0;
			
			double end = 0.0;
			
			boolean isdown = true;
			
			double startopen = 0.0;
			
			double endclose = 0.0;
			
			Document item = i.next();
			
			String code = item.getString("code");
			
			List<Document> monthdatabefore = (ArrayList<Document>)item.get("monthdatabefore");
			
			List<Document> yearlist = new ArrayList<Document>();
			
			for(Iterator<Document> j = monthdatabefore.iterator(); j.hasNext();) {
				
				Document value = j.next();
				
				String datetime = value.getString("time");

				LocalDate zdatetime = LocalDate.parse(datetime, formatter);
				
				if (zdatetime.getYear() == year) {
					
					int month = zdatetime.getMonthValue();
					
					double low = value.getDouble("low");
					
					double high = value.getDouble("high");
					
					double open = value.getDouble("open");
					
					double close = value.getDouble("close");
					
					if (month == 1) {
						
						startopen = open;
						
						first = close;
						
						end = open;
						
						if (open < close) {
							
							isdown = false;
						}
						
					} else {
						
						if (close < first) {
							
							if (!isdown) {
								
								if (close < end) {
									
									isdown = true;
									
									end = first;
									
									first = close;

								}
							} else {
								
								first = close;
							}
							
						} else {
							
							if (!isdown) {
								
								first = close;
								
							} else {
								
								isdown = false;
								
								end = first;
								
								first = close;
								
							}
						}
						
					}

					if (month == 12) {
						
						endclose = close;
						
					}
					
					Document doc = new Document()
							.append("month", month)
							.append("low", low)
							.append("high", high)
							.append("open", open)
							.append("close", close);
					
					yearlist.add(doc);
					
				}
			}
			
			
			double aveginc = (endclose - startopen) / startopen;
			
			double aveghl = 0.0;
				
			aveghl = (first - end) / end;
			
			String monthdatabyyear = "monthdatabyyear." + String.valueOf(year);
			
			String mdbym = monthdatabyyear + ".month";
			
			String mdbbys = monthdatabyyear + ".startopen";
			
			String mdbbye = monthdatabyyear + ".endclose";
			
			String mdbbyesa = monthdatabyyear + ".aveginc";
			
			String mdbbyh = monthdatabyyear + ".first";
			
			String mdbbyl = monthdatabyyear + ".end";
			
			String mdbbyhl = monthdatabyyear + ".avegfe";
			
			db.getDb().getCollection(trader).updateOne(new Document("code", code), new Document()
					.append("$set", new Document()
							.append(mdbbys, startopen)
							.append(mdbbye, endclose)
							.append(mdbbyesa, aveginc)
							.append(mdbbyh, first)
							.append(mdbbyl, end)
							.append(mdbbyhl, aveghl)
							.append(mdbym, yearlist)
							.append("version", datestamp)
							.append("updatedate", new Date())), option);
			
		}
		
	}

	/**
	 * to get rzrq data from d to d
	 * @param db
	 * @param fromdate
	 * @param todate
	 * @throws Exception
	 */
	static public void updateRZRQ(MainDB db, LocalDate fromdate, LocalDate todate, int pagesize) throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
		
		LocalDate cdate = fromdate;
		
		List<Document> alldata = getRZRQTotal(pagesize);
		
		UpdateOptions option = new UpdateOptions();
		
		option.upsert(true);
		
		FindIterable<Document> rs = db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKRZRQTOTAL).find();
		
		rs = rs.sort(new Document("rrdate", 1));
		
		rs = rs.limit(1);
		
		LocalDate lastdate = LocalDate.parse("1900-01-01", formatter);
		
		if (rs.iterator().hasNext()) {
			
			Document item = rs.first();
			
			LocalDate ldate = (LocalDate) item.get("lastdate");
			
			if (ldate.isAfter(lastdate)) {
				
				lastdate = ldate;
			}
		}
		
		do {
			
			for (Iterator<Document> i = alldata.iterator(); i.hasNext();) {
				
				Document item = i.next();
				
				String rrdate = item.getString("rrdate");
				
				LocalDate tempdate = LocalDate.parse(rrdate, formatter);
				
				if (tempdate.isAfter(lastdate)) {
					
					lastdate = tempdate;
					
				}
				
				if (tempdate.equals(cdate)) {
					
					db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKRZRQTOTAL).updateOne(new Document("rrdate", rrdate), new Document("$set", item), option);
					
					break;
					
				}
			}
			
			cdate = cdate.minusDays(1);
			
		} while (todate.isBefore(cdate));
		
		db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKRZRQTOTAL).updateOne(new Document("lastdate", new Document("$exists", true)), new Document("$set", new Document("lastdate", lastdate)), option);
		
	}
	
	/**
	 * 
	 * @param db
	 * @throws Exception
	 */
	static public void updateCurrentRZRQ(MainDB db, int pagesize) throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
		
		List<Document> alldata = getRZRQTotal(pagesize);
		
		if (alldata.isEmpty()) {
			
			return;
			
		}
		
		UpdateOptions option = new UpdateOptions();
		
		option.upsert(true);
		
		FindIterable<Document> rs = db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKRZRQTOTAL).find();
		
		rs = rs.sort(new Document("rrdate", -1));
		
		rs = rs.limit(1);
		
		LocalDate lastdate = LocalDate.parse("1900-01-01", formatter);
		
		if (rs.iterator().hasNext()) {
			
			Document item = rs.first();
			
			Date input = (Date) item.get("rrdate");
			
			Instant instant = input.toInstant();
			
			ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
			
			LocalDate ldate = zdt.toLocalDate();
			
			if (ldate.isAfter(lastdate)) {
				
				lastdate = ldate;
			}
		}
			
		for (Iterator<Document> i = alldata.iterator(); i.hasNext();) {
				
			Document item = i.next();
				
			Date input = (Date) item.get("rrdate");
			
			Instant instant = input.toInstant();
			
			ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
			
			LocalDate tempdate = zdt.toLocalDate();
				
			if (tempdate.isAfter(lastdate)) {
					
				db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKRZRQTOTAL).updateOne(new Document("rrdate", input), new Document("$set", item), option);
					
			}
			
		}
		
	}
	
	/**
	 * to get rzrq data from the eastmoney
	 * @param db
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	static public List<Document> getRZRQTotal(int pagesize) throws Exception {
		
		List<Document> rzrqlist = new ArrayList<Document>();
		
		DateFormat formatter = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
		
		for (int i = 1; i <= pagesize; i++) {
		
			String url = String.format(ConfigConstant.EASTMONEY_RZRQ, String.valueOf(i));
		
			String WholePage = RetrieveWeb.GetPage(url);
			
			if (WholePage.equals("")) {
				
				continue;
				
			}
			
			String content = WholePage.replaceAll("\",\"", ";");
			
			content = content.replaceAll("\\)|\\[|\\]|\\(|\"", "");
			
			List<String> list = Arrays.asList(content.split(";"));
			
			for (Iterator<String> j = list.iterator(); j.hasNext();) {
				
				String item = j.next();
				
				List<String> myList = Arrays.asList(item.split(","));
				
				Document doc = new Document();
				
				String rrtempdate = myList.get(0) + " 16:00:00";
				
				Date rrdate = formatter.parse(rrtempdate);
				
				doc.append("rrdate", rrdate);
				
				String shrztr = myList.get(1);
				
				if (shrztr == "-") {
					
					continue;
				}
				doc.append("shrztr", Double.parseDouble(shrztr));
				
				String szrztr = myList.get(2);
				
				if (szrztr.equals("-")) {
					
					continue;
				}
				doc.append("szrztr", Double.parseDouble(szrztr));
				
				String rztr = myList.get(3);
				
				if (rztr.equals("-")) {
					
					continue;
				}
				doc.append("rztr", Double.parseDouble(rztr));
				
				String shrztb = myList.get(4);
				
				if (shrztb.equals("-")) {
					
					continue;
				}
				doc.append("shrztb", Double.parseDouble(shrztb));
				
				String szrztb = myList.get(5);
				
				if (szrztb.equals("-")) {
					
					continue;
				}
				doc.append("szrztb", Double.parseDouble(szrztb));
				
				String rztb = myList.get(6);
				
				if (rztb.equals("-")) {
					
					continue;
				}
				doc.append("rztb", Double.parseDouble(rztb));
				
				String shrqtr = myList.get(7);
				
				if (shrqtr.equals("-")) {
					
					continue;
				}
				doc.append("shrqtr", Double.parseDouble(shrqtr));
				
				String szrqtr = myList.get(8);
				
				if (szrqtr.equals("-")) {
					
					continue;
				}
				doc.append("szrqtr", Double.parseDouble(szrqtr));
				
				String rqtr = myList.get(9);
				
				if (rqtr.equals("-")) {
					
					continue;
				}
				doc.append("rqtr", Double.parseDouble(rqtr));
				
				String shrzrqtr = myList.get(10);
				
				if (shrzrqtr.equals("-")) {
					
					continue;
				}
				doc.append("shrzrqtr", Double.parseDouble(shrzrqtr));
				
				String szrzrqtr = myList.get(11);
				
				if (szrzrqtr.equals("-")) {
					
					continue;
				}
				doc.append("szrzrqtr", Double.parseDouble(szrzrqtr));
				
				String rzrqtr = myList.get(12);
				
				if (rzrqtr.equals("-")) {
					
					continue;
				}
				doc.append("rzrqtr", Double.parseDouble(rzrqtr));
				
				rzrqlist.add(doc);
			}
		
		}
		
		return rzrqlist;
	}
	
	/**
	 * 
	 * @param db
	 * @param pagesize
	 * @throws Exception
	 */
	static public void updateCurrentRZRQAllStock(MainDB db, int pagesize,  logbase log) throws Exception {
		
		FindIterable<Document> rs = db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKRZRQ).find();
		
		rs = rs.projection(new Document()
				.append("code", 1)
				.append("name", 1)
				.append("base", 1));
		
		int updatecount = 0;
		
		for (Iterator<Document> i = rs.iterator(); i.hasNext();) {
			
			Document item = i.next();
			
			String code = item.getString("code");
			
			String name = item.getString("name");
			
			String base = item.getString("base");
			
			if (Stock.updateCurrentRZRQStock(db, code, base, name, pagesize)) {
				
				updatecount++;
				
			}
		}
		
		log.InsertLog("update rzrq " + updatecount + " stocks \n");
	}
	/**
	 * update the rzrq data of the stock named the code
	 * @param db
	 * @param code
	 * @param pagesize
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	static public boolean updateCurrentRZRQStock(MainDB db, String code, String name, String base, int pagesize) throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
		
		List<Document> alldata = getRZRQStock(code, base, pagesize);
		
		if (alldata.isEmpty()) {
			
			return false;
			
		}
		UpdateOptions option = new UpdateOptions();
		
		option.upsert(true);
		
		FindIterable<Document> rs = db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKRZRQ).find(new Document("code", code));
		
		rs = rs.projection(new Document("rzrq", 1));

		List<Document> rzrqlist  = null;
		
		if (rs.iterator().hasNext()) {
			
			rzrqlist = (ArrayList<Document>)rs.first().get("rzrq");
			
		}
				
		LocalDate lastdate = LocalDate.parse("1900-01-01", formatter);
		
		if (rzrqlist.size() > 0) {
			
			Document item = rzrqlist.get(0);
			
			Date input = (Date) item.get("rrdate");
			
			Instant instant = input.toInstant();
			
			ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
			
			LocalDate ldate = zdt.toLocalDate();
			
			if (ldate.isAfter(lastdate)) {
				
				lastdate = ldate;
			}
		}
			
		List<Document> needupdate = new ArrayList<Document>();
		
		for (Iterator<Document> i = alldata.iterator(); i.hasNext();) {
				
			Document item = i.next();
				
			Date input = (Date) item.get("rrdate");
			
			Instant instant = input.toInstant();
			
			ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
			
			LocalDate tempdate = zdt.toLocalDate();
				
			if (tempdate.isAfter(lastdate)) {
					
				needupdate.add(item);
					
			}
		}
		
		if (needupdate.size() > 0) {
		
			db.getDb().getCollection(DBDATA.SOURCEDB_COLLECTION_STOCKRZRQ).updateOne(new Document("code", code), new Document()
				.append("$push", new Document("rzrq", new Document()
						.append("$each", needupdate)
						.append("$sort", new Document("rrdate", -1)))), option);
		
			return true;
		
		} else {
			
			return false;
		}
	}
	/**
	 * get the rzrq data of the stock
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	static public List<Document> getRZRQStock(String code, String base, int pagesize) throws Exception {
		
		List<Document> rzrqlist = new ArrayList<Document>();
		
		DateFormat formatter = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
		
		String mkt = "1";
		
		if (base.equals("SZ")) {
			
			mkt = "2";
			
		}
		
		for (int i = 1; i <= pagesize; i++) {
		
			String url = String.format(ConfigConstant.EASTMONEY_RZRQ_STOCK, mkt, code, String.valueOf(i));
		
			String WholePage = RetrieveWeb.GetPage(url);
			
			if (WholePage.equals("")) {
				
				continue;
				
			}
			
			String content = WholePage.replaceAll("\",\"", ";");
			
			content = content.replaceAll("\\)|\\[|\\]|\\(|\"", "");
			
			content = content.replaceAll("/", "-");
			
			List<String> list = Arrays.asList(content.split(";"));
			
			for (Iterator<String> j = list.iterator(); j.hasNext();) {
				
				String item = j.next();
				
				if (item.endsWith("\n")) {
					
					item = item.replaceAll("\n", "");
					
				}
				
				if (item.endsWith(",")) {
					
					item = item + "0";
				}
				
				List<String> myList = Arrays.asList(item.split(","));
				
				if (myList.size() < 14) {
					
					continue;
				}
				
				Document doc = new Document();
				
				String rrtempdate = myList.get(4) + " 16:00:00";
				
				Date rrdate = formatter.parse(rrtempdate);
				
				doc.append("rrdate", rrdate);
				
				String rzremain = myList.get(12);
				
				if (rzremain == "-" || rzremain.isEmpty()) {
					
					rzremain = "0";
				}
				doc.append("rzremain", Double.parseDouble(rzremain));
				
				String rzbuy = myList.get(10);
				
				if (rzbuy.equals("-") || rzbuy.isEmpty()) {
					
					rzbuy = "0";
				}
				doc.append("rzbuy", Double.parseDouble(rzbuy));
				
				String rzsell = myList.get(9);
				
				if (rzsell.equals("-") || rzsell.isEmpty()) {
					
					rzsell = "0";
				}
				doc.append("rzsell", Double.parseDouble(rzsell));
				
				String rzdealnet = myList.get(13);
				
				if (rzdealnet.isEmpty() || rzdealnet.equals("-")) {
					
					rzdealnet = "0";
				}
				doc.append("rzdealnet", Double.parseDouble(rzdealnet));
				
				String rqremain = myList.get(7);
				
				if (rqremain.equals("-") || rqremain.isEmpty()) {
					
					rqremain = "0";
				}
				doc.append("rqremain", Double.parseDouble(rqremain));
				
				String rqcountremain = myList.get(8);
				
				if (rqcountremain.equals("-") || rqcountremain.isEmpty()) {
					
					rqcountremain = "0";
				}
				doc.append("rqcountremain", Double.parseDouble(rqcountremain));
				
				String rqsell = myList.get(6);
				
				if (rqsell.equals("-") || rqsell.isEmpty()) {
					
					rqsell = "0";
				}
				doc.append("rqsell", Double.parseDouble(rqsell));
				
				String rqreturn = myList.get(5);
				
				if (rqreturn.equals("-") || rqreturn.isEmpty()) {
					
					rqreturn = "0";
				}
				doc.append("rqreturn", Double.parseDouble(rqreturn));
				
				String rzrqremain = myList.get(11);
				
				if (rzrqremain.equals("-") || rzrqremain.isEmpty()) {
					
					rzrqremain = "0";
				}
				doc.append("rzrqremain", Double.parseDouble(rzrqremain));
				
				
				rzrqlist.add(doc);
			}
		
		}
		
		return rzrqlist;
	}
}
