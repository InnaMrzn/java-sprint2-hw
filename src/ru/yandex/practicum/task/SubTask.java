public class SubTask extends Task{

    private int parentId;

    public SubTask(String name, String description, int parentId) {
        super(name, description);
        this.setParentId(parentId);
    }


    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
}
