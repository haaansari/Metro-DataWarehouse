import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Calendar;

public class MeshJoin {
	private static Integer num_transactions= 50;
	private static Integer num_partitions = 10;
	public static ArrayList<ArrayList<String>> Queue = new ArrayList<>(num_partitions);
	public static Integer num_rows_prod;
	public static Integer num_rows_customer;
	public static Integer num_rows_trasaction;
	public static Map<String, ArrayList<TransactionFact>> HashMap = new HashMap<String, ArrayList<TransactionFact>>();
	
	public static Integer countProdRows(Connection con) throws SQLException{
        String sql = "Select count(*) from Products;";
        PreparedStatement p_obj = con.prepareStatement(sql);
        ResultSet q_result = p_obj.executeQuery();
        Integer num_Rows_Prod = 0;
        while(q_result.next()){
            num_Rows_Prod = q_result.getInt(1);
        }
        return num_Rows_Prod;
    }
	
	public static Integer countCustomerRows(Connection con) throws SQLException{
        String sql = "Select count(*) from Customers;";
        PreparedStatement p_obj = con.prepareStatement(sql);
        ResultSet q_result = p_obj.executeQuery();
        Integer num_Rows_Customer = 0;
        while(q_result.next()){
            num_Rows_Customer = q_result.getInt(1);
        }
        return num_Rows_Customer;
    }
	
    public static Integer countTransactionRows(Connection con) throws SQLException{
        String sql = "Select count(*) from transactions;";
        PreparedStatement p = con.prepareStatement(sql);
        ResultSet q_result = p.executeQuery();
        Integer num_Rows = 0;
        while(q_result.next()){
            num_Rows = q_result.getInt(1);
        }
        return num_Rows;
    }
    
    public static Boolean fetchTransactions(Connection con, Integer value) throws SQLException {
        Statement statement = null;
        try {
            statement = con.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            //SQL Query to fetch transactions
            String sql = "Select * from Transactions Limit 50 offset ?;";
            PreparedStatement preparedStmt = con.prepareStatement(sql);
            preparedStmt.setInt(1, value);

            ResultSet rs = preparedStmt.executeQuery();
            Integer TRANSACTION_ID;
            String PRODUCT_ID = "";
            String CUSTOMER_ID = "";
            String CUSTOMER_NAME = "";
            String STORE_ID = "";
            String STORE_NAME = "";
            Date T_DATE;
            Double QUANTITY;
            // JoinKeys - the list that contains all Product ID pointers in an element in the Queue
            ArrayList<String> JoinKeys = new ArrayList<>();
            while (rs.next()) {      //Get the Transactions from the transactions table
                TRANSACTION_ID = rs.getInt(1);
                PRODUCT_ID = rs.getString(2);
                CUSTOMER_ID = rs.getString(3);
                CUSTOMER_NAME = rs.getString(4);
                STORE_ID = rs.getString("Store_Id");
                STORE_NAME = rs.getString(6);
                T_DATE = rs.getDate(7);
                QUANTITY = rs.getDouble(8);
                TransactionFact tData = new TransactionFact(TRANSACTION_ID, PRODUCT_ID, CUSTOMER_ID,
                        CUSTOMER_NAME, STORE_ID, STORE_NAME, T_DATE, QUANTITY);

                // Insert key if it does not already exist
                if (HashMap.get(PRODUCT_ID) == null) {
                	HashMap.put(PRODUCT_ID, new ArrayList<TransactionFact>());
                }
                // Add the new transaction into the multiValueMap
                HashMap.get(PRODUCT_ID).add(tData);

                // Add the respective Product IDs
                JoinKeys.add(PRODUCT_ID);
            }
            // Add to the Queue
            Queue.add(JoinKeys);
            return Boolean.TRUE;
        }
        catch (SQLException throwables){
            return Boolean.FALSE;
        }
    }
    
    
    
    
	
