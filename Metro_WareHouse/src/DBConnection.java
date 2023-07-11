import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnection {
    private static DBConnection DBConnection=null;

    public Connection con =null;
    private DBConnection(String DB) throws SQLException {
        try{
            String host = "jdbc:mysql://localhost:3306/"+DB;
            String Name = "root";
            String Pass = "ali12345";
            con = DriverManager.getConnection(host, Name, Pass);
        }
        catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }
    public static DBConnection getInstance(String DB) throws SQLException {
        if(DBConnection==null)
        {
            DBConnection=new DBConnection(DB);
        }
        return DBConnection;
    }
}
