import java.util.Comparator;

/**
 * Класс сортировки по имени
 * @author Земнухов Владимир
 */
class ChestNameComparator implements Comparator<Chest> {
    /**
     * Сортирует сундуки на основе их имени
     * @param o1 - первый сундук
     * @param o2 - второй сундук
     * @return - результат сравнения
     */
    @Override
    public int compare(Chest o1, Chest o2) {
        return o1.getName().compareTo(o2.getName());
    }
}