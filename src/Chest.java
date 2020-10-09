import java.awt.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.text.DateFormat;
import java.util.Locale;

/**
 * Класс судуков
 * @author Земнухов Владимир
 */
class Chest implements Serializable {
    @DBField
    @PrimaryKey
    int id;

    @DBField
    private String name;

    @DBField
    private int sum;

    @DBField
    int x;

    @DBField
    int y;

    @DBField
    LocalDateTime create_date;

    private Color color;

    /**
     * Конструктор сундука
     *
     * @param a - задаваемое имя
     * @param b - задаваемая хранимая сумма
     */
    Chest(String a, String b, String x, String y) {
        int col = (int)(4*Math.random());
        switch (col) {
            case (0): color = Color.red;
                break;
            case (1): color = Color.green;
                break;
            case (2): color = Color.blue;
                break;
            case (3): color = Color.yellow;
                break;
        }
        setName(a);
        setSum(b);
        create_date = LocalDateTime.now();
        setState(Integer.valueOf(x), Integer.valueOf(y));
    }

    /**
     * Конструктор сундука
     */
    Chest() {
        int col = (int)(4*Math.random());
        switch (col) {
            case (0): color = Color.red;
            break;
            case (1): color = Color.green;
            break;
            case (2): color = Color.blue;
            break;
            case (3): color = Color.yellow;
            break;
        }
    }

    /**
     * Задать имя сундука
     *
     * @param name - его имя
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Задать сумму, хранимую в сундуке
     *
     * @param sum - сумма на хранение
     */
    void setSum(String sum) {
        try {
            this.sum = Integer.parseInt(sum);
        } catch (NumberFormatException e) {
            System.out.println("Неверный числовой формат в сумме в " + this.name + "'е.");
        }
    }

    private void setState(int x, int y) {
        try {
            this.x = x;
            this.y = y;
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат в координатах в " + this.name + "'е");
        }
    }

    /**
     * Получить имя сундука
     *
     * @return - полученное имя
     */
    String getName() {
        return this.name;
    }

    /**
     * Узнать хранимую в сундуке сумму
     *
     * @return - хранимая сумма
     */
    int getSum() {
        return sum;
    }

    Color getColor() {
        return color;
    }
}