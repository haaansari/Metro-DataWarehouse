
-- Q6--
SELECT fact_table.PRODUCT_ID AS ProdID, Pr.PRODUCT_NAME AS ProdName, SUM(Trans_fact.QUANTITY) AS SaleQuant
FROM transactions AS fact_table, product_table AS Prod, date_table AS DOBJ
WHERE fact_table.PRODID = Prod.PRODUCT_ID 
AND fact_table.Time_ID = DOBJ.TIME_ID AND weekday(dobj.Time_ID) IN (1, 7)
GROUP BY fact_table.PRODUCT_ID, Prod.PRODUCT_NAME
ORDER BY SUM(fact_table.QUANTITY) DESC LIMIT 5;

-- Q8--
SELECT Prod.PRODUCT_NAME AS Product_Name,
SUM(CASE WHEN DOBJ.Quarter = 1 or DOBJ.Quarter= 2 THEN Trans_fact.Total_Sale ELSE 0 END) AS First_Half,
SUM(CASE WHEN DOBJ.Quarter = 3 or DOBJ.Quarter= 4 THEN Trans_fact.Total_Sale ELSE 0 END) AS Second_Half,
SUM(fact_table.Sales) AS Yearly_Sales
FROM product_table Prod, transactions Tfact_table, date_table DOBJ
WHERE DOBJ.Year = 2017 and fact_table.Product_ID=Prod.Product_ID AND 
DOBJ.Time_ID = fact_obj.DateID 
GROUP BY Prod.Product_ID
ORDER BY Prod.Product_Name;

-- Q9--
SELECT PRODUCT_NAME, COUNT(DISTINCT PRODUCT_ID) as PRODUCT_ID
FROM products
GROUP BY PRODUCT_NAME
HAVING COUNT(DISTINCT PRODUCT_ID) > 1
ORDER BY PRODUCT_NAME;

-- Q10--
CREATE TABLE STORE_ANALYSIS AS
SELECT 
    fact_table.STORE_ID, 
    IFNULL(Trans_fact.PRODUCTID, 'STORE_TOTAL') AS Product_Id,  
    SUM(Trans_fact.TOTAL_SALE) AS Total_Sale
FROM transactions_fact AS fact_table
GROUP BY fact_table.STOREID, dw_project.stores.STORE_NAME, fact_table.Product_Id, products.PRODUCT_NAME
WITH ROLLUP
HAVING (
    fact_table.STORE_ID IS NOT NULL
)
ORDER BY fact_table.StoreID, Trans_fact.PRODUCT_ID;

-- Selecting from the View
SELECT * FROM STOREANALYSIS;


