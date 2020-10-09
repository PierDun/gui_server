import java.io.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Класс коллекций сундуков
 * @author Земнухов Владимир
 */
class ChestCollection {
    private Comparator<Chest> chestComparator = new ChestNameComparator().thenComparing(new ChestSumComparator());
    private ConcurrentSkipListSet<Chest> types = new ConcurrentSkipListSet<>(chestComparator);
    private DataBase dataBase = new DataBase(Chest.class);
    private final DateTimeFormatter create_format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    Scanner input = null;
    private String path = "";

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "Half-Life";

    void setPath(String path) throws FileNotFoundException, NullPointerException {
        this.path = path;
        File file;
        try {
            file = new File(this.path);
            file.setReadable(true);
            file.setWritable(true);
            input = new Scanner(file);
        } catch (FileNotFoundException | NullPointerException e) {
            try {
                System.out.println("Переменная окружения Lab7 либо не существует, либо указывает на не тот файл. \n " +
                        "Будет использован файл lab7.csv");
                file = new File("/home/s245031/lab7/lab7.csv");
                file.setReadable(true);
                file.setWritable(true);
                input = new Scanner(file);
            } catch (FileNotFoundException e1) {
                System.out.println("Создайте переменную окружения Lab7 и укажите путь к файлу, в котором хотите хранить коллекцию.");
                System.exit(0);
            }
        }
    }

    private int createID () {
        int randomID;
        boolean check = true;
        do {
            randomID = (int) (Math.random() * 256);
            for (Chest curChest : types) {
                check = check & (curChest.id != randomID);
            }
        } while (!check);
        return randomID;
    }

    /**
     * Считать элементы с указанного файла
     */
    void readElements(){
        Chest curChest;
        try {
            ResultSet resultSet = DriverManager.getConnection(DB_URL,USER,PASS).createStatement().executeQuery( "SELECT * FROM " + Chest.class.getName().toUpperCase() + ";" );
            while (resultSet.next()) {
                curChest = new Chest(resultSet.getString("name"), resultSet.getString("sum"), resultSet.getString("x"), resultSet.getString("y"));
                curChest.create_date = LocalDateTime.parse((resultSet.getString("create_date").replace("T", " ").substring(0, 19)), create_format);
                types.add(curChest);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void writeElements(){
        try {
            ResultSet resultSet = DriverManager.getConnection(DB_URL,USER,PASS).createStatement().executeQuery( "SELECT * FROM " + Chest.class.getName().toUpperCase() + ";" );
            while (resultSet.next()) {
                System.out.println(resultSet.getString("name") + " " +
                        resultSet.getString("sum") + " " +
                        resultSet.getString("x") + " " +
                        resultSet.getString("y") + " " +
                        resultSet.getString("create_date")
                );
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * сохранить коллекцию в файл
     */
    void save() {
        dataBase.commit();
    }

    void add_element(Chest chest){
        types.add(chest);
        chest.id = createID();
        dataBase.insert(chest);
    }

    void removeElement(Chest chest){
        types.remove(chest);
        dataBase.delete(chest);
        System.out.println("removed");
    }

    boolean lower(Chest addedChest){
        if (types.lower(addedChest) == null){
            types.add(addedChest);
            addedChest.id = createID();
            dataBase.insert(addedChest);
            return true;
        }else {
            return false;
        }
    }

    boolean higher(Chest addedChest){
        if (types.higher(addedChest) == null){
            dataBase.insert(addedChest);
            addedChest.id = createID();
            types.add(addedChest);
            return true;
        }else {
            return false;
        }
    }

    boolean contains(Chest addedChest){
        return types.contains(addedChest);
    }

    ArrayList<Chest> removeGreater(Chest addedChest){

        ArrayList<Chest> curArray = new ArrayList<>();

        while(types.higher(addedChest)!= null){
            curArray.add(types.higher(addedChest));
            types.remove(types.higher(addedChest));
        }

        return curArray;
    }

    ArrayList<Chest> returnObjects(){
        return new ArrayList<>(types);
    }
}