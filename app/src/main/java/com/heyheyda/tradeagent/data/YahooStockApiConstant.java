package com.heyheyda.tradeagent.data;

public class YahooStockApiConstant {
    
    //formats
    public static final String REAL_TIME_URL_FORMAT = "https://query1.finance.yahoo.com/v7/finance/quote?fields=%s&symbols=%s&formatted=%s";
    public static final String HISTORY_URL_FORMAT = "https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=%s&range=%s";
    //field names
    public static final String SYMBOL = "symbol";
    public static final String TIMEZONE = "exchangeTimezoneName";
    //-- real time
    public static final String NAME = "shortName";
    public static final String PRICE = "regularMarketPrice";
    public static final String HIGH = "regularMarketDayHigh";
    public static final String LOW = "regularMarketDayLow";
    public static final String OPEN = "regularMarketOpen";
    public static final String CHANGE = "regularMarketChange";
    public static final String CHANGE_PERCENT = "regularMarketChangePercent";
    public static final String VOLUME = "regularMarketVolume";
    public static final String TIME = "regularMarketTime";
    public static final String CURRENCY = "currency";
    public static final String QUANTITY = "quantity";
    //-- history
    public static final String HISTORY_CLOSE = "close";
    public static final String HISTORY_HIGH = "high";
    public static final String HISTORY_LOW = "low";
    public static final String HISTORY_OPEN = "open";
    public static final String HISTORY_VOLUME = "volume";
    public static final String TIME_STAMP = "timestamp";

    //interval (history)
    public static final String HISTORY_INTERVAL_1_MIN = "1m";
    public static final String HISTORY_INTERVAL_2_MIN = "2m";
    public static final String HISTORY_INTERVAL_5_MIN = "5m";
    public static final String HISTORY_INTERVAL_15_MIN = "15m";
    public static final String HISTORY_INTERVAL_30_MIN = "30m";
    public static final String HISTORY_INTERVAL_60_MIN = "60m";
    public static final String HISTORY_INTERVAL_90_MIN = "90m";
    public static final String HISTORY_INTERVAL_1_HOUR = "1h";
    public static final String HISTORY_INTERVAL_1_DAY = "1d";
    public static final String HISTORY_INTERVAL_5_DAY = "5d";
    public static final String HISTORY_INTERVAL_1_WEAK = "1wk";
    public static final String HISTORY_INTERVAL_1_MONTH = "1mo";
    public static final String HISTORY_INTERVAL_3_MONTH = "3mo";
    //range (history)
    public static final String HISTORY_RANGE_1_DAY = "1d";
    public static final String HISTORY_RANGE_5_DAY = "5d";
    public static final String HISTORY_RANGE_1_MONTH = "1mo";
    public static final String HISTORY_RANGE_3_MONTH = "3mo";
    public static final String HISTORY_RANGE_6_MONTH = "6mo";
    public static final String HISTORY_RANGE_1_YEAR = "1y";
    public static final String HISTORY_RANGE_2_YEAR = "2y";
    public static final String HISTORY_RANGE_5_YEAR = "5y";
    public static final String HISTORY_RANGE_10_YEAR = "10y";
    public static final String HISTORY_RANGE_YEAR_TO_DATE = "ytd";
    public static final String HISTORY_RANGE_MAX = "max";
    
    //others
    public static final String FIELD_SEPARATION_MARK = ",";
    public static final String SYMBOL_SEPARATION_MARK = ",";
    public static final String FORMATTED_TRUE = "true";
    public static final String FORMATTED_FALSE = "false";

    /**
     * To prevent someone from accidentally instantiating the constant class, give it an empty constructor.
     */
    private YahooStockApiConstant(){}



