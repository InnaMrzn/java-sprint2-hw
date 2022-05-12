package ru.yandex.practicum.task.models;

import ru.yandex.practicum.task.constants.TaskStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {

    private String name;
    private Long taskId;
    private String description;
    private TaskStatus status;
    private LocalDateTime startTime;
    //продолжительность в минутах
    long duration ;

    public Task(String name, String description, TaskStatus status) {
        this.setName(name);
        this.setDescription(description);
        this.setStatus(status);
    }

    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, long duration) {
        this(name,description,status);
        this.setStartTime(startTime);
        this.setDuration(duration);
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public LocalDateTime getEndTime(){

        return Optional.ofNullable(getStartTime()).isPresent()? this.getStartTime().plusMinutes(duration):null;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Long getTaskId() {

        return taskId;
    }

    public void setTaskId(Long taskId) {

        this.taskId = taskId;
    }

    public String getDescription() {

        return description;
    }

    public long getDuration() {

        return this.duration;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public TaskStatus getStatus() {

        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {

        this.startTime = startTime;
    }


    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " {"+
                "ID='" + taskId + '\'' +
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
        return taskId.equals(task.taskId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(taskId);
        result = 31 * result;
        return result;
    }



}
