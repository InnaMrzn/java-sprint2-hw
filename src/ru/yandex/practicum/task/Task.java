public class Task {

    private String name;
    private Integer ID;
    private String description;
    private TaskStatus Status;

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

    public Integer getID() {

        return ID;
    }

    public void setID(Integer ID) {

        this.ID = ID;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public TaskStatus getStatus() {

        return Status;
    }

    public void setStatus(TaskStatus status) {

        Status = status;
    }
}
