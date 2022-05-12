package ru.yandex.practicum.task.models;

import ru.yandex.practicum.task.constants.TaskStatus;

import java.time.LocalDateTime;

public class SubTask extends Task {

   private long parentId;


    public SubTask(String name, String description, TaskStatus status, long parentId) {
        super(name, description, status);
        this.setParentId(parentId);
    }

    public SubTask(String name, String description, TaskStatus status, long parentId, LocalDateTime startTime, long duration) {
        this(name, description, status, parentId);
        this.setStartTime(startTime);
        this.setDuration(duration);
    }


    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "parentID=" + parentId + ", "+ super.toString();
    }

}
