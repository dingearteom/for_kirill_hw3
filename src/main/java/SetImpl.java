import java.io.Serializable;
import java.util.Iterator;
import java.util.*;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.stream.Collectors;

public class SetImpl<T extends Comparable<T>> implements Set<T> {
    private final Node head = new Node(null, new AtomicMarkableReference<>(null, false));

    @Override
    public boolean add(final T value) {
        final Node newNode = new Node(value, new AtomicMarkableReference<>(null, false));
        while (true) {
            final Map.Entry<Node, Node> pair = find(value);
            final Node prev = pair.getKey();
            final Node target = pair.getValue();
            if (target != null) {
                return false;
            }
            if (prev.next.compareAndSet(null, newNode, false, false)) {
                return true;
            }
        }
    }

    @Override
    public boolean remove(final T value) {
        while (true) {
            final Map.Entry<Node, Node> pair = find(value);
            final Node target = pair.getValue();
            if (target == null) {
                return false;
            }
            final Node next = target.next.getReference();
            if (target.next.compareAndSet(next, next, false, true)) {
                return true;
            }
        }
    }

    @Override
    public boolean contains(final T value) {
        Node curr = head.next.getReference();
        while (curr != null) {
            if (curr.value.equals(value) && !curr.next.isMarked()) {
                return true;
            }
            curr = curr.next.getReference();
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return getSnapshot().isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return getSnapshot().iterator();
    }

    private Map.Entry<Node, Node> find(final T value) {
        while (true) {
            boolean restart = false;
            Node curr = head;
            Node next = head.next.getReference();
            while (next != null && !restart) {
                final AtomicMarkableReference<Node> ref = next.next;
                if (ref.isMarked()) {
                    curr.next.compareAndSet(next, ref.getReference(), false, false);
                    restart = true;
                } else {
                    if (next.value.equals(value)) {
                        return new AbstractMap.SimpleEntry<>(curr, next);
                    }
                    curr = next;
                    next = ref.getReference();
                }
            }
            if (!restart) {
                return new AbstractMap.SimpleEntry<>(curr, null);
            }
        }
    }

    private List<T> getSnapshot() {
        while (true) {
            final List<Node> firstSnap = collect();
            final List<Node> secondSnap = collect();
            if (areSnapsEqual(firstSnap, secondSnap)) {
                return firstSnap.stream().map(node -> node.value).collect(Collectors.toList());
            }
        }
    }

    private boolean areSnapsEqual(final List<Node> first, final List<Node> second) {
        if (first.size() != second.size()) {
            return false;
        }
        for (int i = 0; i < first.size(); i++) {
            if (first.get(i) != second.get(i)) {
                return false;
            }
        }
        return true;
    }

    private List<Node> collect() {
        final List<Node> list = new ArrayList<Node>();
        Node curr = head.next.getReference();
        while (curr != null) {
            final Node value = curr;
            if (!curr.next.isMarked()) {
                list.add(value);
            }
            curr = curr.next.getReference();
        }
        return list;
    }

    private class Node {

        private final T value;
        private final AtomicMarkableReference<Node> next;

        Node(final T value, final AtomicMarkableReference<Node> next) {
            this.value = value;
            this.next = next;
        }
    }
}
