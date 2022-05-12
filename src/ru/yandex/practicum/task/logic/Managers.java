package ru.yandex.practicum.task.logic;


import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Managers {

    public enum ManagerType  {MEMORY, FILE_BACKED, HTTP}
    public static final String BACKUP_FOLDER_NAME = "resources";
    public static final String BACKUP_FILE_NAME = "tasks.csv";
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final Map<Long, Boolean> schedule = new HashMap<>();
    public static final String serverUrl = "http://localhost:8078";


    public static TaskManager getDefault() {
        //загружаем пустую сетку с 15-ти минутными интервалами на год вперед
        loadSchedule();

        ManagerType managerType = ManagerType.HTTP;

        switch (managerType) {
            case FILE_BACKED:
                return new FileBackedTaskManager(Paths.get(BACKUP_FOLDER_NAME + System.getProperty("file.separator") + BACKUP_FILE_NAME).toAbsolutePath());
            case MEMORY:
                return new InMemoryTaskManager();
            case HTTP:
                return new HTTPTaskManager(serverUrl);
            default:
                return null;
        }
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
