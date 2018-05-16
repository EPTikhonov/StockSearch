import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;

public class StockSearch_Display  {

	JFrame frame;
	private StockSearch_Data data = new StockSearch_Data();
	private String tickerSymbol;

	public StockSearch_Display() throws IOException {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("StockSearch - Get Quotes This Instant");
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();

		JTextField txtEnterTickerSymbol = new JTextField();
		txtEnterTickerSymbol.setToolTipText("");
		txtEnterTickerSymbol.setBounds(67, 21, 302, 20);
		txtEnterTickerSymbol.setColumns(10);

		JLabel lblTime = new JLabel(data.time);
		lblTime.setFont(new Font("Arial", Font.PLAIN, 10));
		lblTime.setBounds(67, 98, 228, 14);
		frame.getContentPane().add(lblTime);

		JLabel lblNewLabel = new JLabel(data.companyName);
		lblNewLabel.setBounds(67, 60, 228, 14);
		frame.getContentPane().add(lblNewLabel);

		JLabel lblLastPrice = new JLabel(data.lastPrice);
		lblLastPrice.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblLastPrice.setBounds(67, 85, 65, 14);
		frame.getContentPane().add(lblLastPrice);

		JLabel lblDow = new JLabel("");
		lblDow.setHorizontalAlignment(SwingConstants.CENTER);
		lblDow.setBounds(56, 222, 51, 20);
		frame.getContentPane().add(lblDow);

		JLabel lblSp = new JLabel("");
		lblSp.setHorizontalAlignment(SwingConstants.CENTER);
		lblSp.setBounds(186, 222, 51, 20);
		frame.getContentPane().add(lblSp);

		JLabel lblRussell = new JLabel("");
		lblRussell.setHorizontalAlignment(SwingConstants.CENTER);
		lblRussell.setBounds(287, 222, 65, 20);
		frame.getContentPane().add(lblRussell);

		JLabel lblHigh = new JLabel(data.high);
		lblHigh.setHorizontalAlignment(SwingConstants.CENTER);
		lblHigh.setBounds(103, 123, 77, 19);
		frame.getContentPane().add(lblHigh);

		JLabel lblLow = new JLabel(data.low);
		lblLow.setHorizontalAlignment(SwingConstants.CENTER);
		lblLow.setBounds(103, 150, 77, 23);
		frame.getContentPane().add(lblLow);

		JLabel lblMarketCap = new JLabel(data.marketCap);
		lblMarketCap.setHorizontalAlignment(SwingConstants.CENTER);
		lblMarketCap.setBounds(295, 119, 77, 23);
		frame.getContentPane().add(lblMarketCap);

		JLabel lblPeRatio = new JLabel(data.peRatio);
		lblPeRatio.setHorizontalAlignment(SwingConstants.CENTER);
		lblPeRatio.setBounds(295, 150, 77, 23);
		frame.getContentPane().add(lblPeRatio);

		JLabel lblChange = new JLabel(data.change);
		lblChange.setHorizontalAlignment(SwingConstants.CENTER);
		lblChange.setBounds(123, 85, 57, 14);
		frame.getContentPane().add(lblChange);

		JLabel lblChangepercentage = new JLabel("");
		lblChangepercentage.setHorizontalAlignment(SwingConstants.CENTER);
		lblChangepercentage.setBounds(173, 85, 57, 14);
		frame.getContentPane().add(lblChangepercentage);

		txtEnterTickerSymbol.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tickerSymbol = txtEnterTickerSymbol.getText(); // converts input into String
				txtEnterTickerSymbol.setText(null); // clears search bar

				try {
					// getting individual stock information from input
					data.connectStockToAPI(data.getTickerURL(tickerSymbol));

					// getting information of indexes
					data.getIndexInfo();

					// setting values
					lblTime.setText(data.time);
					lblNewLabel.setText(data.companyName);
					lblLastPrice.setText(data.lastPrice);
					lblDow.setText(data.dowJonesPercentChange+"%");
					lblSp.setText(data.standardAndPoorsPercentChange+"%");
					lblRussell.setText(data.russell2000PercentChange+"%");
					lblHigh.setText(data.high);
					lblLow.setText(data.low);
					lblMarketCap.setText(data.marketCap);
					lblPeRatio.setText(data.peRatio);
					lblChange.setText(data.change);
					lblChangepercentage.setText("("+data.stockChangeAsPercentage+"%)");

				} catch (IOException c) {
					c.printStackTrace();
				}

			}
		});

		frame.getContentPane().add(panel);
		frame.getContentPane().add(txtEnterTickerSymbol);

		JTextPane txtpnHigh = new JTextPane();
		txtpnHigh.setText("High:");
		txtpnHigh.setBounds(67, 123, 40, 20);
		frame.getContentPane().add(txtpnHigh);

		JTextPane txtpnLow = new JTextPane();
		txtpnLow.setText("Low:");
		txtpnLow.setBounds(67, 153, 40, 20);
		frame.getContentPane().add(txtpnLow);

		JTextPane txtpnMarketCap = new JTextPane();
		txtpnMarketCap.setText("Market Cap:");
		txtpnMarketCap.setBounds(216, 123, 76, 20);
		frame.getContentPane().add(txtpnMarketCap);

		JTextPane txtpnPeRatio = new JTextPane();
		txtpnPeRatio.setText("P/E Ratio:");
		txtpnPeRatio.setBounds(216, 153, 65, 20);
		frame.getContentPane().add(txtpnPeRatio);

		JTextPane txtpnDow = new JTextPane();
		txtpnDow.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtpnDow.setText("Dow");
		txtpnDow.setBounds(67, 199, 31, 20);
		frame.getContentPane().add(txtpnDow);

		JTextPane txtpnSp = new JTextPane();
		txtpnSp.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtpnSp.setText("S&P 500");
		txtpnSp.setBounds(186, 204, 57, 20);
		frame.getContentPane().add(txtpnSp);

		JTextPane txtpnRussell = new JTextPane();
		txtpnRussell.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtpnRussell.setText("Russell 2000");
		txtpnRussell.setBounds(293, 204, 76, 20);
		frame.getContentPane().add(txtpnRussell);

		JTextPane txtpnIndexes = new JTextPane();
		txtpnIndexes.setText("INDEXES");
		txtpnIndexes.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtpnIndexes.setBounds(186, 183, 57, 20);
		frame.getContentPane().add(txtpnIndexes);

	}
}
