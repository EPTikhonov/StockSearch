import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import javax.net.ssl.HttpsURLConnection;

public class StockSearch_Data {

	String time = "";
	String companyName = "";
	String lastPrice = "";
	String high = "";
	String low = "";
	String marketCap = "";
	String peRatio = "";
	String change = "";
	String stockChangeAsPercentage = "";
	String stockLogoURL = "";

	final String DOW_TICKER = "DIA";
	final String SANDP500_TICKER = "SPY";
	final String RUSSELL2000_TICKER = "IWM";
	String dowJonesPercentChange = "";
	String standardAndPoorsPercentChange = "";
	String russell2000PercentChange = "";
	String indexChangeAsPercentage = "";
	DecimalFormat decimalForm = new DecimalFormat("#.##");

	public StockSearch_Data() {

	}

	// get stock/index url for connection to API
	public String getTickerURL(String symbolInput) {
		String tickerURL = "https://api.iextrading.com/1.0/stock/"+symbolInput+"/batch?types=quote";

		return tickerURL;
	}

	// connecting stock ticker to IEXTrading API and getting information
	public void connectStockToAPI(String urlLink) throws IOException {
		try {
			URL url = new URL(urlLink);
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.setRequestMethod("GET");
			InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
			BufferedReader buff = new BufferedReader(inStream);
			String line = buff.readLine();

			while(line != null) {
				getCurrentTime(line);
				getMarketStatus(line);
				getCompanyName(line);
				getLastPrice(line);
				getChange(line);
				getStockChangeAsPercentage(line);
				getHighPrice(line);
				getLowPrice(line);
				getMarketCap(line);
				getPERatio(line);

				//System.out.println(line); // prints entire stock info
				line = buff.readLine();
			}
			inStream.close();
			urlConn.disconnect();

		} catch (IOException e) {
			System.out.println("ticker symbol was not found");
		}
	}

	// connecting index ticker to IEXTrading API and getting information
	public String connectIndexToAPI(String urlLink) throws IOException {
		String tempChangePercentage = "";
		try {
			URL url = new URL(urlLink);
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.setRequestMethod("GET");
			InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
			BufferedReader buff = new BufferedReader(inStream);
			String line = buff.readLine();

			while(line != null) {
				tempChangePercentage = getIndexChangeAsPercentage(line);

				//System.out.println(line); // prints entire stock info
				line = buff.readLine();
			}
			inStream.close();
			urlConn.disconnect();
		} catch (IOException e) {
			System.out.println("indexes were not found");
		}

		return tempChangePercentage;
	}

	// getting indexChangeAsPercentage value for each index
	public void getIndexInfo() throws IOException {
		dowJonesPercentChange = connectIndexToAPI(getTickerURL(DOW_TICKER));
		standardAndPoorsPercentChange = connectIndexToAPI(getTickerURL(SANDP500_TICKER));
		russell2000PercentChange = connectIndexToAPI(getTickerURL(RUSSELL2000_TICKER));

	}

	// getting current time
	public void getCurrentTime(String line) {
		if (line.contains("\"latestTime\"")) {
			int target = line.indexOf("\"latestTime\"");
			int decimal = line.indexOf(",", target) - 1;
			int start = decimal - 11;
			while(line.charAt(start) != ':') {
				start--;
			}
			time = line.substring(start + 2, decimal) + " EDT"; // format when open: 2:59:23 PM EDT
		}
	}

	// getting market status (open/closed)
	public String getMarketStatus(String line) {
		if (line.contains("\"latestSource\"")) {
			int target = line.indexOf("\"latestSource\"");
			int decimal = line.indexOf(",", target);
			int start = decimal;
			while(line.charAt(start) != ':') {
				start--;
			}
			String temp = line.substring(start + 2, decimal - 1);
			String closed = "Close";
			String previousClose = "Previous close";

			// if market closed then set to "Market Closed"
			if (temp.equals(closed) || temp.equals(previousClose)) {
				time = "Market Closed";
			}
		}
		return time;
	}

	// getting company name
	public void getCompanyName(String line) {
		if (line.contains("\"companyName\"")) {
			int target = line.indexOf("\"companyName\"");
			int decimal = line.indexOf(",", target);
			int start = decimal;
			while(line.charAt(start) != ':') {
				start--;
			}
			companyName = line.substring(start + 2, decimal - 1);
		}
	}


