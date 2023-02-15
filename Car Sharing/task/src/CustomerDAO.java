import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO implements DAO<Customer> {
    public CustomerDAO() {
        createTable();
    }

    @Override
    public int lastestId() {
       final String query = "select id from customer order by id desc limit 1;";
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
    public void insert(Customer customer) {
        int latestId = lastestId() + 1;
        System.out.println(latestId);
        final String query = "insert into customer values(" + latestId + ", '" + customer.getName() + "', "  + null + ");";
        try (Connection conn = DAOFactory.createConnection()) {
            Statement st = conn.createStatement();
            st.executeUpdate(query);
            customer.setId(latestId);
            customer.setRentedCarId(0);
            System.out.println("The customer was created");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Customer find(int id) {
        final String query = "select * from customer where id = " + id + ";";
        try (Connection conn = DAOFactory.createConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            Customer customer = new Customer("not found");
            while (rs.next()) {
                int customerID = rs.getInt("id");
                String name = rs.getString("name");
                int rented_car_id = rs.getInt("rented_car_id");
                boolean wasNullRentedCar = rs.wasNull();
                rented_car_id = wasNullRentedCar ? -1 : rented_car_id;
                customer = new Customer(customerID, name, rented_car_id);
            }
            return customer;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new Customer("not found");
    }

    @Override
    public List<Customer> select() {
        final String query = "select * from customer;";
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = DAOFactory.createConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int rented_car_id = rs.getInt("rented_car_id");
                    customers.add(new Customer(id, name, rented_car_id));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }

    public String[] getRentedInfo(Customer customer) {
        Integer rentedCarId = customer.getRentedCarId();
        String[] information = new String[2];
        if ( customer.getRentedCarId() == 0 ) {
            return new String[0];
        }
        final String selectForCompanyID = "select company_id from car where id =" + rentedCarId;
        final String getCarByIdQuery = """
                select car.name as car_name, company.name as company_name from car
                inner join company on ( """ + selectForCompanyID + " ) = company.id where car.id = " + rentedCarId;

        try (Connection conn = DAOFactory.createConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(getCarByIdQuery);
            while (rs.next()) {
                information[0] = rs.getString(1);
                information[1] = rs.getString(2);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return (information[0] == null) ? new String[0] : information;
    }

    public Customer returnRentedCar(Customer customer) {
        final String updateRentedCard = """
                update customer set rented_car_id = null
                where id =""" + customer.getId() + ";";
        try (Connection conn = DAOFactory.createConnection()) {
            Statement st = conn.createStatement();
            st.executeUpdate(updateRentedCard);
            customer.setRentedCarId(0);
            System.out.println("You've returned a rented car!");
            return customer;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return customer;
    }

    public Customer rentCar(Customer customer, Car car) {
        final String updateRentedCard =
                " update customer set rented_car_id = " + car.getId()
                        + " where id = " + customer.getId() + ";";
        try (Connection conn = DAOFactory.createConnection()) {
            Statement st = conn.createStatement();
            st.executeUpdate(updateRentedCard);
            customer.setHasRentedCar(true);
            customer.setRentedCarId(car.getId());
            System.out.println("You rented '" + car.getName() + "'");
            return customer;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return customer;
    }

    @Override
    public boolean update(Customer customer) {
        return false;
    }

    @Override
    public boolean delete(int criteria) {
        return false;
    }

    @Override
    public void createTable() {
        final String customerTableQuery = """
                create table if not exists customer (
                id int not null auto_increment primary key,
                name varchar(50) unique not null,
                rented_car_id int null,
                constraint fk_rented_car_id foreign key (rented_car_id) references car(id)
                );
                """;
        String carResetIdQuery = "ALTER TABLE customer ALTER COLUMN id RESTART WITH 1";

        try (Connection connection = DAOFactory.createConnection()) {
            Statement st = connection.createStatement();
            st.executeUpdate(customerTableQuery);
            st.executeUpdate(carResetIdQuery);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
