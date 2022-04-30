package ru.yandex.practicum.task.models;

import ru.yandex.practicum.task.constants.TaskStatus;

public class SubTask extends Task {

   private long parentID;


    public SubTask(String name, String description, TaskStatus status, long parentId) {
        super(name, description, status);
        this.setParentId(parentId);
    }


    public long getParentId() {
        return parentID;
    }

    public void setParentId(long parentId) {
        this.parentID = parentId;
    }

    @Override
    public String toString() {
        return "parentID=" + parentID+ ", "+ super.toString();
    }

}
