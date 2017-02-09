package autofinanceschedule.base;

public class ConfigConstant {
	
	public static final String OPERATIONLOG_PHYADDRESS = "/Users/lihongbo/products/autofinanceschedule/log/autofinanceschedulelog.log";
	
	public static final String CONFIGFILE_DIR = "/Users/lihongbo/products/autofinanceschedule/config/config.af";

	public static final String 	STOCKCONTNET_GETTING = " is being gotten.";
	
	public static final String ROOTPATH = "/Users/lihongbo/products/智金/data/stockbasedata_sh/";
	
	public static final String ROOTPATH_SH = "/Users/lihongbo/products/智金/data/shstock@201606.txt";

	public static final String ROOTPATH_SZ = "/Users/lihongbo/products/智金/data/szstock@201606.txt";
	
	public static final String CASHANDVALUE_SZ = "/Users/lihongbo/products/智金/data/cashandvalue_sz.txt";
	
	public static final String CASHANDVALUE_SH = "/Users/lihongbo/products/智金/data/cashandvalue_sh.txt";
	
	public static final String XUEQIU_STOCKCURRNTDATA_ROOT = "http://xueqiu.com/S/";
	
	public static final String 	XUEQIU_STOCKCURRENTPRICE = "https://xueqiu.com/v4/stock/quote.json?code=";
	
	public static final String CASHANDVALUE_TITLE = "代码,名称,标签,现金流(2011-2015五年平均),市值（201606）,市盈率TTM,市净率TTM,现金流(5年平均)／市值,资产净值／市值,资产净值／权益,现金净值／市值,现金净值／资产净值,净资产收益率(3年平均),归属于母公司股东的权益,无形资产,商誉,资产净值（净资－无形－商誉）,现金及等价物（2015）,短期借款（2015）,长期借款（2015）,现金净值（2015） \n";
	
	public static final String XUEQIU_STOCKTYPE_ONE = "https://xueqiu.com/industry/quote_order.json?page=2&size=90&order=desc&exchange=CN&plate=软件和信息技术服务业&orderBy=percent&level2code=I65";
	
	public static final String XUEQIU_STOCKTYPE = "https://xueqiu.com/industry/quote_order.json?page=1&size=90&order=desc&exchange=CN&plate=土木工程建筑业&orderBy=percent&level2code=E48&_=1482570991136";
	
	public static final String XUEQIE_MONTHDATA = "https://xueqiu.com/stock/forchartk/stocklist.json?symbol=%s&period=1month&type=normal&end=1483237387388&_=1483237387389";
	
	public static final String XUEQIU_CASHANDFLOW = "https://xueqiu.com/stock/f10/cfstatement.json?symbol=%s&page=%s&size=4&_=1483423710929";
	
	public static final String XUEQIU_INCOME = "https://xueqiu.com/stock/f10/incstatement.json?symbol=%s&page=%s&size=4&_=1483423977141";
	
	public static final String XUEQIU_BALANCE = "https://xueqiu.com/stock/f10/balsheet.json?symbol=%S&page=%S&size=4&_=1483424102728";
	
	public static final String XUEQIE_MONTHDATA_BEFORE = "https://xueqiu.com/stock/forchartk/stocklist.json?symbol=%s&period=1month&type=before&end=1483333767864&_=1483333767864";
	
	public static final String XUEQUE_WEEKDATA_BEFORE = "https://xueqiu.com/stock/forchartk/stocklist.json?symbol=%s&period=1week&type=before&end=%s&_=1483584931615";//1483584881225
	
	public static final String XUEQIU_DAYTRADE = "https://xueqiu.com/stock/forchartk/stocklist.json?symbol=%s&period=%s&type=%s&begin=%s&end=%s";
	
	public static final String EASTMONEY_RZRQ = "http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=FD&sty=SHSZHSSUM&p=%s";
	
	public static final String EASTMONEY_RZRQ_STOCK = "http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=FD&sty=MTE&mkt=%s&code=%s&p=%s";

	public static final String XUEQIU_STOCKTYPE_TAG = "土木工程建筑";
	
	public static final String XUEQIU_TOKEN_NAME = "xq_a_token";
	
	public static final String XUEQIU_TOKEN = "01b36c9240f85c0ad4f9a5defab39ce136ff3deb";
	
	public static final String TEMPFILE_MONTHDATA = "monthdata@";
	
	public static final String TEMPFILE_SUFFIX = ".zip";
	
	public static final String FILE_MONTHDATA_SZ = "monthdata_sz@";
	
	public static final String FILE_MONTHDATA_SH = "monthdata_sh@";
	
	public static final String FILE_SUFFIX = ".txt";
	
	public static final String MONTHDATA_TITLE = "代码,名称,年份,开盘价,收盘价,年涨跌幅,最高月收盘价,最低月收盘价,峰值涨跌幅 \n";
	
}
