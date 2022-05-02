package ru.yandex.practicum.task.models;

import ru.yandex.practicum.task.constants.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class EpicTask extends Task {

    private List<Long> epicSubTasksIds = new ArrayList<>();
    private LocalDateTime endTime;

    public EpicTask (String name, String description) {

        super(name, description, TaskStatus.NEW);
    }

    @Override
    public LocalDateTime getEndTime (){

        return endTime;
    }

    public void setEpicSubTasksIds(List<Long> subTasksIDsList) {
        this.epicSubTasksIds = subTasksIDsList;

    }

    public List<Long> getEpicSubTasksIds() {
        return epicSubTasksIds;

    }

    public void setEndTime (LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
