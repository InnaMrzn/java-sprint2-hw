package ru.yandex.practicum.task.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class EpicTask extends Task {

    private List<Long> subTasksIDsList = new ArrayList<>();
    private LocalDateTime endTime;

    public EpicTask (String name, String description) {
        super(name, description);
    }

    @Override
    public LocalDateTime getEndTime (){

        return endTime;
    }

    public void setSubTasksIDsList (List<Long> subTasksIDsList) {
        this.subTasksIDsList = subTasksIDsList;

    }

    public List<Long> getSubTasksIDsList() {
        return subTasksIDsList;

    }

    public void setEndTime (LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
