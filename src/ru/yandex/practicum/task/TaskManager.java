package ru.yandex.practicum.task;

import ru.yandex.practicum.task.constants.TaskStatus;
import ru.yandex.practicum.task.constants.TaskType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class TaskManager {

    private static int nextTaskID;
    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTaskMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();

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

    //метод удаляет все подзадачи во всех эпиках, при этом статусы Эпиков обновляются на NEW
    public void deleteAllSubTasks() {
        this.subTaskMap.clear();
        for (Integer nextEpicKey: this.epicTaskMap.keySet()){
            EpicTask nextEpicTask = this.epicTaskMap.get(nextEpicKey);
            nextEpicTask.getSubTasksMap().clear();
            nextEpicTask.setStatus(TaskStatus.NEW);
        }
    }

    // метод удаляет все эпики проекта, а также их подзадачи, так как подзадачи не могут быть без эпика
    public void deleteAllEpics () {
        this.epicTaskMap.clear();
        this.subTaskMap.clear();
    }

    public void deleteTaskByID(Integer taskID) {
        this.taskMap.remove(taskID);
    }

    // метод удаляет конкретный эпик, а также удаляет из общей коллекции подклассов все подклассы данного эпика
    public void deleteEpicByID(Integer taskID) {
        Collection<SubTask> subTasksCollection = this.subTaskMap.values();
        Iterator<SubTask> iterator = subTasksCollection.iterator();
        while (iterator.hasNext()){
            SubTask nextSubTaskKey = iterator.next();
            if ( nextSubTaskKey.getParentId() == taskID) {
                iterator.remove();
            }
        }
        this.epicTaskMap.remove(taskID);
    }

    /*метод кдаляет конкретный подкласс из коллекции подклассов Эпика и из общеф коллекции подклассов
      Также проверяется и при необходимости меняется статус Эпика*/

    public void deleteSubTaskByID(Integer subTaskID) {
        int parentID = this.subTaskMap.get(subTaskID).getParentId();
        this.subTaskMap.remove(subTaskID);
        this.epicTaskMap.get(parentID).getSubTasksMap().remove(subTaskID);
        ensureEpicStatus(parentID);
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

    //При обновлении подкласса проверяется и при необходимости оновляется статус соответствующего Эпика
    public void updateSubTask (SubTask task) {
        if (task.getID() != null && this.subTaskMap.get(task.getID()) != null) {
            this.subTaskMap.put(task.getID(),task);
            this.epicTaskMap.get(task.getParentId()).getSubTasksMap().put(task.getID(), task);
            ensureEpicStatus(task.getParentId());
        }
    }

    /*При обновлении Эпика запрещается обновление поля status.
     Также к новому Эпику привязываются существующие в старом подклассы. Таким образом при обновлении названия
     Эпика его подклассы не будут потеряны*/

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

    public HashMap<Integer, SubTask> getEpicSubTasks (Integer epicID) {
        return this.epicTaskMap.get(epicID).getSubTasksMap();
    }

    /* Так как уникальный идентификатор сделан сквозным для всех типов задач, за сохранение и
    обновление идентификатора отвечает один метод
     */
    private void saveNewTask(Task newTask, TaskType taskType) {
        newTask.setID(nextTaskID);
        newTask.setStatus(TaskStatus.NEW);

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

    //Данный метот вызывается при действиях с подзадачами, чтобы проверить и при необходимости изменить статус их Эпика
    private void ensureEpicStatus (Integer epicID) {
        EpicTask parentEpic = this.epicTaskMap.get(epicID);
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
        this.epicTaskMap.put(epicID,parentEpic);

    }
}