    public static ArrayList<ProductData> fetchProductData (Connection con, Integer offset) throws SQLException {
        Statement stat = null;
        try {
            stat = con.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //SQL Query to fetch master data
        String sql = "Select * from products Limit 10 offset ?;";
        PreparedStatement preparedStmt = con.prepareStatement(sql);
        preparedStmt.setInt(1, offset);
        ResultSet rs = preparedStmt.executeQuery();
        String PRODUCT_ID;
        String PRODUCT_NAME;
        String SUPPLIER_ID;
        String SUPPLIER_NAME;
        Double PRICE;
        ArrayList<ProductData> PD = new ArrayList<ProductData>(num_partitions);
        while (rs.next()) {
            PRODUCT_ID =rs.getString(1);
            PRODUCT_NAME = rs.getString(2);
            SUPPLIER_ID = rs.getString(3);
            SUPPLIER_NAME =rs.getString(4);
            PRICE =rs.getDouble(5);
            ProductData r = new ProductData(PRODUCT_ID,PRODUCT_NAME,SUPPLIER_ID,SUPPLIER_NAME,PRICE);
            PD.add(r);
        }
        return PD;
    }
    
    public static ArrayList<CustomerData> fetchCustomerData (Connection con, Integer offset) throws SQLException {
        Statement stat = null;
        try {
            stat = con.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //SQL Query to fetch master data
        String sql = "Select * from customers Limit 10 offset ?;";
        PreparedStatement preparedStmt = con.prepareStatement(sql);
        preparedStmt.setInt(1, offset);
        ResultSet rs = preparedStmt.executeQuery();
        String CUSTOMER_ID;
        String CUSTOMER_NAME;
        ArrayList<CustomerData> CD = new ArrayList<CustomerData>(num_partitions);
        while (rs.next()) {
            CUSTOMER_ID =rs.getString(1);
            CUSTOMER_NAME = rs.getString(2);
            CustomerData r = new CustomerData(CUSTOMER_ID,CUSTOMER_NAME);
            CD.add(r);
        }
        return CD;
    }
    
    
    public static ArrayList<TransactionFact> fetchEntireTransactionDataSet (Connection con) throws SQLException {
        //SQL Query to fetch Customer(master data)
        String sql = "Select * from db.transactions;";
        PreparedStatement p = con.prepareStatement(sql);
        ResultSet rs = p.executeQuery();
        
        ArrayList<TransactionFact> transactionData = new ArrayList<TransactionFact>();
        while (rs.next()) {
        	Integer Transaction_id = rs.getInt("Transaction_id");
        	String Product_id = rs.getString("Product_id");
        	String Customer_id = rs.getString("Customer_id");
        	String Store_id = rs.getString("Store_id");
        	String Store_name = rs.getString("Store_name");
        	String Time_id = rs.getString("Time_id");
        	Date T_date = rs.getDate("T_date");
        	Double quantity = rs.getDouble("quantity");
            TransactionFact r = new TransactionFact(Transaction_id, Product_id, Customer_id, Store_id,Store_name, Time_id, T_date, quantity);
            transactionData.add(r);
        }
        return transactionData;
    }

    
    //Function to fetch Customer
    public static ArrayList<CustomerData> fetchEntireCustomerDataSet (Connection con) throws SQLException {
        //SQL Query to fetch Customer(master data)
        String sql = "Select * from db.customers;";
        PreparedStatement p = con.prepareStatement(sql);
        ResultSet rs = p.executeQuery();
        
        String Customer_id = "";
    	String Customer_name = "";
        ArrayList<CustomerData> customerData = new ArrayList<CustomerData>();
        while (rs.next()) {
        	Customer_id = rs.getString("Customer_id");
        	Customer_name = rs.getString("Customer_name");
            CustomerData r = new CustomerData(Customer_id, Customer_name);
            customerData.add(r);
        }
        return customerData;
    }

    //Function to fetch Product
    public static ArrayList<ProductData> fetchEntireProductData (Connection con) throws SQLException {
        //SQL Query to fetch Product(master data)
        String sql = "Select * from db.products;";
        PreparedStatement p = con.prepareStatement(sql);
        ResultSet rs = p.executeQuery();
        
        String Product_id = ""; 
    	String Product_name = ""; 
    	String Supplier_id = ""; 
    	String Supplier_name = ""; 
    	Double price = 0.0;
        ArrayList<ProductData> productData = new ArrayList<ProductData>();
        while (rs.next()) {
        	Product_id = rs.getString("Product_id");
        	Product_name = rs.getString("Product_name");
        	Supplier_id = rs.getString("Supplier_id");
        	Supplier_name = rs.getString("Supplier_name");
        	price = rs.getDouble("price");
            ProductData r = new ProductData(Product_id, Product_name, Supplier_id,Supplier_name, price );
            productData.add(r);
        }
        return productData;
    }
    
    
    
    
    public static void updateToDWH(Connection con, TransactionFact TF, CustomerData CD, ProductData PD) throws SQLException {
    	
    	String sql_query = "Select * from metrodwh.Transactions_Fact where Transaction_Id = ?";
    	PreparedStatement p= con.prepareStatement(sql_query);
        p.setInt(1, TF.TRANSACTION_ID);
        ResultSet r = p.executeQuery();
        if(r.next()) {
        	return;
        }
    	
        String sql_store = "Select * from metrodwh.stores where Store_ID = ? ;";
        PreparedStatement preparedStmt = con.prepareStatement(sql_store);
        preparedStmt.setString(1, TF.STORE_ID);
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println(TF.STORE_ID);

        // Not found (Store ID)
        if (!rs.next()){
            String insert_store = "Insert into metrodwh.stores Values(?,?)";
            PreparedStatement ps = con.prepareStatement(insert_store);
            ps.setString(1,TF.STORE_ID);
            ps.setString(2,TF.STORE_NAME);
            ps.execute();
        }

        // Search if the Customer ID exists in the DWH
        String sql_customer = "Select * from metrodwh.customers where CUSTOMER_ID = ?";
        PreparedStatement preparedStmt2 = con.prepareStatement(sql_customer);
        preparedStmt2.setString(1, TF.CUSTOMER_ID);
        ResultSet rs2 = preparedStmt2.executeQuery();
        if (!rs2.next()){
            String insert_customer = "Insert into metrodwh.customers Values(?,?);";
            PreparedStatement ps2 = con.prepareStatement(insert_customer);
            ps2.setString(1, TF.CUSTOMER_ID);
            ps2.setString(2,CD.CUSTOMER_NAME);
            ps2.execute();
        }

        // Search if the Supplier ID exists in the DWH
        String sql_supplier = "Select * from metrodwh.suppliers where SupplierID = ?";
        PreparedStatement preparedStmt3 = con.prepareStatement(sql_supplier);
        preparedStmt3.setString(1, TF.SUPPLIER_ID);
        ResultSet rs3 = preparedStmt3.executeQuery();
        if (!rs3.next()){
            String insert_supplier = "Insert into metrodwh.suppliers Values(?,?);";
            PreparedStatement ps3 = con.prepareStatement(insert_supplier);
            ps3.setString(1, TF.SUPPLIER_ID);
            ps3.setString(2, PD.SUPPLIER_NAME);
            ps3.execute();
        }
        

        // Search if the Supplier ID exists in the DWH
        String sql_product = "Select * from metrodwh.products where Product_ID = ?";
        PreparedStatement preparedStmt4 = con.prepareStatement(sql_product);
        preparedStmt4.setString(1, TF.PRODUCT_ID);
        ResultSet result4 = preparedStmt4.executeQuery();
        System.out.println("Product Id is:"+TF.PRODUCT_ID);
        if (!result4.next()){
            String insert_product = "Insert into metrodwh.products Values(?,?,?);";
            PreparedStatement prepStat4 = con.prepareStatement(insert_product);
            prepStat4.setString(1, TF.PRODUCT_ID);
            prepStat4.setString(2, PD.PRODUCT_NAME);
            prepStat4.setDouble(3, PD.PRICE);
            prepStat4.execute();
        }
        
        System.out.println("Done with Product");

        // Search if Date exists in the DWH
        String sql_date = "Select * from metrodwh.dates where TIME_ID = ?";
        PreparedStatement preparedStmt5 = con.prepareStatement(sql_date);
        int mon = TF.T_DATE.getMonth() + 1;
        int quar;
        if (mon <=3){
            quar = 1;
        }
        else if(mon>3 && mon <=6)
        {
            quar = 2;
        }
        else if(mon>6 && mon<=9)
        {
            quar = 3;
        }
        else
        {
            quar = 4;
        }
        java.sql.Date sqlDate = new java.sql.Date(TF.T_DATE.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sqlDate);
        int year = calendar.get(Calendar.YEAR);
        int date = calendar.get(Calendar.DATE);
        // Find the day of the week
        String dayWeek = new SimpleDateFormat("EEEE").format(sqlDate);
        preparedStmt5.setDate(1, sqlDate);
        System.out.println("The sdate is"+sqlDate);

        ResultSet result5 = preparedStmt5.executeQuery();
        if (result5.next()==false){
            String insert_date = "Insert into metrodwh.dates Values(?,?,?,?,?,?);";
            PreparedStatement prepStat5 = con.prepareStatement(insert_date);
            prepStat5.setDate(1, sqlDate);
            prepStat5.setString(2, dayWeek);
            prepStat5.setInt(3, date);
            prepStat5.setInt(4, mon);
            prepStat5.setInt(5, quar);
            prepStat5.setInt(6, year);
            prepStat5.execute();
        }

        // INSERT INTO FACT TABLE
        String sql_trans = "INSERT into metrodwh.Transactions_Fact VALUES(?,?,?,?,?,?,?,?);";
        PreparedStatement prepStat6 = con.prepareStatement(sql_trans);
        prepStat6.setInt(1,TF.TRANSACTION_ID);
        prepStat6.setString(2, TF.CUSTOMER_ID);
        prepStat6.setString(3, TF.PRODUCT_ID);
        prepStat6.setString(4, TF.STORE_ID);
        prepStat6.setString(5, TF.SUPPLIER_ID );
        prepStat6.setDate(6, sqlDate);
        prepStat6.setDouble(7, TF.TOTAL_SALE);
        prepStat6.setDouble(8, TF.QUANTITY);
        prepStat6.execute();

    }
    
    //Function to remove records in Hash Table
    public static void EmptyHashTable(){
        ArrayList<String> head = Queue.remove(0);
        for(String val : head){
            Iterator iterator = HashMap.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry Entry_list_tuples = (Map.Entry)iterator.next();
                String product_id = (String)Entry_list_tuples.getKey();
                if (product_id.equals(val)==true) {
                    ArrayList<TransactionFact> ListOfProductTuples = (ArrayList<TransactionFact>) Entry_list_tuples.getValue();
                    ArrayList<TransactionFact> tupleListCopy = new ArrayList<TransactionFact>(ListOfProductTuples);
                    for (TransactionFact tD : ListOfProductTuples) {
                        if (tD.TRANSACTION_ID != null && tD.PRODUCT_ID != null && tD.CUSTOMER_ID != null && tD.STORE_ID != null && tD.T_DATE != null && tD.QUANTITY != null &&tD.SUPPLIER_ID != null && tD.SUPPLIER_NAME != null && tD.TOTAL_SALE != null) {
                            tupleListCopy.remove(tD);
                        }
                    }
                    HashMap.put(product_id, tupleListCopy);
                }
            }

        }

    }
    
    
    
    public static Boolean HashTable(Connection con, ArrayList<CustomerData> customerArray, ArrayList<ProductData> productArray) throws SQLException {
        // Getting an iterator
        Iterator Iter = HashMap.entrySet().iterator();
        while(Iter.hasNext() == true){
        	Map.Entry Iter2 = (Map.Entry)Iter.next();
        	String PRODUCT_ID = (String)Iter2.getKey();
        	for(ProductData PD: productArray) {
    			if(PD.PRODUCT_ID.equals(PRODUCT_ID)==true) {
    				ArrayList<TransactionFact> tupleListTransaction = (ArrayList<TransactionFact>)Iter2.getValue();
    				ArrayList<TransactionFact> tupleListNewTransaction = new ArrayList<TransactionFact>(tupleListTransaction);
    				for(TransactionFact TD: tupleListTransaction ){
    					int ind;
                        ind = tupleListTransaction.indexOf(TD);
                        TD.PRODUCT_ID = PD.PRODUCT_ID;
                        TD.SUPPLIER_ID = PD.SUPPLIER_ID;
                        TD.TOTAL_SALE = TD.QUANTITY * PD.PRICE;
                        for(CustomerData CD: customerArray) {
                        	if(TD.CUSTOMER_ID.equals(CD.CUSTOMER_ID)==true) {
                        		tupleListNewTransaction.set(ind, TD);
                        		updateToDWH(con, TD, CD, PD);
                        	}
                        }
                    }
    			}
    		}
        	
        }
        System.out.println("Done");
        return Boolean.TRUE;
    }
  
	public static void main(String[] args)throws SQLException {
        Integer DBF1 = 0;
        Integer DBF2 = 0;
		Integer val = 0;
        Integer countremove = 0;
        Integer i = 0;
		
		DBConnection DB = DBConnection.getInstance("db");
		
		ArrayList<TransactionFact> trans_complete= fetchEntireTransactionDataSet(DB.con);
		ArrayList<CustomerData> customer_complete= fetchEntireCustomerDataSet(DB.con);
		ArrayList<ProductData> product_complete= fetchEntireProductData(DB.con);
		num_rows_prod = countProdRows(DB.con); 
		num_rows_customer = countCustomerRows(DB.con);
		num_rows_trasaction = countTransactionRows(DB.con);
		
        while(Queue.isEmpty()==false || i==0){
            if(val <= num_rows_trasaction) {
                // Fetch the 50 tuples from Transaction table
                Boolean boolFetched = fetchTransactions(DB.con, val);
                val += num_rows_trasaction;
            }
            ArrayList<CustomerData> customerArray = fetchCustomerData(DB.con, DBF1);
            DBF1 = DBF1 + num_partitions;
            
            ArrayList<ProductData> productArray = fetchProductData(DB.con, DBF1);
            DBF1 = DBF1 + num_partitions;
            /*Boolean updated =*/ HashTable(DB.con, customer_complete, product_complete);
            System.out.print("Done with iteration of creating a lookup hash table");
            
            if (countremove == 1 || DBF1 == num_rows_customer || DBF2 == num_rows_prod ){
                countremove = 1;
                if (DBF1 == num_rows_customer){
                    DBF1 = 0;
                }
                if (DBF2 == num_rows_prod){
                    DBF2 = 0;
                }

                // Delete functionality
                EmptyHashTable();
            }
            if (val % 1000 == 0) {
            	System.out.println("Please wait... Inserted: " + val );
            }
        }
        i+=1;
	}
}