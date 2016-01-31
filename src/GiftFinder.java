//=============================================================================
// Finds the gifts using data from Zappos API
//
// Ashutosh Sharma, 1/19/16
//=============================================================================


import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class GiftFinder {
	private int numItems;			//number of items
	private double totalPrice;		//total price of items
	private double maxPrice;		//max feasible price
	private double minPrice;        //optimal minimum price
	private int page;				//page of results
	private JSONArray products;		//products in range
	private ArrayList<Product> productObjects; //JSON->Product list
	private ArrayList<GiftCombos> productCombos; //list of product combos
	private final double TOL = Math.pow(10, -7);  //tolerance for subtracting doubles
	private final int MAXCOMBOS = 10;
	private boolean gotPage = false;
	
	/**
	 * Constructs a new GiftSearcher
	 * @param num The number of items to search for
	 * @param total The maximum total price of combined items
	 */
	public GiftFinder(int num, double total) {
		numItems = num;
		totalPrice = total;
		maxPrice = Integer.MAX_VALUE; 	//will set later
		page = 1;					//will pull at least one page of results
		products = new JSONArray();
		productObjects = new ArrayList<Product>();
		productCombos = new ArrayList<GiftCombos>();
	}
	
	/**
	 * Gets the price from Product JSON
	 * @param item The JSON Object product
	 * @return The price as a double
	 */
	private Double getPrice(Object item){
		return Double.parseDouble(((String) ((JSONObject) item).get("price")).substring(1));
	}
	
	/**
	 * Gets all products that are within the feasible price range
	 * i.e. min price to max=(totalamoun - (numItems-1)*(min price))
	 * @return The JSON array of objects in String format, or "" if (numItems)*(min price)
	 * is greater than the total price
	 * @throws IOException 
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	private void setProductsInRange() throws IOException, ParseException {
		//get maximum amount of products (100), starting at lowest price, and pull out results
		String reply = ParseJSON.httpGet(ParseJSON.BASEURL + "&term=&limit=100&sort={\"price\":\"asc\"}");
		JSONObject replyObject = ParseJSON.parseReply(reply);
		JSONArray resultArray = ParseJSON.getResults(replyObject);
		
		//first product's price
		double firstPrice = getPrice(resultArray.get(0));
		
		//setting optimal Minimum Price
		minPrice = (totalPrice/numItems) - 5;
		if (!(minPrice > 0) ){ minPrice = firstPrice;}
		
		
		//if cheapest n items price > total price, return empty string
		if( (firstPrice * numItems) > totalPrice) {
			products = null;
		}
		
		
		//Assuming maximum price
		//maxPrice = totalPrice - (numItems - 1)*(firstPrice);
		maxPrice = totalPrice - (numItems - 1)*(minPrice);
		
		//increment page
		page++;
		
		//last product's price on the page
		Double lastPrice = getPrice(resultArray.get(resultArray.size() - 1));
		
		//clear resultArray if lasPrice is less than minPrice
		if(lastPrice < minPrice) resultArray.clear();
		
		//get more pages till lastprice is less than maxprice
		JSONObject nextObject = new JSONObject();
		JSONArray nextArray = new JSONArray();
		Double newlastPrice;
		
		while(lastPrice < maxPrice) { 
			String nextPage = ParseJSON.httpGet(ParseJSON.BASEURL + "&term=&limit=100&sort={\"price\":\"asc\"}&page=" + page);
			nextObject = ParseJSON.parseReply(nextPage);
			nextArray = ParseJSON.getResults(nextObject);
			
			//get new last product and price
			newlastPrice = getPrice(nextArray.get(nextArray.size() - 1));
			
			//append new page of results to original array if lastPrice is greater than minPrice
			if (gotPage==true){
				lastPrice = newlastPrice;
				if(lastPrice> minPrice)resultArray.addAll(nextArray);
				page++;
			}
			else{
				if (newlastPrice > minPrice){
					if(!(page==1)){
						page = page/2;
					}
					gotPage = true;
				}
				else{
					lastPrice = newlastPrice;
					page = page*2;
				}
				
			}
			
		}

		//return resultArray.toString();
		products = resultArray;
	}
	
	/**
	 * Converts JSONObjects into Products, puts products in price range in
	 * ArrayList to be sorted and searched
	 */
	private void setSearchableProducts() {
		//add the first (smallest price) object
		productObjects.add(new Product((JSONObject)products.get(0)));
		
		//count how many times a price has already shown up
		int already = 1;
		int numPrices = 1;
		
		//go through the whole 
		for(int i = 1; i < products.size() && getPrice(products.get(i)) < maxPrice; i++) {
			double currentPrice = getPrice(products.get(i));
			if( currentPrice > productObjects.get(numPrices-1).getPrice()) {
				productObjects.add(new Product((JSONObject)products.get(i)));
				numPrices++;
				already = 1;
			} else if(Math.abs(currentPrice - productObjects.get(numPrices-1).getPrice()) < TOL && already < numItems){
				productObjects.add(new Product((JSONObject)products.get(i)));
				numPrices++;
				already++;
			} else {
				while(i < products.size() && Math.abs(currentPrice - productObjects.get(numPrices-1).getPrice()) < TOL) {
					i++;
					currentPrice = getPrice(products.get(i));
				}
				i++;
				already = 0;
			}
		}
	}

	
	/**
	 * Finds the product combinations of numItems items with $1 of the totalPrice
	 * @param productList The list of products to search
	 * @param target The target to get near
	 * @param partial The list of prices so far
	 */
	private void setProductCombosRecursive(ArrayList<Product> productList, double target, ArrayList<Product> partial) {
		int priceWithinAmount = 1;
		
		//if partial size > numItems, you already have too many items, so stop
		if(partial.size() > numItems) { return; }
		
		double sum = 0;
		for(Product x : partial) sum += x.getPrice();
		
		//if sum is within $1 of target, and partial size is numItems, and you don't already have too many product 
		//combos, then add another product combo
		if(Math.abs(sum - target) < priceWithinAmount && partial.size() == numItems && productCombos.size() < MAXCOMBOS) {
			//if no price combos yet, just add it on
			if(productCombos.size() == 0) {	
				productCombos.add(new GiftCombos(partial, totalPrice)); 
			}
			//else, check it against the most recent product combo to make sure you're not repeating
			else{
				GiftCombos testerCombo = productCombos.get(productCombos.size() -1);
				GiftCombos partialCombo = new GiftCombos(partial, totalPrice);
				if(!partialCombo.equals(testerCombo)) {
					productCombos.add(partialCombo);
				}
			}
		}
		//if sum is at or within $1 of target, then stop - done!
		if(sum >= target + priceWithinAmount) {
			return;
		}
		
		//otherwise, recursively continue adding another product to combo and test it
		for(int i = 0; i < productList.size() && !(partial.size() == numItems && sum < target); i++){
			ArrayList<Product> remaining = new ArrayList<Product>();
			Product n = productList.get(i);
			for(int j=i+1; j < productList.size(); j++) {remaining.add(productList.get(j)); }
			ArrayList<Product> partial_rec = new ArrayList<Product>(partial);
			partial_rec.add(n);
			setProductCombosRecursive(remaining, target, partial_rec);
		}
	}
	
	/**
	 * Sorts product combinations based on closeness to totalPrice 
	 */
	@SuppressWarnings("unchecked")
	private void sortProductCombos() {
		Collections.sort(productCombos);
	}
	
	/**
	 * Returns the gift combinations that are closest to the total dollar amount
	 * @throws IOException
	 * @throws ParseException
	 */
	public String getGifts() throws IOException, ParseException {
		//get products from API
		System.out.println("Getting products");
		this.setProductsInRange();
		
		//convert to Products
		System.out.println("Setting Searchable Products");
		this.setSearchableProducts();
	
		//find combinations
		System.out.println("Getting Combos");
		this.setProductCombosRecursive(productObjects, totalPrice, new ArrayList<Product>());
		
		//sort by how close they are to given total
		System.out.println("Sorting");
		this.sortProductCombos();
		
		//return results
		if(productCombos.size() != 0) {
			String toPrint = "<table>";
			for(GiftCombos x:productCombos) {
				toPrint += x.toString() + "<br>";
			}
			toPrint += "</table>";
			return toPrint;
		}
		else {
			return "You can't get soooooo many things for that amount. <br> " +
					"Please try with fewer items or spend some more dollars.";
		}
	}
	
}