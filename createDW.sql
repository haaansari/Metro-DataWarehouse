drop schema if exists metrodwh;
create schema metrodwh;
use metrodwh;
drop table if exists Transactions_Fact;
drop table if exists stores;
drop table if exists dates;
drop table if exists suppliers;
drop table if exists products;
drop table if exists customers; 
CREATE table stores(
	STORE_ID VARCHAR(4) NOT NULL,
    STORE_NAME VARCHAR(50),
    CONSTRAINT STORE_PK PRIMARY KEY (STORE_ID)
);
CREATE table dates(
	TIME_ID VARCHAR(10) NOT NULL,
	T_DATE VARCHAR(10) NOT NULL,
    week varchar(10),
    month varchar(2),
    quarter varchar(2),
    year varchar(4),
    CONSTRAINT DATE_PK PRIMARY KEY (Time_ID)
);

CREATE table suppliers(
	SupplierID VARCHAR(5) NOT NULL,
    SupplierName VARCHAR(30),
    CONSTRAINT Supplier_pk PRIMARY KEY (SupplierID)
);

CREATE TABLE customers(
    CUSTOMER_ID VARCHAR(4), 
    CUSTOMER_NAME VARCHAR(30) not null,
    CONSTRAINT `CUSTOMERS_PK` PRIMARY KEY (CUSTOMER_ID)
); 

CREATE TABLE products(
    PRODUCT_ID VARCHAR(6), 
	PRODUCT_NAME VARCHAR(30) NOT NULL, 
	PRICE DOUBLE(5,2) DEFAULT 0.0,
    CONSTRAINT `PRODUCTS_PK` PRIMARY KEY (PRODUCT_ID)
);
CREATE table Transactions_Fact(
	Transaction_ID INT NOT NULL,
    CUSTOMER_ID VARCHAR(6) NOT NULL,
	Product_ID VARCHAR(7) NOT NULL,
	STORE_ID VARCHAR(4) NOT NULL,
    SUPPLIER_ID VARCHAR(6) NOT NULL,
    TIME_ID varchar(10) NOT NULL,
    Sales Double,
    Quantity Double,
    CONSTRAINT TRANSACTIONS_Fact_pk PRIMARY KEY (Transaction_ID),
    CONSTRAINT TRANSACTIONS_Store_fk FOREIGN KEY(STORE_ID) References stores(Store_ID),
    CONSTRAINT TRANSACTIONS_Product_fk FOREIGN KEY(Product_ID) References products(Product_ID),
    CONSTRAINT TRANSACTIONS_Customer_fk FOREIGN KEY(CUSTOMER_ID) References customers(Customer_ID),
    CONSTRAINT TRANSACTIONS_Date_fk FOREIGN KEY(TIME_ID) References dates(TIME_ID),
    CONSTRAINT TRANSACTIONS_Supplier_fk FOREIGN KEY(SUPPLIER_ID) References suppliers(SupplierID)
); 

select * from Transactions_Fact