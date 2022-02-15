package ru.yandex.practicum.task;

public class SubTask extends Task {

    private int parentID;

    public SubTask(String name, String description, int parentId) {
        super(name, description);
        this.setParentId(parentId);
    }


    public int getParentId() {
        return parentID;
    }

    public void setParentId(int parentId) {
        this.parentID = parentId;
    }

    @Override
    public String toString() {
        return "parentID=" + parentID+ ", "+ super.toString();
    }
}
