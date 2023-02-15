import java.sql.*;

public class DAOFactory {
    public static final String DRIVER = "org.h2.Driver";
    public static final String DBURL = "jdbc:h2:./src/carsharing/db/carsharing";

    public static CompanyDAO getCompanyDAO() {
        return new CompanyDAO();
    }

    public static CarDAO getCarDAO() {
        return new CarDAO();
    }

    public static CustomerDAO getCustomerDAO() {
        return new CustomerDAO();
    }

    public static Connection createConnection() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        Connection conn = DriverManager.getConnection(DBURL);
        conn.setAutoCommit(true);
        return conn;
    }
}
