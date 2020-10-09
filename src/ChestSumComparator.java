import java.util.Comparator;

/**
 * Класс сортировки по сумме
 * @author Земнухов Владимир
 */
public class ChestSumComparator implements Comparator<Chest> {
    /**
     * Сортирует сундуки на основе их суммы
     * @param o1 - первый сундук
     * @param o2 - второй сундук
     * @return - результат сравнения
     */
    @Override
    public int compare(Chest o1, Chest o2){
        return Integer.compare(o1.getSum(), o2.getSum());
    }
}