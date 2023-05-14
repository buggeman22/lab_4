import java.util.ArrayList;

class Node<K extends Comparable<K>, V> {
    ArrayList<K> keys;
    ArrayList<V> values;
    ArrayList<Node<K, V>> children;

    Node(int t) {
        keys = new ArrayList<>(2 * t - 1);
        values = new ArrayList<>(2 * t - 1);
        children = new ArrayList<>(2 * t);
    }

    int keyBinarySearch(K key) {
        int left = 0;
        int right = keys.size() - 1;

        while (left <= right) {
            int mid = (left + right) >>> 1;
            K midKey = keys.get(mid);

            if (midKey.compareTo(key) < 0) {
                left = mid + 1;
            } else if (midKey.compareTo(key) > 0) {
                right = mid - 1;
            } else {
                return mid;
            }
        }

        return left;
    }

    int insertKey(K key) {
        keys.add(key);

        int i = keys.size() - 1;
        while (i > 0) {
            if (key.compareTo(keys.get(i - 1)) < 0) {
                K temp = keys.get(i - 1);
                keys.set(i - 1, key);
                keys.set(i, temp);

                i--;
            } else {
                break;
            }
        }

        return i;
    }
}


public class BtreeMap<K extends Comparable<K>, V> {
    private Node<K, V> root;
    private final int t;

    public BtreeMap(int t) {
        this.t = t;
    }

    public BtreeMap(BtreeMap<K, V> btree) {
        this.t = btree.t;

        if (btree.root != null) {
            root = new Node<>(t);
            recursiveCopy(root, btree.root);
        }
    }

    private Node<K, V> recursiveCopy(Node<K, V> destNode, Node<K, V> srcNode) {
        destNode.keys.addAll(srcNode.keys);
        destNode.values.addAll(srcNode.values);

        for (int i = 0; i < srcNode.children.size(); i++) {
            destNode.children.add(recursiveCopy(new Node<>(t), srcNode.children.get(i)));
        }

        return destNode;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        root = null;
    }

    public void insert(K key, V value) {
        if (root == null) {
            root = new Node<>(t);
            root.keys.add(key);
            root.values.add(value);
        } else {
            Node<K, V> parent = null;
            Node<K, V> son = root;

            while (son != null) {
                if (son.keys.size() == 2 * t - 1) {
                    if (parent == null) {
                        root = new Node<>(t);
                        root.children.add(son);
                        parent = root;
                    }

                    splitNode(parent, son);
                    son = parent.children.get(parent.keyBinarySearch(key));
                }

                if (son.children.size() != 0) {
                    parent = son;
                    son = son.children.get(son.keyBinarySearch(key));
                } else {
                    int index = son.insertKey(key);
                    son.values.add(index, value);
                    break;
                }
            }
        }
    }

    private void splitNode(Node<K, V> parent, Node<K, V> son) {
        int index = parent.insertKey(son.keys.get((2 * t - 1) / 2));
        son.keys.remove((2 * t - 1) / 2);

        parent.values.add(index, son.values.get((2 * t - 1) / 2));
        son.values.remove((2 * t - 1) / 2);

        Node<K, V> newSon = new Node<>(t);
        parent.children.add(index + 1, newSon);

        while (son.keys.size() > t - 1) {
            newSon.keys.add(son.keys.get(t - 1));
            son.keys.remove(t - 1);

            newSon.values.add(son.values.get(t - 1));
            son.values.remove(t - 1);

            if (son.children.size() != 0) {
                newSon.children.add(son.children.get(t));
                son.children.remove(t);
            }
        }
        if (son.children.size() != 0) {
            newSon.children.add(son.children.get(t));
            son.children.remove(t);
        }
    }

    public V getValue(K key) {
        Node<K, V> node = root;

        while (node != null) {
            int index = node.keyBinarySearch(key);

            if (index < node.keys.size() && node.keys.get(index).compareTo(key) == 0) {
                return node.values.get(index);
            }

            if (node.children.size() != 0) {
                node = node.children.get(index);
            } else {
                break;
            }
        }

        System.out.println("Node with this key does not exist!");
        return null;
    }

    public void setValue(K key, V value) {
        Node<K, V> node = root;

        while (node != null) {
            int index = node.keyBinarySearch(key);

            if (index < node.keys.size() && node.keys.get(index).compareTo(key) == 0) {
                node.values.set(index, value);
                return;
            }

            if (node.children.size() != 0) {
                node = node.children.get(index);
            } else {
                break;
            }
        }

        System.out.println("Node with this key does not exist!");
    }

    public boolean isExist(K key) {
        Node<K, V> node = root;

        while (node != null) {
            int index = node.keyBinarySearch(key);

            if (index < node.keys.size() && node.keys.get(index).compareTo(key) == 0) {
                return true;
            }

            if (node.children.size() != 0) {
                node = node.children.get(index);
            } else {
                break;
            }
        }

        return false;
    }

