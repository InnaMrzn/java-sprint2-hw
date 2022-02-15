import java.util.HashMap;

public class TaskManager {

    private static int nextTaskID;

    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, EpicTask> epicTaskMap = new HashMap<>();
    private HashMap<Integer, SubTask> subTaskMap = new HashMap<>();

    public HashMap<Integer, Task> getAllTasks() {
        return taskMap;
    }

    public HashMap<Integer, EpicTask> getAllEpics() {
        return epicTaskMap;
    }

    public HashMap<Integer, SubTask> getAllSubTasks() {
        return subTaskMap;
    }

    public void deleteAllTasks() {
        this.taskMap.clear();
    }

    public void deleteAllSubTasks() {
        this.subTaskMap.clear();
        for (Integer nextEpicKey: this.epicTaskMap.keySet()){
            this.epicTaskMap.get(nextEpicKey).getSubTasksMap().clear();
        }
    }

    public void deleteAllEpics () {
        this.epicTaskMap.clear();
        this.subTaskMap.clear();
    }

    public void deleteTaskByID(Integer taskID) {

        this.taskMap.remove(taskID);
    }

    public void deleteEpicByID(Integer taskID) {

        this.epicTaskMap.remove(taskID);
        for (Integer nextSubTaskKey: this.subTaskMap.keySet()){
            if (this.subTaskMap.get(nextSubTaskKey).getParentId() == taskID) {
                this.subTaskMap.remove(nextSubTaskKey);
            }
        }
    }

    public void deleteSubTaskByID(Integer subTaskID, Integer parentID) {

        this.subTaskMap.remove(subTaskID);
        this.epicTaskMap.get(parentID).getSubTasksMap().remove(subTaskID);
    }


    public SubTask getSubTaskByID (Integer id) {

       return this.subTaskMap.get(id);
    }

    public Task getTaskByID (Integer id) {

        return this.taskMap.get(id);
    }

    public EpicTask getEpicTaskByID (Integer id) {

        return this.epicTaskMap.get(id);
    }

    public void updateTask (Task task) {

        if (task.getID() != null && this.taskMap.get(task.getID()) != null) {

            this.taskMap.put(task.getID(),task);
        }
    }

    public void updateSubTask (SubTask task) {

        if (task.getID() != null && this.subTaskMap.get(task.getID()) != null) {

            this.subTaskMap.put(task.getID(),task);
            this.epicTaskMap.get(task.getParentId()).getSubTasksMap().put(task.getID(), task);
            ensureEpicStatus(task);
        }
    }

    public void updateEpicTask (EpicTask task) {

        if (task.getID() != null && this.epicTaskMap.get(task.getID()) != null) {
            EpicTask currentEpic = this.epicTaskMap.get(task.getID());
            HashMap<Integer, SubTask> subTask = currentEpic.getSubTasksMap();
            task.setSubTasksMap(subTask);
            if (!(task.getStatus().equals(currentEpic.getStatus()))) {
                task.setStatus(currentEpic.getStatus());
                System.out.println("Поменять статус Эпика нельзя");
            }
            this.epicTaskMap.put(task.getID(),task);

        }
    }

    public void createNewTask (Task task){

        saveNewTask(task, TaskType.TASK);

    }

    public void createNewEpicTask (EpicTask task){

        saveNewTask(task, TaskType.EPIC);

    }

    public void createNewSubTask (SubTask task){
        task.setParentId(task.getParentId());
        saveNewTask(task, TaskType.SUB_TASK);

    }

    public HashMap<Integer, SubTask> getAllEpicSubTasks (Integer epicID) {

        return this.epicTaskMap.get(epicID).getSubTasksMap();

    }

    private void saveNewTask(Task newTask, TaskType taskType) {
        newTask.setID(nextTaskID);

        switch (taskType){
            case TASK: {
                this.taskMap.put(nextTaskID,newTask);
                break;
            }
            case SUB_TASK: {
                SubTask subTask = (SubTask)newTask;
                if (this.epicTaskMap.get(subTask.getParentId()) != null) {
                    this.epicTaskMap.get(subTask.getParentId()).getSubTasksMap().put(nextTaskID, subTask);
                    this.subTaskMap.put(subTask.getID(), subTask);
                } else {
                    System.out.println("Эпик с номером "+ subTask.getParentId()+" не найден. Создание подзадачи невозможно");
                }
                break;
            }
            case EPIC: {
                this.epicTaskMap.put(nextTaskID,(EpicTask)newTask);
                break;
            }

        }
        nextTaskID++;

    }

    private void ensureEpicStatus (SubTask subTask) {

        EpicTask parentEpic = this.epicTaskMap.get(subTask.getParentId());
        int doneCount=0;
        int newCount=0;

        if (parentEpic.getSubTasksMap().size()==0){
            parentEpic.setStatus(TaskStatus.NEW);

        } else {

            for (Integer nextSubTask : parentEpic.getSubTasksMap().keySet()) {

                if (parentEpic.getSubTasksMap().get(nextSubTask).getStatus().equals(TaskStatus.NEW))
                    newCount++;
                if (parentEpic.getSubTasksMap().get(nextSubTask).getStatus().equals(TaskStatus.DONE))
                    doneCount++;
            }

            if (newCount == parentEpic.getSubTasksMap().size())
                parentEpic.setStatus(TaskStatus.NEW);
            else if (doneCount == parentEpic.getSubTasksMap().size())
                parentEpic.setStatus(TaskStatus.DONE);
            else
                parentEpic.setStatus(TaskStatus.IN_PROCESS);
        }
        this.epicTaskMap.put(subTask.getParentId(),parentEpic);

    }
}
