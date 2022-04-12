package ru.yandex.practicum.task.models;

public class SubTask extends Task {

   private long parentID;


    public SubTask(String name, String description, long parentId) {
        super(name, description);
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
