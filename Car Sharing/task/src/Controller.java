import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {

    CompanyDAO companyDAO;
    CarDAO carDAO;
    CustomerDAO customerDAO;
    Scanner scanner;

    List<Customer> customers;

    public Controller() {
        this.companyDAO = DAOFactory.getCompanyDAO();
        this.carDAO = DAOFactory.getCarDAO();
        this.customerDAO = DAOFactory.getCustomerDAO();
        this.scanner = new Scanner(System.in);
        this.customers = this.customerDAO.select();
    }

    public void closeScanner() {
        this.scanner.close();
    }

    // ------------ Menus --------------- //
    public Command mainMenu() {
        Map<Integer, MenuCommand> menu = new LinkedHashMap<>();
        menu.put(1, new MenuCommand("Log in as a manager", this::managerMenu));
        menu.put(2, new MenuCommand("Log in as a customer", this::getCustomerList));
        menu.put(3, new MenuCommand("Create a customer", this::createCustomer));
        menu.put(0, new MenuCommand("Exit", null));
        return displayMenu(menu);
    }

    public Command managerMenu() {
        Map<Integer, MenuCommand> menu = new LinkedHashMap<>();
        menu.put(1, new MenuCommand("Company list", this::getCompanyList));
        menu.put(2, new MenuCommand("Create a company", this::createCompany));
        menu.put(0, new MenuCommand("Back", this::mainMenu));
        return displayMenu(menu);
    }

    public Command companyMenu(Company company) {
        System.out.printf("'%s' company", company.getName());
        System.out.println();
        Map<Integer, MenuCommand> menu = new LinkedHashMap<>();
        menu.put(1, new MenuCommand("Car list", () -> getCarList(company)));
        menu.put(2, new MenuCommand("Create a car", () -> createCar(company)));
        menu.put(0, new MenuCommand("Back", () -> this::managerMenu));
        return displayMenu(menu);
    }

    public Command customerMenu(Customer customer) {
        Map<Integer, MenuCommand> menu = new LinkedHashMap<>();
        menu.put(1, new MenuCommand("Rent a car", () -> rentACar(customer)));
        menu.put(2, new MenuCommand("Return a rented car", () -> returnACar(customer)));
        menu.put(3, new MenuCommand("My rented car", () -> myRentedCar(customer)));
        menu.put(0, new MenuCommand("Back", () -> this::mainMenu));
        return displayMenu(menu);
    }

    // ------------ Actions ------------ //
    public Command displayMenu(Map<Integer, MenuCommand> menu) {
        menu.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ". " + entry.getValue().getCaption())
                .forEach(System.out::println);

        int option = this.scanner.nextInt();
        this.scanner.nextLine();
        if (menu.containsKey(option)) {
            return menu.get(option).getCommand();
        }
        return null;
    }

    // --------- Company ------ //
    public Command getCompanyList() {
        List<Company> companies = this.companyDAO.select();
        Map<Integer, MenuCommand> menu = new LinkedHashMap<>();
        if (companies.size() == 0) {
            System.out.println("The company list is empty");
            return this::managerMenu;
        }
        System.out.println("Company list: ");
        int indexes = 1;
        for (Company company : companies) {
            menu.put(indexes, new MenuCommand(company.getName(), () -> this.companyMenu(company)));
            ++indexes;
        }
        menu.put(0, new MenuCommand("Back", this::managerMenu));
        return displayMenu(menu);
    }

    public Command createCompany() {
        System.out.println("Enter the company name: ");
        String name = this.scanner.nextLine();
        Company company = new Company(name);
        this.companyDAO.insert(company);
        return this::managerMenu;
    }

    // ------- Car ---------- //
    public Command getCarList(Company company) {
        List<Car> cars = this.carDAO.filterByCompany(company);
        if (cars.size() == 0) {
            System.out.println("The car list is empty!");
            return companyMenu(company);
        }
        System.out.println("Car list: ");
        int indexes = 1;
        for (Car car : cars) {
            System.out.println(indexes + ". " + car.getName());
            ++indexes;
        }
        return companyMenu(company);
    }

    public Command createCar(Company company) {
        System.out.println("Enter the car name: ");
        String name = this.scanner.nextLine();
        Car car = new Car(name, company.getId());
        this.carDAO.insert(car);
        return companyMenu(company);
    }

    // ------------ Customer ----- //

    public Command getCompanyListForCustomers(Customer customer) {
        List<Company> companies = this.companyDAO.select();
        List<Car> cars = this.carDAO.select();
        Map<Integer, MenuCommand> menu = new LinkedHashMap<>();
        if (companies.size() == 0) {
            System.out.println("The company list is empty");
            return this.customerMenu(customer);
        }
        System.out.println("Company list: ");
        companies.forEach(company -> menu.put(company.getId(), new MenuCommand(company.getName(), () -> {
            boolean hasCompanyCars = cars.stream()
                    .filter(car -> car.getCompanyId() == company.getId())
                    .toList()
                    .size() == 0;
            if (hasCompanyCars) {
                System.out.println("No available cars in the '" + company.getName() + "' company");
                return this.customerMenu(customer);
            }
            return getCarListForCustomers(customer, company, cars);
        })));
        menu.put(0, new MenuCommand("Back", () -> this.customerMenu(customer)));
        return displayMenu(menu);
    }

    public Command getCarListForCustomers(Customer customer, Company company ,List<Car> cars) {
        Map<Integer, MenuCommand> menu = new LinkedHashMap<>();
        List<Car> filteredCars = new ArrayList<>(cars);
        for (Car car : cars) {
            List<Customer> filtered = this.customers.stream().filter(cus -> cus.getRentedCarId() == car.getId()).toList();
            if (filtered.size() != 0) {
                filteredCars.removeIf(c -> c.getId() == filtered.get(0).getRentedCarId());
            }
        }
        if (filteredCars.size() == 0) {
            System.out.println("No available cars in the '" + company.getName() + "' company");
            return getCompanyListForCustomers(customer);
        }
        System.out.println("Car list: ");
        filteredCars.forEach(car -> menu.put(car.getId(), new MenuCommand(car.getName(), () -> {
            Customer updatedCustomer = customerDAO.rentCar(customer, car);
            return this.customerMenu(updatedCustomer);
        })));
        menu.put(0, new MenuCommand("Back", () -> this.customerMenu(customer)));
        return displayMenu(menu);
    }

    public Command createCustomer() {
        System.out.println("Enter the customer name: ");
        String name = this.scanner.nextLine();
        Customer customer = new Customer(name);
        this.customerDAO.insert(customer);
        // here created customer
        this.customers.add(customer);
        return this::mainMenu;
    }

    public Command getCustomerList() {

        Map<Integer, MenuCommand> menu = new LinkedHashMap<>();
        if (this.customers.size() == 0) {
            System.out.println("The customer list is empty!");
            return this::mainMenu;
        }
        System.out.println("Customer list: ");
        this.customers.forEach(customer -> menu.put(customer.getId(), new MenuCommand(customer.getName(), () -> this.customerMenu(customer))));
        menu.put(0, new MenuCommand("Back", () -> this::mainMenu));
        return this.displayMenu(menu);
    }

    public Command rentACar(Customer customer) {

        if (customer.getRentedCarId() != 0) {
            System.out.println("You've already rented a car!");
            return customerMenu(customer);
        }
        return getCompanyListForCustomers(customer);
    }

    public Command returnACar(Customer customer) {
        if (!customer.isHasRentedCar()) {
            System.out.println("You didn't rent a car!");
            return () -> this.customerMenu(customer);
        } else if (customer.getRentedCarId() == 0) {
            System.out.println("You've returned a rented car!");
            return () -> this.customerMenu(customer);
        }
        Customer updatedCustomer = customerDAO.returnRentedCar(customer);
        return () -> this.customerMenu(updatedCustomer);
    }

    public Command myRentedCar(Customer customer) {
        String[] rentedCarInfo = this.customerDAO.getRentedInfo(customer);
        if (rentedCarInfo.length <= 1) {
            System.out.println("You didn't rent a car!");
            return customerMenu(customer);
        }
        System.out.println("Your rented car: ");
        System.out.println(rentedCarInfo[0]);
        System.out.println("Company: ");
        System.out.println(rentedCarInfo[1]);
        return customerMenu(customer);
    }
}
