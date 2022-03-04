package ru.yandex.practicum.task.models;

import ru.yandex.practicum.task.constants.TaskStatus;
import java.util.Objects;

public class Task {

    private String name;
    private Long ID;
    private String description;
    private TaskStatus status;

    public Task(String name, String description) {
        this.setName(name);
        this.setDescription(description);
        this.setStatus(TaskStatus.NEW);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " {"+
                "ID='" + ID + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return ID.equals(task.ID);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, ID, description, status);
        result = 31 * result;
        return result;
    }
}
