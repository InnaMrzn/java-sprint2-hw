import java.util.HashMap;

public class EpicTask extends Task{

    public void setSubTasksMap(HashMap<Integer, SubTask> subTasksMap) {
        this.subTasksMap = subTasksMap;
    }

    private HashMap<Integer, SubTask> subTasksMap = new HashMap<>();

    public EpicTask (String name, String description) {

        super(name, description);
    }

    public HashMap<Integer, SubTask> getSubTasksMap() {
        return subTasksMap;
    }


}
