package ru.yandex.practicum.task.models;

import java.util.HashMap;

public class EpicTask extends Task {

    public void setSubTasksMap(HashMap<Long, SubTask> subTasksMap) {
        this.subTasksMap = subTasksMap;
    }

    private HashMap<Long, SubTask> subTasksMap = new HashMap<>();

    public EpicTask (String name, String description) {
        super(name, description);
    }

    public HashMap<Long, SubTask> getSubTasksMap() {
        return subTasksMap;
    }
}
