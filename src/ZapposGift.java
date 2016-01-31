
//=============================================================================
// User Interface for Zappos Gift Search
//
// Ashutosh Sharma, 1/19/16
//=============================================================================

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;

import org.json.simple.parser.ParseException;

//import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

public class ZapposGift {

	private JFrame frame;
	private JTextField products;
	private JTextField amount;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ZapposGift window = new ZapposGift();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ZapposGift() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 823, 601);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		products = new JTextField();
		products.setBounds(20, 39, 86, 20);
		frame.getContentPane().add(products);
		products.setColumns(10);

		amount = new JTextField();
		amount.setBounds(139, 39, 86, 20);
		frame.getContentPane().add(amount);
		amount.setColumns(10);

		JLabel lblNewLabel = new JLabel("# of Products");
		lblNewLabel.setBounds(20, 14, 86, 14);
		frame.getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Dollar Amount");
		lblNewLabel_1.setBounds(139, 14, 86, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 84, 797, 478);
		frame.getContentPane().add(scrollPane);
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		scrollPane.setViewportView(editorPane);

		JButton btnNewButton = new JButton("Find");

		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String htm;
				
				if (products.getText().equals("") || amount.getText().equals("")) {
					htm = "<h1> Enter valid number of Products or amount</h1>";
					editorPane.setText(htm);
				} else {
					htm = giftSearch(Integer.parseInt(products.getText()),
							Integer.parseInt(amount.getText()));
					editorPane.setText(htm);
				}
			}
		});
		btnNewButton.setBounds(252, 10, 89, 49);
		frame.getContentPane().add(btnNewButton);
		
		
		
		

	}

	public String giftSearch(int numItemsArg, int totalPriceArg) {

		// set the number of items and amount
		int numItems = numItemsArg;
		double totalPrice = totalPriceArg;
		String result;

		// check to make sure number of items is valid
		if (numItems < 1) {
			result = "Number of items must be greater than 0.";
			return result;
		}
		// check to make sure total price is valid
		else if (totalPrice <= 0) {
			result = "Total price must be greater than 0.";
			return result;
		}

		// run search
		try {
			GiftFinder finder = new GiftFinder(numItems, totalPrice);
			result = finder.getGifts();
			return result;

		} catch (ParseException e) {
			// parsed incorrectly
			result = "Something went wrong.";
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			// Bad Request
			result = "Bad Request.";
			e.printStackTrace();
			return result;

		}
	}
}
