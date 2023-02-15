public class Car implements java.io.Serializable{
    private String name;
    private int id;
    private int companyId;
    public Car(String name) {
        this.name = name;
    }
    public Car(String name, int companyId) {
        this.name = name;
        this.companyId = companyId;
    }
    public Car(int id, String name, int companyId) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
