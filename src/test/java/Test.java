import com.github.unldenis.easydb4j.EasyDB;
import com.github.unldenis.easydb4j.api.DataSource;
import java.sql.Timestamp;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {

    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws InterruptedException {
        DataSource ds = new DataSource("jdbc:mysql://localhost:3306/easydb", "root", "");
        var db = new EasyDB(ds);
//
//        var p = new Person("Maria", Timestamp.from(Instant.now()));
//
//        db.get(Person.class).add(p, success -> logger.info("Added Maria? " + success));

        var id = 6;
        db.get(Person.class).findOne(id, res -> {
            if(res == null) {
                logger.warn("Not found with id " + id);
                return;
            }
            logger.info("Id " + id + " ? " + res);

            res.name = "Mario";
            db.get(Person.class).update(6,  res, success -> logger.info("Updated Maria to Mario? "+ success));
        });




    }

};
