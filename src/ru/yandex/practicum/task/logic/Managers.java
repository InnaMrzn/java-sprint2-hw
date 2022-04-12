package ru.yandex.practicum.task.logic;



public class Managers {

    /*константа выбора какой тип менеджера задач применять
    M - InMemory
    F - FileBackedTasksManager*/
    public static final boolean isBacked = true;
    public static final String BACKUP_FOLDER_NAME = "resources";
    public static final String BACKUP_FILE_NAME = "tasks.csv";


    public static TaskManager getDefault() {
        if (isBacked)
            return FileBackedTaskManager.getManagerFromFile(BACKUP_FOLDER_NAME+ System.getProperty("file.separator")+BACKUP_FILE_NAME);
        else return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }


}
