import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class StockSearch_Data {

	private Scanner input = new Scanner(System.in);
	private String symbolInput = "ticker was not found";
	private String terminateValue = "exit"; // value that ends the program if entered as input

	private String time = "time not found";
	private String companyName = "company not found";
	private String lastPrice = "not found";
	private String high = "not found";
	private String low = "not found";
	private String marketCap = "not found";
	private String peRatio = "not found";
	private String change = "not found";
	private double stockChangeAsPercentage = 0.0;
	private String stockLogoURL = "not found";


	private final String DOW_TICKER = "DIA";
	private final String SANDP500_TICKER = "SPY";
	private final String RUSSELL2000_TICKER = "IWM";
	private double dowJonesPercentChange = 0.0;
	private double standardAndPoorsPercentChange = 0.0;
	private double russell2000PercentChange = 0.0;
	private double indexChangeAsPercentage = 0.0;

	// when class is initialized, methods inside are run automatically
	public StockSearch_Data() throws IOException {
		System.out.println("Enter \"" + terminateValue + "\" to end StockSearch!\n");
		while (true) {
			// getting input and terminates the program if terminateValue is entered
			getTickerInput();
			if (symbolInput.equals(terminateValue)) {
				break;
			} else {

				try {
					// getting individual stock information from input
					connectStockToAPI(getTickerURL(symbolInput));
					printIndividualStockInfo();

					// getting information of indexes
					getIndexInfo();
					printIndexesInfo();
				} catch (IOException e ) {
					//System.out.println("Exception: " + e);
					System.out.println(symbolInput + " was not found\n");
				}

			}
		}
	}

	// get stock symbol/ticker from input
	public String getTickerInput() {
		System.out.print("Enter ticker: ");
		String ticker = input.nextLine();
		symbolInput = ticker;

		return ticker;
	}

	// get stock url for connection to API
	public String getTickerURL(String symbolInput) {
		String tickerURL = "https://api.iextrading.com/1.0/stock/"+symbolInput+"/batch?types=quote";

		return tickerURL;
	}

	// connecting stock ticker to IEXTrading API and getting information
	public void connectStockToAPI(String urlLink) throws IOException {
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
			getStockLogo();
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

	}

	// connecting index ticker to IEXTrading API and getting information
	public double connectIndexToAPI(String urlLink) throws IOException {
		URL url = new URL(urlLink);
		HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
		urlConn.setRequestMethod("GET");
		InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
		BufferedReader buff = new BufferedReader(inStream);
		String line = buff.readLine();
		double tempChangePercentage = 0.0;

		while(line != null) {
			tempChangePercentage = getIndexChangeAsPercentage(line);

			//System.out.println(line); // prints entire stock info
			line = buff.readLine();
		}
		inStream.close();
		urlConn.disconnect();

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
				return time;
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

	// getting stock logo URL
	public void getStockLogo() {
		stockLogoURL = "https://storage.googleapis.com/iex/api/logos/"+symbolInput+".png";
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
		if (line.contains("\"changePercent\"")) {
			int target = line.indexOf("\"changePercent\"");
			int decimal = line.indexOf(",", target);
			int start = decimal;
			while(line.charAt(start) != ':') {
				start--;
			}
			String temp = line.substring(start + 1, decimal);
			stockChangeAsPercentage = Double.parseDouble(temp) * 100;
		}
	}

	// getting index change as percent
	public double getIndexChangeAsPercentage(String line) {
		if (line.contains("\"changePercent\"")) {
			int target = line.indexOf("\"changePercent\"");
			int decimal = line.indexOf(",", target);
			int start = decimal;
			while(line.charAt(start) != ':') {
				start--;
			}
			String temp = line.substring(start + 1, decimal);
			indexChangeAsPercentage = Double.parseDouble(temp) * 100;
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

	// setting stock logo URL
	public void setStockLogo(String ticker) {
		stockLogoURL = "https://storage.googleapis.com/iex/api/logos/"+ticker+".png";
	}

	// printing individual stock information
	public void printIndividualStockInfo() {
		System.out.println(time);
		System.out.println(companyName);
		System.out.println("Last Price: " + lastPrice);
		System.out.printf("Change: %s (%.3f%%)\n",change, stockChangeAsPercentage);
		System.out.println("High: " + high);
		System.out.println("Low: " + low);
		System.out.println("Market Cap: " + marketCap);
		System.out.println("P/E Ratio: " + peRatio);
		System.out.println("Stock Logo: " + stockLogoURL);
	}

	// printing index information (Dow, S&P 500, and Russell 2000)
	public void printIndexesInfo() {
		System.out.println("\n-----{ INDEXES }-----");
		System.out.printf("Dow: %.2f%%\n", dowJonesPercentChange);
		System.out.printf("S&P 500: %.2f%%\n", standardAndPoorsPercentChange);
		System.out.printf("Russell 2000: %.2f%%\n", russell2000PercentChange);
		System.out.println("---------------------\n");
	}
}
