package ru.yandex.practicum.task.logic;

import ru.yandex.practicum.task.constants.TaskStatus;
import ru.yandex.practicum.task.constants.TaskType;
import ru.yandex.practicum.task.exception.ManagerSaveException;
import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import static ru.yandex.practicum.task.logic.Managers.timeFormatter;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private Path backupPath;

    /*конструктор делаем приватным, чтобы нельзя было создать этот менеджер с помощь New,
    он создается с помощью файла Managers есть переменная isBacked == true
    */

    public FileBackedTaskManager(Path filePath){
        super();
        backupPath = filePath;
        try {
            if (Files.exists(backupPath)) {
                String fileContent = Files.readString(backupPath, StandardCharsets.UTF_8);
                loadFromStorage(fileContent);

            } else {
                Files.createFile(backupPath);
            }

        } catch (IOException e) {
            throw new ManagerSaveException( "Невозможно прочитать файл или создать новый.");
        }



    }

    public FileBackedTaskManager()
    {

    }

    /* переопределенные методы родительского класса. К каждому такому методу добавляется сохранение состояния.
    для операций изменения (создание, обновление, удаление) состояние записывается после совершения операции.
    Для операций просмотра состояние записывается непосредственно до (согласовано с наставником)
     */
    @Override
    public void deleteAllTasks(){
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteTaskById(Long taskID) {
        super.deleteTaskById(taskID);
        save();
    }

    @Override
    public void deleteEpicById(Long taskID) {
        super.deleteEpicById(taskID);
        save();
    }

    @Override
    public void deleteSubTaskById(Long subTaskID) {
        super.deleteSubTaskById(subTaskID);
        save();
    }

    @Override
    public SubTask getSubTaskById (Long id) {
        final SubTask result = super.getSubTaskById(id);
        save();
        return result;

    }

    @Override
    public Task getTaskById (Long id){
        final Task result = super.getTaskById(id);
        save();
        return result;
    }

    @Override
    public EpicTask getEpicTaskById (Long id) {
        final EpicTask result = super.getEpicTaskById(id);
        save();
        return result;
    }

    @Override
    public void updateTask (Task task){
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask (SubTask task){
        super.updateSubTask(task);
        save();
    }

    @Override
    public void updateEpicTask (EpicTask updatedEpic){
        super.updateEpicTask(updatedEpic);
        save();
    }

    @Override
    public Task createNewTask (Task task){
        Task result = super.createNewTask(task);
        save();
        return result;
    }

    @Override
    public EpicTask createNewEpicTask (EpicTask task){
        EpicTask result = super.createNewEpicTask(task);
        save();
        return result;
    }

    @Override
    public SubTask createNewSubTask (SubTask task){
        SubTask result = super.createNewSubTask(task);
        save();
        return result;
    }


    /* данный метод вызывается при запуске программы один раз, восстанавливая состояние менеджера
    из файла бекапа
     */
    protected void loadFromStorage(String storageContent){
        String[] arr = storageContent.split("\n");
        for (int i=1; i< arr.length; i++){

            /*пустая строка является признаком того, что надо присвоить текущий ID задачи,
            загрузить объекты истории просмотров и выйти из цикла*/
            if (arr[i].isEmpty() || arr[i].isBlank()) {
                currentTaskId = Long.parseLong(arr[i-1].substring(0,arr[i-1].indexOf(',')));
                loadHistoryFromStorage(arr[i+1]);
                break;
            }
            Task task = taskFromString(arr[i]);
            switch (task.getClass().getSimpleName()) {
                case "Task":
                    tasks.put(task.getTaskId(), task);
                    prioritizedTasks.add(task);
                    checkAndMarkScheduler(task);
                    break;
                case "EpicTask":
                    epicTasks.put(task.getTaskId(), (EpicTask) task);
                    break;
                case "SubTask":
                    subTasks.put(task.getTaskId(), (SubTask) task);
                    prioritizedTasks.add(task);
                    checkAndMarkScheduler(task);
                    break;
            }
        }

        /*если истории просмотров еще не было, ориентируемся на символ \n в конце*/
        if ((storageContent.lastIndexOf("\n") >=0) && (storageContent.lastIndexOf("\n") == storageContent.length()-1)){
           currentTaskId = Long.parseLong(arr[arr.length-1].substring(0,arr[arr.length-1].indexOf(',')));
        }
    }

    //метод сохраняет текущее состояние менеджера в файл в указанном формате.
    protected void save (){

        try (BufferedWriter writer  = new BufferedWriter
                (new FileWriter(backupPath.toAbsolutePath().toString(), StandardCharsets.UTF_8))){
            writer.write(this.toString());

        } catch (IOException e) {
            throw new ManagerSaveException( "Невозможно сохранить в указанный файл.");
        }

    }

    //переопределяет метод toString() согласно формату файла CSV
    @Override
    public String toString(){
        TreeMap<Long, Task> allTasks = new TreeMap<>();
        allTasks.putAll(tasks);
        allTasks.putAll(epicTasks);
        allTasks.putAll(subTasks);
        StringBuilder builder = new StringBuilder("id,type,name,status,description,startTime,duration, epicID\n");
        for (Long id: allTasks.keySet()){
            Task task = allTasks.get(id);
            Optional<LocalDateTime> timeOptional  = Optional.ofNullable(task.getStartTime());
            String timeString = "";
            String parentID="";
            if (timeOptional.isPresent())
                timeString = timeFormatter.format(timeOptional.get());
            String taskType ="";
            if (task.getClass().getSimpleName().equals("Task"))
                taskType=TaskType.TASK.toString();
            else if (task.getClass().getSimpleName().equals("SubTask")) {
                taskType = TaskType.SUBTASK.toString();
                parentID = Long.toString(((SubTask)task).getParentId());
            } else if (task.getClass().getSimpleName().equals("EpicTask"))
                taskType=TaskType.EPIC.toString();
            builder.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s%n",
                            task.getTaskId(), taskType, task.getName(), task.getStatus(),task.getDescription(),
                            timeString, task.getDuration(), parentID));

        }

        builder.append("\n");
        for (Task historyTask: historyManager.getHistory()) {
            builder.append(String.format("%d,",historyTask.getTaskId()));
        }
        return builder.toString();

    }

    //создает объект Task из строчки файла
    private Task taskFromString (String line){

        String[] elements = line.split(",");
        Task task = null;

        if (elements[1].equals(TaskType.TASK.toString())) {
            task = new Task(elements[2].trim(), elements[4].trim(), TaskStatus.valueOf(elements[3].trim()));
        } else if (elements[1].equals(TaskType.EPIC.toString())) {
            task = new EpicTask(elements[2].trim(), elements[4].trim());
        } else if (elements[1].equals(TaskType.SUBTASK.toString())) {
            task = new SubTask(elements[2].trim(), elements[4], TaskStatus.valueOf(elements[3].trim()), Long.parseLong(elements[7].trim()));
        }
        task.setTaskId(Long.parseLong(elements[0].trim()));
        if (!elements[5].isBlank()&& !elements[5].isEmpty())
            task.setStartTime(LocalDateTime.parse(elements[5], timeFormatter));
        if (!elements[6].isBlank()&& !elements[6].isEmpty())
            task.setDuration(Long.parseLong(elements[6].trim()));

        return task;
    }

    //загружает историю задач из последней строчки файла с IDs просмотров
    protected void loadHistoryFromStorage(String historyIdsString){
        String[] historyIDs = historyIdsString.split(",");
        for (String nextID: historyIDs) {
            Task task = null;
            Long id = Long.parseLong(nextID.trim());
            if (tasks.get(id) != null)
                task = tasks.get(id);
            else if (subTasks.get(id) != null)
                task = subTasks.get(id);
            else if (epicTasks.get(id) != null)
                task = epicTasks.get(id);
            historyManager.add(task);
        }
    }

    private static void printAllTasks(TaskManager taskManager) {

        for (Task nextTask: taskManager.getAllTasks().values()){
            System.out.println(nextTask);
        }
        System.out.println("\n");


        for (EpicTask nextEpic: taskManager.getAllEpics().values()){
            System.out.println(nextEpic);
            List<Long> nextSubTasksIDsList = nextEpic.getEpicSubTasksIds();
            System.out.println("Subtasks: ");
            for (Long nextSubTaskID: nextSubTasksIDsList) {
                System.out.print ("    "+nextSubTaskID);
            }
            System.out.println();

        }

        for (SubTask nextTask: taskManager.getAllSubTasks().values()){
            System.out.println(nextTask);
        }
    }

}
