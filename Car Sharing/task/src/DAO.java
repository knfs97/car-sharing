import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    int lastestId();
    void insert(T t);
    T find(int id); // find one specific
    Collection<T> select(); // select all from rows from table into collection
    boolean update(T t); // update one
    boolean delete(int criteria); // delete one row or many
    void createTable();


}
