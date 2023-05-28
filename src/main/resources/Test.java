import com.github.unldenis.easydb4j.api.DataSource;

public class Test {


    public static void main(String[] args) {
        DataSource ds = new DataSource("jdbc:mysql://localhost:3306/easydb", "root", "");

        var db = new EasyDB(ds);

        db.get(MyUser.class).findAll((myUsers, t) -> {
            if(t != null) { t.printStackTrace(); return; }
            System.out.println(myUsers);
        });
    }

};
