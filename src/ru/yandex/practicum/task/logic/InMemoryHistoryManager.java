package ru.yandex.practicum.task.logic;

import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.TaskHistoryNode;
import ru.yandex.practicum.task.models.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{

    // мапа содержит все ранее просмотренные элементы, служит для удаления из LinkedList за O(1)
    private final Map<Long, TaskHistoryNode> inHistoryMap = new HashMap<>();
    // специальная реализация LinkedList с работы со списком просмотров
    final InMemoryHistoryManager.HistoryLinkedList<TaskHistoryNode> linkedHistory = this.new HistoryLinkedList<>();

    public static final long MAX_HISTORY_LENGTH = 10L;

    @Override
    public void add(Task task) {

        linkedHistory.linkLast(task);

    }

    @Override
    public List<Task> getHistory(){

        return linkedHistory.getTasks();

    }

    @Override
    public void remove(long id){
        if (inHistoryMap.get(id) != null) {
            if (inHistoryMap.get(id).getData() instanceof EpicTask){
                List<Long> subTaskIDs= ((EpicTask)inHistoryMap.get(id).getData()).getSubTasksIDsList();
                for (Long subID: subTaskIDs) {
                    if (inHistoryMap.get(subID) != null)
                        linkedHistory.removeNode(inHistoryMap.get(subID));
                }
            }
            linkedHistory.removeNode(inHistoryMap.get(id));

        }

    }


    //Внутренний класс, представляющий специальную реализацию LinkedList для работы с историей просмотров
     class HistoryLinkedList<E> {

        private TaskHistoryNode head;
        private TaskHistoryNode tail;
        private long size = 0L;

        //преобразует коллекцию HistoryLikedList с нодами в ArrayList с объектами Task и возвращает его
        private List<Task> getTasks() {
            List<Task> historyList = new ArrayList<>();
            TaskHistoryNode currNode = head;

            while (currNode != null) {
                historyList.add(currNode.getData());
                currNode = currNode.next;
            }

            return historyList;
        }

        /*реализует добавление "хвоста" в LinkedList с доп.условиями:
        1) Если в LinkedList уже 10 элементов, перед добавлением первый элемент удаляется
        2) Если в мапе просмотров уже есть элемент с таким ID, перед добавлением он удаляется
        3) Добавленный в LinkedList элемент также синхронно добавляется в мапу просмотров
         */
        private void linkLast(Task newTask) {

            TaskHistoryNode newNode = new TaskHistoryNode(tail, newTask, null);
            if (size >= MAX_HISTORY_LENGTH) {
                remove(head.getData().getID());
            }

            //случай если список пустой
            if (head == null) {
                head = newNode;
                tail = newNode;
                head.prev = null;
                tail.next = null;
            } else {

                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
                tail.next = null;
            }
            TaskHistoryNode repeatedNode = inHistoryMap.get(newTask.getID());

            if (repeatedNode != null) {
                removeNode(repeatedNode);
            }
            inHistoryMap.put(newTask.getID(), newNode);
            size++;
        }
        //удаляет ноду из LinkedList и мапы
        private void removeNode(TaskHistoryNode node) {

            if(node == head)
                head = node.next;
            else
                node.prev.next = node.next;
            if(node == tail)
                tail = node.prev;
            else
                node.next.prev = node.prev;

            inHistoryMap.remove(node.getData().getID());
            size--;

        }

    }

}