	// getting last price
	public void getLastPrice(String line) {
		if (line.contains("\"latestPrice\"")) {
			int target = line.indexOf("\"latestPrice\"");
			int decimal = line.indexOf(",", target);
			int start = decimal;
			while(line.charAt(start) != ':') {
				start--;
			}
			lastPrice = line.substring(start + 1, decimal);
		}
	}

	// getting change
	public void getChange(String line) {
		if (line.contains("\"change\"")) {
			int target = line.indexOf("\"change\"");
			int decimal = line.indexOf(",", target);
			int start = decimal;
			while(line.charAt(start) != ':') {
				start--;
			}
			change = line.substring(start + 1, decimal);
		}
	}

	// getting stock change as percent
	public void getStockChangeAsPercentage(String line) {
		decimalForm.setRoundingMode(RoundingMode.FLOOR);
		if (line.contains("\"changePercent\"")) {
			int target = line.indexOf("\"changePercent\"");
			int decimal = line.indexOf(",", target);
			int start = decimal;
			while(line.charAt(start) != ':') {
				start--;
			}
			String temp = line.substring(start + 1, decimal);
			stockChangeAsPercentage = decimalForm.format(Double.parseDouble(temp) * 100);
		}
	}

	// getting index change as percent
	public String getIndexChangeAsPercentage(String line) {
		decimalForm.setRoundingMode(RoundingMode.FLOOR);
		if (line.contains("\"changePercent\"")) {
			//indexChangeAsPercentage = "";
			int target = line.indexOf("\"changePercent\"");
			int decimal = line.indexOf(",", target);
			int start = decimal;
			while(line.charAt(start) != ':') {
				start--;
			}
			String temp = line.substring(start + 1, decimal);
			indexChangeAsPercentage = decimalForm.format(Double.parseDouble(temp) * 100);
		}
		return indexChangeAsPercentage;
	}

	// getting high price
	public void getHighPrice(String line) {
		if (line.contains("\"high\"")) {
			int target = line.indexOf("\"high\"");
			int decimal = line.indexOf(",", target);
			int start = decimal;
			while(line.charAt(start) != ':') {
				start--;
			}
			high = line.substring(start + 1, decimal);
		}
	}

	// getting low price
	public void getLowPrice(String line) {
		if (line.contains("\"low\"")) {
			int target = line.indexOf("\"low\"");
			int decimal = line.indexOf(",", target);
			int start = decimal;
			while(line.charAt(start) != ':') {
				start--;
			}
			low = line.substring(start + 1, decimal);
		}
	}

	// getting market cap (capitalization)
	public void getMarketCap(String line) {
		// getting market cap (capitalization)
		if (line.contains("\"marketCap\"")) {
			int target = line.indexOf("\"marketCap\"");
			int decimal = line.indexOf(",", target);
			int start = decimal;
			while(line.charAt(start) != ':') {
				start--;
			}

			String temp = line.substring(start + 1, decimal);
			double result = Double.parseDouble(temp);

			/* Checking if market cap is Q, T, B, M, or K and rounding up to 3 decimal places*/

			// if market cap is in quadrillions (Q) (hyperinflation most likely occured)
			if ( (result >= 1000000000000000.0) ) {
				marketCap = Math.ceil((result / 1000000000000000.0) * 1000.0) / 1000.0 + "Q";
			}
			// if market cap is in trillions (T)
			else if ( (result >= 1000000000000.0) ) {
				marketCap = Math.ceil((result / 1000000000000.0) * 1000.0) / 1000.0 + "T";
			} 
			// if market cap is in billions (B)
			else if (result >= 1000000000.0) {
				marketCap = Math.ceil((result / 1000000000.0) * 1000.0 ) / 1000.0 + "B";
			} 
			// if market cap is in millions (M)
			else if (result >= 1000000.0) {
				marketCap = Math.ceil((result / 1000000.0) * 1000.0) / 1000.0 + "M";
			} 
			// if market cap is in thousands (K)
			else if (result >= 1000.0) {
				marketCap = Math.ceil((result / 1000.0) * 1000.0) / 1000.0 + "K";
			} 
			// if market cap is less than 1 thousand or greater than 1 quantillion
			else {
				marketCap = Math.ceil(result * 1000.0) / 1000.0 + "";
			}
		}
	}

	// getting P/E Ratio
	public void getPERatio(String line) {
		if (line.contains("\"peRatio\"")) { // peRatioHigh, peRatioLow
			int target = line.indexOf("\"peRatio\"");
			int decimal = line.indexOf(",", target);
			int start = decimal;
			while(line.charAt(start) != ':') {
				start--;
			}
			peRatio = line.substring(start + 1, decimal);
		}
	}

}
