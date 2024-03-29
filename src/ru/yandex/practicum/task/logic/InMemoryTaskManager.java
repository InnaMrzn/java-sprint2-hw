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

    protected long currentTaskId;
    protected final HashMap<Long, Task> tasks = new HashMap<>();
    protected final HashMap<Long, EpicTask> epicTasks = new HashMap<>();
    protected final HashMap<Long, SubTask> subTasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    transient protected TreeSet<Task> prioritizedTasks = new TreeSet<>((o1, o2) -> {
        if (o1.getStartTime() == null)
            return 1;
        if (o2.getStartTime() == null)
            return -1;
        return o1.getStartTime().compareTo(o2.getStartTime());

    });

    transient protected  HashMap<Long, HashSet<Long>> blockedTimeIntervals = new HashMap<>();

    @Override
    public List<Task> getHistory(){

        return new ArrayList<Task>(historyManager.getHistory());
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks(){
        return prioritizedTasks;
    }

    @Override
    public Map<Long, Task> getAllTasks() {
        return new HashMap<>(tasks);
    }

    @Override
    public Map<Long, EpicTask> getAllEpics() {

        return new HashMap<>(epicTasks);
    }

    @Override
    public Map<Long, SubTask> getAllSubTasks() {

        return new HashMap<>(subTasks);
    }

    @Override
    public void deleteAllTasks() {
        for (Task current: tasks.values()){
            Long taskId = current.getTaskId();
            historyManager.remove(taskId);
            clearTimeIntervals(current);
        }
        tasks.clear();

    }

    //метод удаляет список с ID  подзадач во всех эпиках, при этом статусы Эпиков обновляются на NEW
    @Override
    public void deleteAllSubTasks() {
        for (SubTask current: subTasks.values()){
            Long taskId = current.getTaskId();
            historyManager.remove(taskId);
            clearTimeIntervals(current);
        }
        subTasks.clear();
        for (EpicTask nextEpicTask: epicTasks.values()){
            nextEpicTask.getEpicSubTasksIds().clear();
            nextEpicTask.setStatus(TaskStatus.NEW);
            nextEpicTask.setStartTime(null);
            nextEpicTask.setDuration(0L);
        }
    }

    // метод удаляет все эпики проекта, а также их подзадачи, так как подзадачи не могут быть без эпика
    @Override
    public void deleteAllEpics () {
        for (EpicTask epic: epicTasks.values()){
            Long epicId = epic.getTaskId();
            for (Long subTaskId: epic.getEpicSubTasksIds()){
                historyManager.remove(subTaskId);
            }
            historyManager.remove(epicId);
        }
        epicTasks.clear();
        subTasks.clear();
    }

    @Override
    public void deleteTaskById(Long taskID) {
        clearTimeIntervals(tasks.get(taskID));
        tasks.remove(taskID);
        historyManager.remove(taskID);
    }

    // метод удаляет конкретный эпик, а также удаляет из общей коллекции подзадач все подзадачи данного эпика
    @Override
    public void deleteEpicById(Long taskID) {
        EpicTask epic = epicTasks.get(taskID);
        if(epic!=null){
            for (Long subtaskId : epic.getEpicSubTasksIds()) {
                subTasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epicTasks.remove(taskID);
            historyManager.remove(taskID);
        }
    }

    /*метод удаляет конкретный подкласс из коллекции ID подклассов Эпика и из общеф коллекции подклассов
      Также проверяется и при необходимости меняется статус Эпика*/
    @Override
    public void deleteSubTaskById(Long subTaskID) {
        long parentID = subTasks.get(subTaskID).getParentId();
        ensureEpic(parentID, subTaskID);
        clearTimeIntervals(subTasks.get(subTaskID));
        subTasks.remove(subTaskID);
        historyManager.remove(subTaskID);
        List<Long> epicSubTaskIDs = epicTasks.get(parentID).getEpicSubTasksIds();
        epicSubTaskIDs.remove(subTaskID);

    }

    @Override
    public SubTask getSubTaskById (Long id) {
        SubTask subTask = subTasks.get(id);
        if (subTask!=null)
            historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Task getTaskById (Long id) {
        if (tasks.get(id)!=null)
            historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public EpicTask getEpicTaskById (Long id) {
        if (epicTasks.get(id)!=null)
            historyManager.add(epicTasks.get(id));
        return epicTasks.get(id);
    }

    @Override
    public void updateTask (Task task) {
        if (task.getTaskId() != null && tasks.get(task.getTaskId()) != null) {
             checkAndMarkScheduler(task);
            tasks.put(task.getTaskId(),task);
            prioritizedTasks.add(task);
        }
    }

    //При обновлении подкласса проверяется и при необходимости оновляется статус соответствующего Эпика
    @Override
    public void updateSubTask (SubTask task) {
        if (task.getTaskId() != null && subTasks.get(task.getTaskId()) != null) {
            checkAndMarkScheduler(task);
            subTasks.put(task.getTaskId(),task);
            prioritizedTasks.add(task);
            ensureEpic(task.getParentId(), task.getTaskId());
        }
    }

    /*При обновлении Эпика запрещается обновление поля status.
     Также к новому Эпику привязываются существующие в старом ID подклассов. Таким образом при обновлении названия
     Эпика ID его подклассов не будут потеряны*/
    @Override
    public void updateEpicTask (EpicTask updatedEpic) {

        if (updatedEpic.getTaskId() != null && epicTasks.get(updatedEpic.getTaskId()) != null) {
            EpicTask currentEpic = epicTasks.get(updatedEpic.getTaskId());
            List<Long> subTaskIDs = currentEpic.getEpicSubTasksIds();
            updatedEpic.setEpicSubTasksIds(subTaskIDs);
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
            epicTasks.put(updatedEpic.getTaskId(),updatedEpic);
        }
    }

    @Override
    public Task createNewTask (Task task){

        return saveNewTask(task, TaskType.TASK);
    }

    @Override
    public EpicTask createNewEpicTask (EpicTask task){

        return (EpicTask)saveNewTask(task, TaskType.EPIC);
    }
    @Override
    public SubTask createNewSubTask (SubTask task){
        SubTask resultTask = (SubTask) saveNewTask(task, TaskType.SUBTASK);
        ensureEpic(task.getParentId(),task.getTaskId());

        return resultTask;
    }

    @Override
    public HashMap<Long, SubTask> getEpicSubTasks (Long epicID) {
        List<Long> subTasksIDs = epicTasks.get(epicID).getEpicSubTasksIds();
        HashMap<Long, SubTask> epicSubTasks = new HashMap<>();
        for (Long subTaskID: subTasksIDs) {
            if (subTasks.get(subTaskID)!=null){
                epicSubTasks.put(subTaskID, subTasks.get(subTaskID));
            }
        }
        return new HashMap<Long, SubTask> (epicSubTasks);
    }


    /* Так как уникальный идентификатор сделан сквозным для всех типов задач, за сохранение и
    обновление идентификатора отвечает один метод
     */
    private Task saveNewTask(Task newTask, TaskType taskType) throws TimeIsBusyException {
        currentTaskId++;
        newTask.setTaskId(currentTaskId);
        switch (taskType){
            case TASK: {
                tasks.put(currentTaskId,newTask);
                prioritizedTasks.add(newTask);
                checkAndMarkScheduler(newTask);
                break;
            }
            case SUBTASK: {
                SubTask subTask = (SubTask)newTask;
                if (epicTasks.get(subTask.getParentId()) != null) {
                    epicTasks.get(subTask.getParentId()).getEpicSubTasksIds().add(subTask.getTaskId());
                    subTasks.put(subTask.getTaskId(), subTask);
                    prioritizedTasks.add(subTask);
                    checkAndMarkScheduler(newTask);
                } else {
                    System.out.println("Эпик с номером "+ subTask.getParentId()+" не найден. Создание подзадачи невозможно");
                }
                break;
            }
            case EPIC: {
                newTask.setStatus(TaskStatus.NEW);
                epicTasks.put(currentTaskId,(EpicTask)newTask);
                break;
            }

        }
        return newTask;
    }

    //Данный метот вызывается при действиях с подзадачами, чтобы проверить и при необходимости изменить расчетные поля Эпика
    private void ensureEpic (Long epicID, Long subTaskID){
        EpicTask parentEpic = epicTasks.get(epicID);
        SubTask subTask = subTasks.get(subTaskID);
        ensureEpicStatus (parentEpic);
        ensureEpicTime (parentEpic, subTask);
        epicTasks.put(epicID,parentEpic);

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
        if (parentEpic.getEpicSubTasksIds().size()==0){
            parentEpic.setStatus(TaskStatus.NEW);
        } else {
            for (Long nextSubTaskID : parentEpic.getEpicSubTasksIds()) {
                if (subTasks.get(nextSubTaskID).getStatus().equals(TaskStatus.NEW))
                    newCount++;
                if (subTasks.get(nextSubTaskID).getStatus().equals(TaskStatus.DONE))
                    doneCount++;
            }
            if (newCount == parentEpic.getEpicSubTasksIds().size())
                parentEpic.setStatus(TaskStatus.NEW);
            else if (doneCount == parentEpic.getEpicSubTasksIds().size())
                parentEpic.setStatus(TaskStatus.DONE);
            else
                parentEpic.setStatus(TaskStatus.IN_PROCESS);
        }

    }

    //Данный метод помечает в мапе расписания слоты = false при загрузке задач из файла или их создании/обновлении
    protected void checkAndMarkScheduler (Task task) throws TimeIsBusyException{

         if (task.getStartTime()!=null && task.getDuration()>0){
            blockedTimeIntervals.putIfAbsent(task.getTaskId(), new HashSet<>());
            clearTimeIntervals(task);
            LocalDateTime startBlockedTime = task.getStartTime().minusMinutes(task.getStartTime().getMinute() % 15);
            LocalDateTime endBlockedTime = task.getEndTime().plusMinutes(15 - (task.getEndTime().getMinute() % 15));

            while (startBlockedTime.isBefore(endBlockedTime)) {
                Long blockedPoint = Long.parseLong(startBlockedTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
                if (schedule.get(blockedPoint) != null && !schedule.get(blockedPoint)){
                    throw new TimeIsBusyException("Невозможно сохранить задачу с началом "
                            + timeFormatter.format(task.getStartTime()) + " и продолжительностью " +
                            task.getDuration() + " мин. \nИнтервал с " + timeFormatter.format(startBlockedTime) + " уже занят.");
                 }
                schedule.put(blockedPoint,false);
                blockedTimeIntervals.get(task.getTaskId()).add(blockedPoint);
                blockedTimeIntervals.put(task.getTaskId(),blockedTimeIntervals.get(task.getTaskId()));
                startBlockedTime = startBlockedTime.plusMinutes(15);
            }
        }

    }

    protected void clearTimeIntervals (Task task){
            HashSet<Long> taskBlockedIntervals = blockedTimeIntervals.get(task.getTaskId());
            if (taskBlockedIntervals !=null) {
                for (Long interval : taskBlockedIntervals) {
                    schedule.put(interval, true);
                }
                blockedTimeIntervals.get(task.getTaskId()).clear();
            }
    }



}
