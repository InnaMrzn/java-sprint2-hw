package ru.yandex.practicum.task.logic;

public class Managers {

    private static final String TASK_MANAGER_TYPE ="IN_MEMORY";
    private static final String HISTORY_MANAGER_TYPE ="IN_MEMORY";

    public static TaskManager getDefault(){
        if(TASK_MANAGER_TYPE.equalsIgnoreCase("IN_MEMORY"))
            return new InMemoryTaskManager();
        //сюда добавляем if else когда у нас появятся другие типы TaskManager
        return null;
    }

    public static HistoryManager getDefaultHistory(){
        if(HISTORY_MANAGER_TYPE.equalsIgnoreCase("IN_MEMORY"))
            return new InMemoryHistoryManager();
        //сюда добавляем if else когда у нас появятся другие типы TaskManager
        return null;
    }


}
