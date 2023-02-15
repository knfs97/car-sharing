import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CarDAO implements DAO<Car> {

    public CarDAO() {
        createTable();
    }

    @Override
    public int lastestId() {
        final String query = "select id from car order by id desc limit 1";
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
    public void insert(Car car) {
        int latestId = lastestId() + 1;
        final String query = "insert into car values(" + latestId +  ", '" + car.getName() + "' ," + car.getCompanyId() + ");";
        try (Connection conn = DAOFactory.createConnection()) {
            Statement st = conn.createStatement();
            st.executeUpdate(query);
            System.out.println("The car was created");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Car find(int id) {
        final String query = "select * from car where id = " + id + " || company_id = " + id;
        try (Connection conn = DAOFactory.createConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            return new Car(rs.getInt(1), rs.getString(2), rs.getInt(3));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new Car("not found");
    }

    @Override
    public List<Car> select() {
        final String query = "select * from car;";
        List<Car> cars = new ArrayList<>();
        try (Connection conn = DAOFactory.createConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                cars.add(new Car(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return cars;
    }

    public List<Car> filterByCompany(Company company) {
        return select().stream().filter(car -> car.getCompanyId() == company.getId()).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean update(Car car) {
        return false;
    }

    @Override
    public boolean delete(int criteria) {
        return false;
    }

    @Override
    public void createTable() {
        final String carTableQuery = """
                create table if not exists car (
                id int not null auto_increment primary key,
                name varchar(50) unique not null,
                company_id int not null,
                constraint fk_company_id foreign key (company_id) references company(id)
                );
                """;
        String carResetIdQuery = "ALTER TABLE car ALTER COLUMN id RESTART WITH 1";

        try (Connection connection = DAOFactory.createConnection()) {
            Statement st = connection.createStatement();
            st.executeUpdate(carTableQuery);
            st.executeUpdate(carResetIdQuery);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