    public void remove(K key) {
        Node<K, V> parent = root;

        while (parent != null) {
            int index = parent.keyBinarySearch(key);

            if (index < parent.keys.size() && key.compareTo(parent.keys.get(index)) == 0) {
                if (parent.children.size() == 0) {
                    parent.keys.remove(index);
                    parent.values.remove(index);
                    return;
                } else {
                    Node<K, V> leftSon = parent.children.get(index);
                    Node<K, V> rightSon = parent.children.get(index + 1);

                    if (leftSon.keys.size() >= t) {
                        Node<K, V> replaceable = findReplaceable(leftSon, -1);
                        parent.keys.set(index, replaceable.keys.get(replaceable.keys.size() - 1));
                        parent.values.set(index, replaceable.values.get(replaceable.values.size() - 1));

                        key = replaceable.keys.get(replaceable.keys.size() - 1);
                        parent = leftSon;
                    } else if (rightSon.keys.size() >= t) {
                        Node<K, V> replaceable = findReplaceable(rightSon, 0);
                        parent.keys.set(index, replaceable.keys.get(0));
                        parent.values.set(index, replaceable.values.get(0));

                        key = replaceable.keys.get(0);
                        parent = rightSon;
                    } else {
                        merge(parent, index, index + 1);

                        parent = leftSon;
                    }
                }
            } else {
                if (parent.children.size() != 0) {
                    Node<K, V> destSon = parent.children.get(index);

                    if (destSon.keys.size() < t) {
                        Node<K, V> leftSrcSon = index - 1 >= 0 ? parent.children.get(index - 1) : null;
                        Node<K, V> rightSrcSon = index + 1 < parent.children.size() ? parent.children.get(index + 1) : null;

                        if ((leftSrcSon == null || leftSrcSon.keys.size() == t - 1)
                                && (rightSrcSon == null || rightSrcSon.keys.size() == t - 1)) {
                            merge(parent, index, leftSrcSon == null ? index + 1 : index - 1);
                        } else {
                            replace(parent, index, leftSrcSon == null || leftSrcSon.keys.size() == t - 1 ? index + 1 : index - 1);
                        }
                    }

                    parent = destSon;
                } else {
                    break;
                }
            }
        }

        System.out.println("Map does not have this key!");
    }

    private Node<K, V> findReplaceable(Node<K, V> node, int param) {
        while (node.children.size() != 0) {
            if (param == 0) {
                node = node.children.get(0);
            } else {
                node = node.children.get(node.children.size() - 1);
            }
        }

        return node;
    }

    private void replace(Node<K, V> parent, int destSonIndex, int srcSonIndex) {
        Node<K, V> destSon = parent.children.get(destSonIndex);
        Node<K, V> srcSon = parent.children.get(srcSonIndex);

        if (destSonIndex < srcSonIndex) {
            destSon.keys.add(parent.keys.get(destSonIndex));
            destSon.values.add(parent.values.get(destSonIndex));
            if (srcSon.children.size() != 0) {
                destSon.children.add(srcSon.children.get(0));
                srcSon.children.remove(0);
            }

            parent.keys.set(destSonIndex, srcSon.keys.get(0));
            parent.values.set(destSonIndex, srcSon.values.get(0));

            srcSon.keys.remove(0);
            srcSon.values.remove(0);
        } else {
            destSon.keys.add(0, parent.keys.get(srcSonIndex));
            destSon.values.add(0, parent.values.get(srcSonIndex));
            if (srcSon.children.size() != 0) {
                destSon.children.add(0, srcSon.children.get(srcSon.children.size() - 1));
                srcSon.children.remove(srcSon.children.size() - 1);
            }

            parent.keys.set(srcSonIndex, srcSon.keys.get(srcSon.keys.size() - 1));
            parent.values.set(srcSonIndex, srcSon.values.get(srcSon.values.size() - 1));

            srcSon.keys.remove(srcSon.keys.size() - 1);
            srcSon.values.remove(srcSon.values.size() - 1);
        }
    }

    private void merge(Node<K, V> parent, int destSonIndex, int srcSonIndex) {
        Node<K, V> destSon = parent.children.get(destSonIndex);
        Node<K, V> srcSon = parent.children.get(srcSonIndex);

        if (destSonIndex < srcSonIndex) {
            destSon.keys.add(parent.keys.get(destSonIndex));
            destSon.values.add(parent.values.get(destSonIndex));

            destSon.keys.addAll(srcSon.keys);
            destSon.values.addAll(srcSon.values);
            if (srcSon.children.size() != 0) {
                destSon.children.addAll(srcSon.children);
            }

            parent.keys.remove(destSonIndex);
            parent.values.remove(destSonIndex);
            parent.children.remove(srcSonIndex);
        } else {
            destSon.keys.add(0, parent.keys.get(srcSonIndex));
            destSon.values.add(0, parent.values.get(srcSonIndex));

            destSon.keys.addAll(0, srcSon.keys);
            destSon.values.addAll(0, srcSon.values);
            if (srcSon.children.size() != 0) {
                destSon.children.addAll(0, srcSon.children);
            }

            parent.keys.remove(srcSonIndex);
            parent.values.remove(srcSonIndex);
            parent.children.remove(srcSonIndex);
        }

        if (parent.keys.size() == 0) {
            root = destSon;
        }
    }

    public void printTree()
    {
        int level = 1;

        while (printLevel(root, level)) {
            level++;
            System.out.println();
        }
        System.out.println();
    }

    private boolean printLevel(Node<K, V> node, int level)
    {
        if (node == null) {
            return false;
        }

        if (level == 1) {
            System.out.print(node.keys + " ");

            return true;
        }

        boolean isLevelExist = false;
        for (int i = 0; i < node.children.size(); i++) {
            isLevelExist |= printLevel(node.children.get(i), level - 1);
        }

        return isLevelExist;
    }
}