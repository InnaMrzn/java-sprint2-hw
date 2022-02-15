package ru.yandex.practicum.task.logic;

import ru.yandex.practicum.task.constants.TaskStatus;
import ru.yandex.practicum.task.constants.TaskType;
import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class TaskManager {

    private static long nextTaskID;
    private final HashMap<Long, Task> taskMap = new HashMap<>();
    private final HashMap<Long, EpicTask> epicTaskMap = new HashMap<>();
    private final HashMap<Long, SubTask> subTaskMap = new HashMap<>();

    public HashMap<Long, Task> getAllTasks() {
        return taskMap;
    }

    public HashMap<Long, EpicTask> getAllEpics() {
        return epicTaskMap;
    }

    public HashMap<Long, SubTask> getAllSubTasks() {
        return subTaskMap;
    }

    public void deleteAllTasks() {
        taskMap.clear();
    }

    //метод удаляет все подзадачи во всех эпиках, при этом статусы Эпиков обновляются на NEW
    public void deleteAllSubTasks() {
        subTaskMap.clear();
        for (Long nextEpicKey: epicTaskMap.keySet()){
            EpicTask nextEpicTask = epicTaskMap.get(nextEpicKey);
            nextEpicTask.getSubTasksMap().clear();
            nextEpicTask.setStatus(TaskStatus.NEW);
        }
    }

    // метод удаляет все эпики проекта, а также их подзадачи, так как подзадачи не могут быть без эпика
    public void deleteAllEpics () {
        epicTaskMap.clear();
        subTaskMap.clear();
    }

    public void deleteTaskByID(Long taskID) {
        taskMap.remove(taskID);
    }

    // метод удаляет конкретный эпик, а также удаляет из общей коллекции подклассов все подклассы данного эпика
    public void deleteEpicByID(Long taskID) {
        Collection<SubTask> subTasksCollection = subTaskMap.values();
        Iterator<SubTask> iterator = subTasksCollection.iterator();
        while (iterator.hasNext()){
            SubTask nextSubTaskKey = iterator.next();
            if ( nextSubTaskKey.getParentId() == taskID) {
                iterator.remove();
            }
        }
        epicTaskMap.remove(taskID);
    }

    /*метод кдаляет конкретный подкласс из коллекции подклассов Эпика и из общеф коллекции подклассов
      Также проверяется и при необходимости меняется статус Эпика*/

    public void deleteSubTaskByID(Long subTaskID) {
        long parentID = subTaskMap.get(subTaskID).getParentId();
        subTaskMap.remove(subTaskID);
        epicTaskMap.get(parentID).getSubTasksMap().remove(subTaskID);
        ensureEpicStatus(parentID);
    }


    public SubTask getSubTaskByID (Long id) {
       return subTaskMap.get(id);
    }

    public Task getTaskByID (Long id) {
        return taskMap.get(id);
    }

    public EpicTask getEpicTaskByID (Long id) {
        return epicTaskMap.get(id);
    }

    public void updateTask (Task task) {
        if (task.getID() != null && taskMap.get(task.getID()) != null) {
            taskMap.put(task.getID(),task);
        }
    }

    //При обновлении подкласса проверяется и при необходимости оновляется статус соответствующего Эпика
    public void updateSubTask (SubTask task) {
        if (task.getID() != null && subTaskMap.get(task.getID()) != null) {
            subTaskMap.put(task.getID(),task);
            epicTaskMap.get(task.getParentId()).getSubTasksMap().put(task.getID(), task);
            ensureEpicStatus(task.getParentId());
        }
    }

    /*При обновлении Эпика запрещается обновление поля status.
     Также к новому Эпику привязываются существующие в старом подклассы. Таким образом при обновлении названия
     Эпика его подклассы не будут потеряны*/

    public void updateEpicTask (EpicTask task) {

        if (task.getID() != null && epicTaskMap.get(task.getID()) != null) {
            EpicTask currentEpic = epicTaskMap.get(task.getID());
            HashMap<Long, SubTask> subTask = currentEpic.getSubTasksMap();
            task.setSubTasksMap(subTask);
            if (!(task.getStatus().equals(currentEpic.getStatus()))) {
                task.setStatus(currentEpic.getStatus());
                System.out.println("Поменять статус Эпика нельзя");
            }
            epicTaskMap.put(task.getID(),task);
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

    public HashMap<Long, SubTask> getEpicSubTasks (Long epicID) {
        return epicTaskMap.get(epicID).getSubTasksMap();
    }

    /* Так как уникальный идентификатор сделан сквозным для всех типов задач, за сохранение и
    обновление идентификатора отвечает один метод
     */
    private void saveNewTask(Task newTask, TaskType taskType) {
        newTask.setID(nextTaskID);
        newTask.setStatus(TaskStatus.NEW);

        switch (taskType){
            case TASK: {
                taskMap.put(nextTaskID,newTask);
                break;
            }
            case SUB_TASK: {
                SubTask subTask = (SubTask)newTask;
                if (epicTaskMap.get(subTask.getParentId()) != null) {
                    epicTaskMap.get(subTask.getParentId()).getSubTasksMap().put(nextTaskID, subTask);
                    subTaskMap.put(subTask.getID(), subTask);
                } else {
                    System.out.println("Эпик с номером "+ subTask.getParentId()+" не найден. Создание подзадачи невозможно");
                }
                break;
            }
            case EPIC: {
                epicTaskMap.put(nextTaskID,(EpicTask)newTask);
                break;
            }

        }
        nextTaskID++;
    }

    //Данный метот вызывается при действиях с подзадачами, чтобы проверить и при необходимости изменить статус их Эпика
    private void ensureEpicStatus (Long epicID) {
        EpicTask parentEpic = epicTaskMap.get(epicID);
        int doneCount=0;
        int newCount=0;

        if (parentEpic.getSubTasksMap().size()==0){
            parentEpic.setStatus(TaskStatus.NEW);
        } else {
            for (Long nextSubTask : parentEpic.getSubTasksMap().keySet()) {
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
        epicTaskMap.put(epicID,parentEpic);

    }
}
