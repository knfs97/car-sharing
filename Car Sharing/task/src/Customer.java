public class Customer {
    private int id;
    private String name;
    private int rentedCarId;
    private boolean hasRentedCar;

    public Customer(int id, String name, int rentedCarId) {
        this.id = id;
        this.name = name;
        this.rentedCarId = rentedCarId;
        this.hasRentedCar = false;
    }
    public Customer(String name) {
        this.name = name;
        this.hasRentedCar = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isHasRentedCar() {
        return hasRentedCar;
    }

    public void setHasRentedCar(boolean hasRentedCar) {
        this.hasRentedCar = hasRentedCar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRentedCarId() {
        return rentedCarId;
    }

    public void setRentedCarId(int rentedCarId) {
        this.rentedCarId = rentedCarId;
    }
}
