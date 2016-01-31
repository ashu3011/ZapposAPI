# ZapposAPI
Gift Search Using Zappos API and Java

Its a simple Java project with ZapposGift.java being the main file. The best way to run it would be to load in IDE and run.

The project uses JSON-Simple. Please add that to a build path in case you get error running it.

The Algorithm is designed to provide Maximum 10 closest gift combinations. The Maximum limit can be set in the code.


Following are the Assumptions Made:

1) Better Results are when all the gifts have nearly equal price. This assumption is to get faster results. Thus, the Algorithm sets the minimum price of product to be considered as Total Price/Number of Products - some weight. This can be changed as required.

2) The maximum Price is set to be Total Price - (Number of Items * Minimum Price) to limit the range of products to consider.

Following steps are taken in the Algorithm:

1) API calls are made to get all products within Min and Max Price decided according to above mentioned criteria.

2) Less API calls are made by doubling the page number each time The maximum price on page is less than the minimum required. Thus logarithmically decreasing the API call for higher amount numbers.

3) All products are stored in Arraylist.

4) All combinations are found by recursively creating them and adding to another arraylist.

5) Once Max number of combinations are found the algorithm stops.

6) The Arraylist is sorted based on closeness which is calcuated in the Gift combination class.

7) The results are displayed in the Jframe.


Possible Improvements:

1) In real scenario a Database can handle the IO faster than arraylist.
2) Using greedy approach while making combination can significantly reduce the time to find all combinations.
3) Map- Reduce can make the task significantly faster.