    //raw formats
    //-- real time
    public static final String YAHOO_REALTIME_00 = "https://query2.finance.yahoo.com/v10/finance/quoteSummary/AAPL?formatted=true&crumb=o7SyyILY0qT&lang=en-US&region=US&modules=price,summaryDetail&corsDomain=finance.yahoo.com";
    public static final String YAHOO_REALTIME_01 = "https://query2.finance.yahoo.com/v10/finance/quoteSummary/GOOG?formatted=true&crumb=o7SyyILY0qT&lang=en-US&region=US&modules=price,summaryDetail&corsDomain=finance.yahoo.com";
    public static final String YAHOO_REALTIME_10 = "https://query2.finance.yahoo.com/v10/finance/quoteSummary/AAPL?formatted=true&crumb=o7SyyILY0qT&lang=en-US&region=US&modules=summaryProfile,financialData,recommendationTrend,upgradeDowngradeHistory,earnings,defaultKeyStatistics,calendarEvents,esgScores,details&corsDomain=finance.yahoo.com";
    public static final String YAHOO_REALTIME_11 = "https://query1.finance.yahoo.com/v10/finance/quoteSummary/GOOG?formatted=true&crumb=o7SyyILY0qT&lang=en-US&region=US&modules=summaryProfile,financialData,recommendationTrend,upgradeDowngradeHistory,earnings,defaultKeyStatistics,calendarEvents,esgScores,details&corsDomain=finance.yahoo.com";
    public static final String YAHOO_REALTIME_20 = "https://query1.finance.yahoo.com/v7/finance/quote?formatted=true&crumb=o7SyyILY0qT&lang=en-US&region=US&symbols=AAPL&fields=messageBoardId,longName,shortName,marketCap,underlyingSymbol,underlyingExchangeSymbol,headSymbolAsString,regularMarketPrice,regularMarketChange,regularMarketChangePercent,regularMarketVolume,uuid,regularMarketOpen,fiftyTwoWeekLow,fiftyTwoWeekHigh&corsDomain=finance.yahoo.com";
    public static final String YAHOO_REALTIME_21 = "https://query1.finance.yahoo.com/v7/finance/quote?formatted=true&crumb=o7SyyILY0qT&lang=en-US&region=US&symbols=GOOG&fields=messageBoardId,longName,shortName,marketCap,underlyingSymbol,underlyingExchangeSymbol,headSymbolAsString,regularMarketPrice,regularMarketChange,regularMarketChangePercent,regularMarketVolume,uuid,regularMarketOpen,fiftyTwoWeekLow,fiftyTwoWeekHigh&corsDomain=finance.yahoo.com";
    public static final String YAHOO_REALTIME_30 = "https://query2.finance.yahoo.com/v7/finance/quote?formatted=true&crumb=o7SyyILY0qT&lang=en-US&region=US&symbols=AAPL,FB,AMZN,NFLX,TSLA&fields=longName,shortName,regularMarketPrice,regularMarketChange,regularMarketChangePercent,messageBoardId,marketCap,underlyingSymbol,underlyingExchangeSymbol,headSymbolAsString,regularMarketVolume,uuid,regularMarketOpen,fiftyTwoWeekLow,fiftyTwoWeekHigh&corsDomain=finance.yahoo.com";
    public static final String YAHOO_REAL_TIME_00 = "https://query1.finance.yahoo.com/v7/finance/quote?lang=en-US&region=US&corsDomain=finance.yahoo.com&fields=symbol,longName,shortName,priceHint,regularMarketPrice,regularMarketChange,regularMarketChangePercent,currency,regularMarketTime,regularMarketVolume,quantity,averageDailyVolume3Month,regularMarketDayHigh,regularMarketDayLow,regularMarketPrice,regularMarketOpen,fiftyTwoWeekHigh,fiftyTwoWeekLow,regularMarketPrice,regularMarketOpen,sparkline,marketCap&symbols=AAPL&formatted=false";
    //-- history
    public static final String YAHOO_MONTH_FORMAT = "https://query1.finance.yahoo.com/v8/finance/chart/2330.TW?region=US&lang=en-US&includePrePost=false&interval=30m&range=1mo&corsDomain=finance.yahoo.com&.tsrc=finance";
    public static final String YAHOO_DAY_FORMAT = "https://query1.finance.yahoo.com/v8/finance/chart/2330.TW?region=US&lang=en-US&includePrePost=false&interval=1m&range=1d&corsDomain=finance.yahoo.com&.tsrc=finance";
    public static final String TWSE_YEAR_FORMAT = "http://www.twse.com.tw/exchangeReport/FMNPTK?response=json&stockNo=1416&_=1536315794981";
    public static final String TWSE_MONTH_FORMAT = "http://www.twse.com.tw/exchangeReport/FMSRFK?response=json&date=20180907&stockNo=1416&_=1536315899941";
    public static final String TWSE_DAY_FORMAT = "http://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=20180501&stockNo=1416&_=1533289429610";


