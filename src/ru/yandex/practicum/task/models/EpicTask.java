package ru.yandex.practicum.task.models;

import java.util.List;
import java.util.ArrayList;

public class EpicTask extends Task {

    private List<Long> subTasksIDsList = new ArrayList<>();

    public void setSubTasksIDsList (List<Long> subTasksIDsList) {
        this.subTasksIDsList = subTasksIDsList;

    }

    public EpicTask (String name, String description) {
        super(name, description);
    }

    public List<Long> getSubTasksIDsList() {
        return subTasksIDsList;

    }
}
