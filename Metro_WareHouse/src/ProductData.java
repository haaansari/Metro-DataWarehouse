
public class ProductData {
	String PRODUCT_ID;
    String PRODUCT_NAME;
    String SUPPLIER_ID;
    String SUPPLIER_NAME;
    Double PRICE;

    public ProductData(String ID, String NAME, String S_ID, String S_NAME, Double P) {
        PRODUCT_ID = ID;
        PRODUCT_NAME = NAME;
        SUPPLIER_ID = S_ID;
        SUPPLIER_NAME = S_NAME;
        PRICE = P;
    }

}
