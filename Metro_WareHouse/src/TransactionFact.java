import java.util.Date;

public class TransactionFact {
	    Integer TRANSACTION_ID;
	    String PRODUCT_ID;
	    String CUSTOMER_ID;
	    String CUSTOMER_NAME;
	    String STORE_ID;
	    String STORE_NAME;
	    Date T_DATE;
	    Double QUANTITY;
	    String PRODUCT_NAME;
	    String SUPPLIER_ID;
	    String SUPPLIER_NAME;
	    Double TOTAL_SALE;

	    public TransactionFact(Integer T_ID, String P_ID, String C_ID, String C_NAME, String S_ID, String S_NAME, Date t_DATE, Double quantity) {
	        TRANSACTION_ID = T_ID;
	        PRODUCT_ID = P_ID;
	        CUSTOMER_ID = C_ID;
	        CUSTOMER_NAME = C_NAME;
	        STORE_ID = S_ID;
	        STORE_NAME = S_NAME;
	        T_DATE = t_DATE;
	        QUANTITY = quantity;
	    }
}
