//=============================================================================
// Represents a single Product
//
// Ashutosh Sharma, 1/19/16
//=============================================================================


import org.json.simple.*;

public class Product {
	private double price;		//product price
	private String name;		//name of product
	private String priceString;	//String representation of price
	private String imageURL;	//String representation of Image URL
	
	/**
	 * Constructs a Product from the JSONObject
	 * @param product The JSONObject provided
	 */
	public Product(JSONObject product) {
		//substring(1) on the price to remove the $
		price = Double.parseDouble(((String) product.get("price")).substring(1));
		name = (String)product.get("productName");
		imageURL = (String)product.get("thumbnailImageUrl");
		
		//format the price string with only 2 numbers after decimal place
		priceString = String.format("%.2f", price);
	}
	
	/**
	 * Returns String representation of product
	 * @return name, $price, (id: id, styleId: styleID)
	 */
	public String toString() {
		return "<td><img height= '50' widht = '50' src='"+ imageURL +"'><td>" + name + ", $" + priceString;
	}
	
	/**
	 * Returns the price of the Product
	 * @return
	 */
	public double getPrice() {
		return price;
	}
}
