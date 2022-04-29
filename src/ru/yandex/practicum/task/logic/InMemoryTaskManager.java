package ru.yandex.practicum.task.logic;

import ru.yandex.practicum.task.constants.TaskStatus;
import ru.yandex.practicum.task.constants.TaskType;
import ru.yandex.practicum.task.exception.TimeIsBusyException;
import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ru.yandex.practicum.task.logic.Managers.*;

public class InMemoryTaskManager implements TaskManager {

    protected static long nextTaskID;
    protected final HashMap<Long, Task> taskMap = new HashMap<>();
    protected final HashMap<Long, EpicTask> epicTaskMap = new HashMap<>();
    protected final HashMap<Long, SubTask> subTaskMap = new HashMap<>();
    protected HistoryManager historyMgr = Managers.getDefaultHistory();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(new Comparator<>() {
        @Override
        public int compare(Task o1, Task o2) {

            if (o1.getStartTime() == null)
                return 1;
            if (o2.getStartTime() == null)
                return -1;
            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    });

    @Override
    public long getNextTaskID(){
        return nextTaskID;
    }

    @Override
    public HistoryManager getHistoryManager(){

        return historyMgr;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks(){
        return prioritizedTasks;
    }

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
        for (Long id: taskMap.keySet()){
            historyMgr.remove(id);
        }
        for (Long taskID: taskMap.keySet()){
            Task current = taskMap.get(taskID);
            clearTimeIntervals(current);
        }
        taskMap.clear();

    }

    //метод удаляет список с ID  подзадач во всех эпиках, при этом статусы Эпиков обновляются на NEW
    @Override
    public void deleteAllSubTasks() {
        for (Long id: subTaskMap.keySet()){
            historyMgr.remove(id);
        }
        for (Long taskID: subTaskMap.keySet()){
            SubTask current = subTaskMap.get(taskID);
            clearTimeIntervals(current);
        }
        subTaskMap.clear();
        for (Long nextEpicKey: epicTaskMap.keySet()){
            EpicTask nextEpicTask = epicTaskMap.get(nextEpicKey);
            nextEpicTask.getSubTasksIDsList().clear();
            nextEpicTask.setStatus(TaskStatus.NEW);
            nextEpicTask.setStartTime(null);
            nextEpicTask.setDuration(0L);
        }
    }

    // метод удаляет все эпики проекта, а также их подзадачи, так как подзадачи не могут быть без эпика
    @Override
    public void deleteAllEpics () {
        for (Long id: epicTaskMap.keySet()){
            historyMgr.remove(id);
        }
        epicTaskMap.clear();
        subTaskMap.clear();
    }

    @Override
    public void deleteTaskByID(Long taskID) {
        clearTimeIntervals(taskMap.get(taskID));
        taskMap.remove(taskID);
        historyMgr.remove(taskID);
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
        historyMgr.remove(taskID);
    }

    /*метод удаляет конкретный подкласс из коллекции ID подклассов Эпика и из общеф коллекции подклассов
      Также проверяется и при необходимости меняется статус Эпика*/
    @Override
    public void deleteSubTaskByID(Long subTaskID) {
        long parentID = subTaskMap.get(subTaskID).getParentId();
        ensureEpic(parentID, subTaskID);
        clearTimeIntervals(subTaskMap.get(subTaskID));
        subTaskMap.remove(subTaskID);
        historyMgr.remove(subTaskID);
        List<Long> epicSubTaskIDs = epicTaskMap.get(parentID).getSubTasksIDsList();
        epicSubTaskIDs.remove(subTaskID);

    }

    @Override
    public SubTask getSubTaskByID (Long id) {
        if (subTaskMap.get(id)!=null)
            historyMgr.add(subTaskMap.get(id));
        return subTaskMap.get(id);
    }

    @Override
    public Task getTaskByID (Long id) {
        if (taskMap.get(id)!=null)
            historyMgr.add(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public EpicTask getEpicTaskByID (Long id) {
        if (epicTaskMap.get(id)!=null)
            historyMgr.add(epicTaskMap.get(id));
        return epicTaskMap.get(id);
    }

    @Override
    public void updateTask (Task task) {
        if (task.getID() != null && taskMap.get(task.getID()) != null) {
            try {
                checkAndMarkScheduler(task);
             } catch (TimeIsBusyException ex){
                    System.out.println(ex.getMessage());
            }
            taskMap.put(task.getID(),task);
            prioritizedTasks.add(task);
        }
    }

    //При обновлении подкласса проверяется и при необходимости оновляется статус соответствующего Эпика
    @Override
    public void updateSubTask (SubTask task) {
        if (task.getID() != null && subTaskMap.get(task.getID()) != null) {
            try {
                checkAndMarkScheduler(task);
            } catch (TimeIsBusyException ex){
                System.out.println(ex.getMessage());
            }
            subTaskMap.put(task.getID(),task);
            prioritizedTasks.add(task);
            ensureEpic(task.getParentId(), task.getID());
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
            if (updatedEpic.getStartTime()!= null &&
                    !(updatedEpic.getStartTime().equals(currentEpic.getStartTime()))) {
                if (currentEpic.getStartTime()!= null)
                    updatedEpic.setStartTime(currentEpic.getStartTime());
                else updatedEpic.setStartTime(null);
                System.out.println("Поменять дату начала Эпика нельзя");
            }
            if (updatedEpic.getDuration() != currentEpic.getDuration()) {
                updatedEpic.setDuration(currentEpic.getDuration());
                System.out.println("Поменять продолжительность Эпика нельзя");
            }
            epicTaskMap.put(updatedEpic.getID(),updatedEpic);
        }
    }

    @Override
    public long createNewTask (Task task){
        long taskID =-1;
        try {
            taskID = saveNewTask(task, TaskType.TASK);
        } catch (TimeIsBusyException ex){
            System.out.println(ex.getMessage());
        }
        return taskID;
    }

    @Override
    public long createNewEpicTask (EpicTask task){
        long taskID =-1;
        try{
            taskID = saveNewTask(task, TaskType.EPIC);
        } catch (TimeIsBusyException ex){
            System.out.println(ex.getMessage());
        }
        return taskID;
    }

    @Override
    public long createNewSubTask (SubTask task){
        task.setParentId(task.getParentId());
        long taskID =-1;
        try {
            taskID = saveNewTask(task, TaskType.SUBTASK);
            ensureEpic(task.getParentId(),task.getID());
        } catch (TimeIsBusyException ex){
            System.out.println(ex.getMessage());
        }

        return taskID;
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
    private long saveNewTask(Task newTask, TaskType taskType) throws TimeIsBusyException {
        newTask.setID(nextTaskID);
        newTask.setStatus(TaskStatus.NEW);

        switch (taskType){
            case TASK: {
                taskMap.put(nextTaskID,newTask);
                prioritizedTasks.add(newTask);
                checkAndMarkScheduler(newTask);
                break;
            }
            case SUBTASK: {
                SubTask subTask = (SubTask)newTask;
                if (epicTaskMap.get(subTask.getParentId()) != null) {
                    epicTaskMap.get(subTask.getParentId()).getSubTasksIDsList().add(subTask.getID());
                    subTaskMap.put(subTask.getID(), subTask);
                    prioritizedTasks.add(subTask);
                    checkAndMarkScheduler(newTask);
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
        return nextTaskID++;
    }

    //Данный метот вызывается при действиях с подзадачами, чтобы проверить и при необходимости изменить расчетные поля Эпика
    private void ensureEpic (Long epicID, Long subTaskID){
        EpicTask parentEpic = epicTaskMap.get(epicID);
        SubTask subTask = subTaskMap.get(subTaskID);
        ensureEpicStatus (parentEpic);
        ensureEpicTime (parentEpic, subTask);
        epicTaskMap.put(epicID,parentEpic);

    }

    private void ensureEpicTime (EpicTask parentEpic, SubTask subTask){

        if (subTask.getStartTime() != null){
            if (parentEpic.getStartTime() == null) {
                parentEpic.setStartTime(subTask.getStartTime());
                parentEpic.setDuration(subTask.getDuration());
            } else {
                if (parentEpic.getStartTime().isAfter(subTask.getStartTime()))
                    parentEpic.setStartTime(subTask.getStartTime());
                parentEpic.setDuration(parentEpic.getDuration()+subTask.getDuration());
            }

            if (parentEpic.getEndTime() == null) {
                parentEpic.setEndTime(subTask.getEndTime());
            } else {
                if (parentEpic.getEndTime().isBefore(subTask.getEndTime()))
                    parentEpic.setEndTime(subTask.getEndTime());
            }
        }

    }


    private void ensureEpicStatus (EpicTask parentEpic) {
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

    }

    //Данный метод помечает в мапе расписания слоты = false при загрузке задач из файла или их создании/обновлении
    protected void checkAndMarkScheduler (Task task) throws TimeIsBusyException{

         if (task.getStartTime()!=null && task.getDuration()>0){
            clearTimeIntervals(task);
            LocalDateTime startBlockedTime = task.getStartTime().minusMinutes(task.getStartTime().getMinute() % 15);
            LocalDateTime endBlockedTime = task.getEndTime().plusMinutes(15 - (task.getEndTime().getMinute() % 15));

            while (startBlockedTime.isBefore(endBlockedTime)) {
                Long blockedPoint = Long.parseLong(startBlockedTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
                if (schedule.get(blockedPoint) != null && schedule.get(blockedPoint) == false){
                    throw new TimeIsBusyException("Невозможно сохранить задачу с началом "
                            + timeFormatter.format(task.getStartTime()) + " и продолжительностью " +
                            task.getDuration() + " мин. \nИнтервал с " + timeFormatter.format(startBlockedTime) + " уже занят.");
                 }
                schedule.put(blockedPoint,false);
                task.getBlockedTimeIntervals().add(blockedPoint);
                startBlockedTime = startBlockedTime.plusMinutes(15);
            }
        }

    }

    protected void clearTimeIntervals (Task task){
        for (Long interval: task.getBlockedTimeIntervals()){
            schedule.put(interval,true);
        }
        task.getBlockedTimeIntervals().clear();

    }



}
