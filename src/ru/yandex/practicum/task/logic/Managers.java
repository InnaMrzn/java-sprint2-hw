package ru.yandex.practicum.task.logic;

public class Managers {

    private static final String MANAGER_TYPE ="IN_MEMORY";

    public static TaskManager getTaskManager(){
        if(MANAGER_TYPE.equalsIgnoreCase("IN_MEMORY"))
            return new InMemoryTaskManager();
        //сюда добавляем if else когда у нас появятся другие типы TaskManager
        return null;
    }
}
