import org.postgresql.util.PSQLException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

class DataBase<T> {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "Half-Life";

    private String tableName;
    private ArrayList<AccessibleObject> elements = new ArrayList<>();
    private ArrayList<AccessibleObject> primaryKeys = new ArrayList<>();

    private Connection connection = null;

    DataBase(Class currentClass) {
        connect();
        tableName = currentClass.getName().toUpperCase();
        System.out.println(tableName);

        System.out.println("---- Declared Fields ----");

        Arrays.stream(currentClass.getDeclaredFields())
                .forEach(e -> {
                    if (Arrays.stream(e.getAnnotations()).anyMatch(el -> el instanceof DBField)) {
                        elements.add(e);

                        if (Arrays.stream(e.getAnnotations()).anyMatch(el -> el instanceof PrimaryKey)) {
                            primaryKeys.add(e);
                        }

                    }});

        elements.forEach(e-> System.out.println(e.toString()));
        primaryKeys.forEach(System.out::println);

        createTable(tableName);
    }

    private void connect() {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Не удалось подключиться к БД");
            e.printStackTrace();
            return;
        }


        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

        } catch (SQLException e) {
            System.out.println("Не удалось подключиться");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("Вы успешно подключились к БД");
        } else {
            System.out.println("Не удалось подключиться к БД");
        }

    }

    private void createTable(String name){
        StringBuilder sqlQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS ");

        sqlQuery.append(name);
        sqlQuery.append(" ( ");

        elements.forEach(e -> {
            String temp = "";
            temp += "\"" + getName(e) + "\"";
            temp += " ";
            temp += getType(e);
            temp += " NOT NULL, ";
            sqlQuery.append(temp);
        });

        sqlQuery.append("PRIMARY KEY (");

        primaryKeys.forEach(e -> {
            sqlQuery.append("\"");
            sqlQuery.append(getName(e));
            sqlQuery.append("\"");
            sqlQuery.append(",");
        });
        sqlQuery.deleteCharAt(sqlQuery.length() - 1);
        sqlQuery.append("));");

        System.out.println(sqlQuery);

        try {
            connection.createStatement().execute(sqlQuery.toString());
        }catch (PSQLException e){
            System.out.println("Таблица уже существует либо ошибка синтаксиса.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    int insert(T object) {
        StringBuilder sqlQuery = new StringBuilder("INSERT INTO " + tableName + " VALUES (");

        elements.forEach(e -> {
            sqlQuery.append(getValue(e, object)).append(",");
        });
        sqlQuery.deleteCharAt(sqlQuery.length() - 1);
        sqlQuery.append(");");

        System.out.println(sqlQuery);

        try {
            return connection.createStatement().executeUpdate(sqlQuery.toString());
        }catch (PSQLException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    int update(T object) {
        StringBuilder sqlQuery = new StringBuilder("UPDATE " + tableName + " SET ");
        elements.forEach(e -> {
            String temp = "";
            temp += "\"";
            temp += getName(e);
            temp += "\"";
            temp += " = ";
            temp += getValue(e, object);
            temp += ",";
            sqlQuery.append(temp);
        });
        sqlQuery.deleteCharAt(sqlQuery.length() - 1);
        sqlQuery.append(" WHERE ");
        sqlQuery.append(getPrimaryKeyConditions(object));
        sqlQuery.delete(sqlQuery.length() - 4, sqlQuery.length());
        sqlQuery.append(";");
        try {
            return connection.createStatement().executeUpdate(sqlQuery.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    int delete(T object) {
        StringBuilder sqlQuery = new StringBuilder("DELETE FROM "+tableName+" WHERE ");
        sqlQuery.append(getPrimaryKeyConditions(object));
        sqlQuery.delete(sqlQuery.length() - 4, sqlQuery.length());
        sqlQuery.append(";");

        System.out.println(sqlQuery);

        try {
            return connection.createStatement().executeUpdate(sqlQuery.toString().toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    ResultSet executeQuery(String query) {
        try {
            return connection.createStatement().executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPrimaryKeyConditions(T object) {
        StringBuilder sqlQuery = new StringBuilder();
        primaryKeys.forEach(e -> {
            String temp = "";
            temp += "\"";
            temp += getName(e);
            temp += "\"";
            temp += " = ";
            temp += getValue(e, object);
            temp += " AND ";
            sqlQuery.append(temp);
        });
        return sqlQuery.toString();
    }

    private String getValue(AccessibleObject e, T object) {

        e.setAccessible(true);
        Object temp = null;

        if (e instanceof Field) {
            try {
                temp = ((Field) e).get(object);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (temp instanceof String) {
            return "'" + temp + "'";
        }
        else if (temp instanceof LocalDateTime){
            return "'" + ((LocalDateTime) temp).toString() + "'";
        }
        else if (Integer.class.isInstance(temp))
            return Integer.toString((int) temp);

        return null;
    }

    private String getType(AccessibleObject e) {

        Object type = null;

        if (e instanceof Field){
            type = ((Field) e).getType();
        }

        if (type == String.class){
            return "varchar(31)";
        }
        else if (type == int.class){
            return "integer";
        }
        else if (type == LocalDateTime.class){
            return "timestamp";
        }
        else
            return null;

    }

    private String getName(AccessibleObject e) {

        if (e instanceof Field){
            return ((Field) e).getName();
        }
        else
            return null;
    }

    void commit() {
        try {
            if (!connection.getAutoCommit())
                connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}