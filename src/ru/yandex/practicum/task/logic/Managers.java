package ru.yandex.practicum.task.logic;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Managers {

    public static boolean getIsBacked() {
        return isBacked;
    }

    public static void setIsBacked(boolean isBacked) {
        Managers.isBacked = isBacked;
    }

    /*константа выбора какой тип менеджера задач применять
        M - InMemory
        F - FileBackedTasksManager*/
    private static boolean isBacked = true;
    public static final String BACKUP_FOLDER_NAME = "resources";
    public static final String BACKUP_FILE_NAME = "tasks.csv";

    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static final Map<Long, Boolean> schedule = new HashMap<>();

    public static TaskManager getDefault() {

        loadSchedule();
        if (isBacked)
            return FileBackedTaskManager.getManagerFromFile(BACKUP_FOLDER_NAME+ System.getProperty("file.separator")+BACKUP_FILE_NAME);
        else return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

    //Загружает мапу с интервалами времени по 15 мин на год вперед и присваивает всем слотам значение true=свободен.
    private static void loadSchedule(){
        LocalDateTime startDate = LocalDateTime.now();
        startDate = startDate.minusMinutes(startDate.getMinute() % 15);
        LocalDateTime endDate = startDate.plusYears(1);

        while (startDate.isBefore(endDate)) {
            Long startPoint = Long.parseLong(startDate.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
            schedule.put(startPoint,true);
            startDate = startDate.plusMinutes(15);
        }

    }


}
