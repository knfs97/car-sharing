import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class CompanyDAO implements DAO<Company> {

    public CompanyDAO() {
        createTable();
    }

    @Override
    public int lastestId() {
        final String query = "select id from company order by id desc limit 1";
        try(Connection conn = DAOFactory.createConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            int num = 0;
            while (rs.next()) {
                num = rs.getInt(1);
            }
            return num;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
    @Override
    public void insert(Company company) {
        int latestId = lastestId() + 1;
        final String query = "insert into company values(" + latestId + ", ?)";
        try (Connection conn =  DAOFactory.createConnection()){
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, company.getName());
            st.executeUpdate();
            System.out.println("The company was created");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<Company> select() {
        final String query = "select * from company;";
        List<Company> companies = new ArrayList<>();
        try (Connection conn =  DAOFactory.createConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                companies.add(new Company(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return companies;
    }
    @Override
    public Company find(int id) {
        final String query = "select * from company where id = " + id;
        try (Connection conn = DAOFactory.createConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            return new Company(rs.getInt(1), rs.getString(2));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new Company("not found");
    }

    @Override
    public boolean update(Company company) {
        return false;
    }

    @Override
    public boolean delete(int criteria) {
        return false;
    }

    @Override
    public void createTable() {
        final String companyTableQuery = """
        create table if not exists company (
        id int not null auto_increment primary key,
        name varchar(50) unique not null
        );
        """;
        String companyResetIdQuery = "ALTER TABLE company ALTER COLUMN id RESTART WITH 1";

        try (Connection connection = DAOFactory.createConnection()) {
            Statement st = connection.createStatement();
            st.executeUpdate(companyTableQuery);
            st.executeUpdate(companyResetIdQuery);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
