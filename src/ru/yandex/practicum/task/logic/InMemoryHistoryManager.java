package ru.yandex.practicum.task.logic;

import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.TaskHistoryNode;
import ru.yandex.practicum.task.models.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{

    // мапа содержит все ранее просмотренные элементы, служит для удаления из LinkedList за O(1)
    private final Map<Long, TaskHistoryNode> inHistoryNodes = new HashMap<>();
    // специальная реализация LinkedList с работы со списком просмотров
    private final InMemoryHistoryManager.HistoryLinkedList<TaskHistoryNode> linkedHistory = this.new HistoryLinkedList<>();

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
        if (inHistoryNodes.get(id) != null) {
            if (inHistoryNodes.get(id).getData() instanceof EpicTask){
                List<Long> subTaskIDs= ((EpicTask)inHistoryNodes.get(id).getData()).getEpicSubTasksIds();
                for (Long subID: subTaskIDs) {
                    if (inHistoryNodes.get(subID) != null)
                        linkedHistory.removeNode(inHistoryNodes.get(subID));
                }
            }
            linkedHistory.removeNode(inHistoryNodes.get(id));

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
                currNode = currNode.getNext();
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
                remove(head.getData().getTaskId());
            }

            //случай если список пустой
            if (head == null) {
                head = newNode;
                tail = newNode;
                head.setPrev(null);
                tail.setNext(null);
            } else {

                tail.setNext(newNode);
                newNode.setPrev(tail);
                tail = newNode;
                tail.setNext(null);
            }
            TaskHistoryNode repeatedNode = inHistoryNodes.get(newTask.getTaskId());

            if (repeatedNode != null) {
                removeNode(repeatedNode);
            }
            inHistoryNodes.put(newTask.getTaskId(), newNode);
            size++;
        }
        //удаляет ноду из LinkedList и мапы
        private void removeNode(TaskHistoryNode node) {

            if(node == head)
                head = node.getNext();
            else
                node.getPrev().setNext(node.getNext());
            if(node == tail)
                tail = node.getPrev();
            else
                node.getNext().setPrev(node.getPrev());

            inHistoryNodes.remove(node.getData().getTaskId());
            size--;

        }

    }

}
