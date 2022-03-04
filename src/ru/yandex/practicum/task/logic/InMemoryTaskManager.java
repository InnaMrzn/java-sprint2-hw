package ru.yandex.practicum.task.logic;

import ru.yandex.practicum.task.constants.TaskStatus;
import ru.yandex.practicum.task.constants.TaskType;
import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private static long nextTaskID;
    private final HashMap<Long, Task> taskMap = new HashMap<>();
    private final HashMap<Long, EpicTask> epicTaskMap = new HashMap<>();
    private final HashMap<Long, SubTask> subTaskMap = new HashMap<>();

    @Override
    public HashMap<Long, Task> getAllTasks() {

        return taskMap;
    }

    @Override
    public HashMap<Long, EpicTask> getAllEpics() {

        return epicTaskMap;
    }

    @Override
    public HashMap<Long, SubTask> getAllSubTasks() {

        return subTaskMap;
    }

    @Override
    public void deleteAllTasks() {

        taskMap.clear();
    }

    //метод удаляет список с ID  подзадач во всех эпиках, при этом статусы Эпиков обновляются на NEW
    @Override
    public void deleteAllSubTasks() {
        subTaskMap.clear();
        for (Long nextEpicKey: epicTaskMap.keySet()){
            EpicTask nextEpicTask = epicTaskMap.get(nextEpicKey);
            nextEpicTask.getSubTasksIDsList().clear();
            nextEpicTask.setStatus(TaskStatus.NEW);
        }
    }

    // метод удаляет все эпики проекта, а также их подзадачи, так как подзадачи не могут быть без эпика
    @Override
    public void deleteAllEpics () {
        epicTaskMap.clear();
        subTaskMap.clear();
    }

    @Override
    public void deleteTaskByID(Long taskID) {

        taskMap.remove(taskID);
    }

    // метод удаляет конкретный эпик, а также удаляет из общей коллекции подклассов все подклассы данного эпика
    @Override
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

    /*метод удаляет конкретный подкласс из коллекции ID подклассов Эпика и из общеф коллекции подклассов
      Также проверяется и при необходимости меняется статус Эпика*/
    @Override
    public void deleteSubTaskByID(Long subTaskID) {
        long parentID = subTaskMap.get(subTaskID).getParentId();
        subTaskMap.remove(subTaskID);
        List<Long> epicSubTaskIDs = epicTaskMap.get(parentID).getSubTasksIDsList();
        epicSubTaskIDs.remove(subTaskID);
        ensureEpicStatus(parentID);
    }

    @Override
    public SubTask getSubTaskByID (Long id) {
        return subTaskMap.get(id);
    }

    @Override
    public Task getTaskByID (Long id) {
        return taskMap.get(id);
    }

    @Override
    public EpicTask getEpicTaskByID (Long id) {
        return epicTaskMap.get(id);
    }

    @Override
    public void updateTask (Task task) {
        if (task.getID() != null && taskMap.get(task.getID()) != null) {
            taskMap.put(task.getID(),task);
        }
    }

    //При обновлении подкласса проверяется и при необходимости оновляется статус соответствующего Эпика
    @Override
    public void updateSubTask (SubTask task) {
        if (task.getID() != null && subTaskMap.get(task.getID()) != null) {
            subTaskMap.put(task.getID(),task);
            ensureEpicStatus(task.getParentId());
        }
    }

    /*При обновлении Эпика запрещается обновление поля status.
     Также к новому Эпику привязываются существующие в старом ID подклассов. Таким образом при обновлении названия
     Эпика ID его подклассов не будут потеряны*/
    @Override
    public void updateEpicTask (EpicTask updatedEpic) {

        if (updatedEpic.getID() != null && epicTaskMap.get(updatedEpic.getID()) != null) {
            EpicTask currentEpic = epicTaskMap.get(updatedEpic.getID());
            List<Long> subTaskIDs = currentEpic.getSubTasksIDsList();
            updatedEpic.setSubTasksIDsList(subTaskIDs);
            if (!(updatedEpic.getStatus().equals(currentEpic.getStatus()))) {
                updatedEpic.setStatus(currentEpic.getStatus());
                System.out.println("Поменять статус Эпика нельзя");
            }
            epicTaskMap.put(updatedEpic.getID(),updatedEpic);
        }
    }

    @Override
    public void createNewTask (Task task){
        saveNewTask(task, TaskType.TASK);
    }

    @Override
    public void createNewEpicTask (EpicTask task){
        saveNewTask(task, TaskType.EPIC);
    }

    @Override
    public void createNewSubTask (SubTask task){
        task.setParentId(task.getParentId());
        saveNewTask(task, TaskType.SUB_TASK);
        ensureEpicStatus(task.getParentId());
    }

    @Override
    public HashMap<Long, SubTask> getEpicSubTasks (Long epicID) {
        List<Long> subTasksIDs = epicTaskMap.get(epicID).getSubTasksIDsList();
        HashMap<Long, SubTask> epicSubTasks = new HashMap<>();
        for (Long subTaskID: subTasksIDs) {
            if (subTaskMap.get(subTaskID)!=null){
                epicSubTasks.put(subTaskID,subTaskMap.get(subTaskID));
            }
        }
        return epicSubTasks;
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
                    epicTaskMap.get(subTask.getParentId()).getSubTasksIDsList().add(subTask.getID());
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

        if (parentEpic.getSubTasksIDsList().size()==0){
            parentEpic.setStatus(TaskStatus.NEW);
        } else {
            for (Long nextSubTaskID : parentEpic.getSubTasksIDsList()) {
                if (subTaskMap.get(nextSubTaskID).getStatus().equals(TaskStatus.NEW))
                    newCount++;
                if (subTaskMap.get(nextSubTaskID).getStatus().equals(TaskStatus.DONE))
                    doneCount++;
            }
            if (newCount == parentEpic.getSubTasksIDsList().size())
                parentEpic.setStatus(TaskStatus.NEW);
            else if (doneCount == parentEpic.getSubTasksIDsList().size())
                parentEpic.setStatus(TaskStatus.DONE);
            else
                parentEpic.setStatus(TaskStatus.IN_PROCESS);
        }
        epicTaskMap.put(epicID,parentEpic);

    }
}