    //raw module names
    public static final String price = "price";
    public static final String summaryDetail = "summaryDetail";
    public static final String summaryProfile = "summaryProfile";
    public static final String financialData = "financialData";
    public static final String recommendationTrend = "recommendationTrend";
    public static final String upgradeDowngradeHistory = "upgradeDowngradeHistory";
    public static final String earnings = "earnings";
    public static final String defaultKeyStatistics = "defaultKeyStatistics";
    public static final String calendarEvents = "calendarEvents";
    public static final String esgScores = "esgScores";
    public static final String details = "details";

    //raw fields (common)
    public static final String result = "result";
    public static final String symbol = "symbol";
    public static final String priceHint = "priceHint";
    public static final String currency = "currency";
    public static final String exchangeTimezoneName = "exchangeTimezoneName";

    //raw fields (real time)
    public static final String quoteResponse = "quoteResponse";
    public static final String messageBoardId = "messageBoardId";
    public static final String underlyingSymbol = "underlyingSymbol";
    public static final String underlyingExchangeSymbol = "underlyingExchangeSymbol";
    public static final String headSymbolAsString = "headSymbolAsString";
    public static final String uuid = "uuid";
    public static final String longName = "longName";
    public static final String shortName = "shortName";
    public static final String regularMarketPrice = "regularMarketPrice";
    public static final String regularMarketChange = "regularMarketChange";
    public static final String regularMarketChangePercent = "regularMarketChangePercent";
    public static final String regularMarketTime = "regularMarketTime";
    public static final String regularMarketVolume = "regularMarketVolume";
    public static final String quantity = "quantity";
    public static final String averageDailyVolume3Month = "averageDailyVolume3Month";
    public static final String regularMarketDayHigh = "regularMarketDayHigh";
    public static final String regularMarketDayLow = "regularMarketDayLow";
    public static final String regularMarketOpen = "regularMarketOpen";
    public static final String fiftyTwoWeekHigh = "fiftyTwoWeekHigh";
    public static final String fiftyTwoWeekLow = "fiftyTwoWeekLow";
    public static final String sparkline = "sparkline";
    public static final String marketCap = "marketCap";
    public static final String exchangeTimezoneShortName = "exchangeTimezoneShortName";

    //raw fields (history)
    public static final String chart = "chart";
    public static final String meta = "meta";
    public static final String exchangeName = "exchangeName";
    public static final String instrumentType = "instrumentType";
    public static final String firstTradeDate = "firstTradeDate";
    public static final String gmtoffset = "gmtoffset";
    public static final String timezone = "timezone";
    public static final String chartPreviousClose = "chartPreviousClose";
    public static final String previousClose = "previousClose";
    public static final String scale = "scale";
    public static final String currentTradingPeriod = "currentTradingPeriod";
    public static final String pre = "pre";
    public static final String start = "start";
    public static final String end = "end";
    public static final String regular = "regular";
    public static final String post = "post";
    public static final String tradingPeriods = "tradingPeriods";
    public static final String dataGranularity = "dataGranularity";
    public static final String validRanges = "validRanges";
    public static final String indicators = "indicators";
    public static final String quote = "quote";
    public static final String error = "error";
}
