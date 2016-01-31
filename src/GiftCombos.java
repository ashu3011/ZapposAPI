//=============================================================================
// Represents a possible combination of prices
// Makes them sortable by how close they are to the total
//
// Ashutosh Sharma, 1/19/16
//=============================================================================


import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class GiftCombos implements Comparable{
	private ArrayList<Product> comboProducts;		//prices to sort, etc.
	private double sum;							//sum of the prices
	private double idealTotal; 					//total prices should be near
	private double closeness;					//how close sum is to total
	private final double TOL = Math.pow(10, -7);//tolerance for comparing doubles
	
	/**
	 * Constructor
	 * @param pricesForCombo The ArrayList of products 
	 * @param total The total the sum of prices should be near
	 */
	public GiftCombos(ArrayList<Product> productsForCombo, double total) {
		comboProducts = productsForCombo;
		sum = 0;
		idealTotal = total;
		for(Product x:comboProducts) sum += x.getPrice(); 
		closeness = Math.abs(idealTotal - sum);
	}
	
	/**
	 * Returns the price at a given index
	 * @param index Where in list of prices to return
	 * @return The price (double)
	 */
	public double getPrice(int index) {
		return comboProducts.get(index).getPrice();
	}
	
	/**
	 * Returns the sum of the prices
	 * @return Sum (double)
	 */
	public double getSum() {
		return sum;
	}
	
	/**
	 * Gets the length of the comboPrices array
	 * @return How many prices (int)
	 */
	public int getProductComboLength() {
		return comboProducts.size();
	}
	
	/**
	 * Returns how close the sum of prices is to the total
	 * @return abs(total - sum)
	 */
	public double getCloseness() {
		return closeness;
	}
	
	/**
	 * Returns the ideal total for the sum
	 * @return Total (double)
	 */
	public double getTotal() {
		return idealTotal;
	}

	/**
	 * Compares two PriceCombos based on their closeness to the total
	 */
	@Override
	public int compareTo(Object o) {
		GiftCombos other = (GiftCombos) o;
		if(this.equals(other)) return 0;
		else if(this.closeness < other.getCloseness()) return -1;
		else return 1;
	}
	
	/**
	 * Compares each element of PriceCombo to determine equality
	 * @param other
	 * @return
	 */
	public boolean equals(GiftCombos other) {
		if(this.comboProducts.size() != other.getProductComboLength()) {
			return false;
		}
		if(this.idealTotal != other.getTotal()) {
			return false;
		}
		for(int i = 0; i < comboProducts.size(); i++){
			if(Math.abs(this.comboProducts.get(i).getPrice() - other.getPrice(i)) > TOL) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns string representation of the product combinations
	 * @return #: (comboProduct.toString)\n
	 */
	public String toString() {
		String toReturn = "<tr style= 'border: 1px #ccc solid;'><td>Total $" + String.format("%.2f", sum) + ":";
		for(int i = 0; i < comboProducts.size(); i ++) {
			if(i == 0){
				toReturn += comboProducts.get(i).toString();
			}else{
				toReturn += "<td> + " + comboProducts.get(i).toString();
			}
			toReturn += "</tr>";
			
		}
		return toReturn;
	}
	
}
