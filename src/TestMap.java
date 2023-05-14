public class TestMap {
    public static void main(String[] args) {
        BtreeMap<Integer, String> btree = new BtreeMap<>(3);
        Integer[] test = {5, 25, 50, 75, 1, 3, 7, 9, 11, 20, 26, 30, 37, 52, 58, 60, 80, 89};
        for (int i = 0; i < test.length; i++) {
            btree.insert(test[i], "a");
        }
        btree.printTree();

        btree.insert(70, "a");
        btree.printTree();

        btree.insert(55, "a");
        btree.printTree();

        btree.insert(62, "a");
        btree.printTree();

        btree.insert(4, "a");
        btree.printTree();

        System.out.println(btree.getValue(75));
        System.out.println(btree.getValue(90));

        System.out.println(btree.isExist(75));
        System.out.println(btree.isExist(90));

        System.out.println(btree.isEmpty());
        btree.clear();
        System.out.println(btree.isEmpty());

        Integer[] test2 = {50, 55, 5, 26, 35, 33, 29, 32, 38, 47, 65, 76, 2, 3, 11, 13, 62, 64, 70, 71, 88, 89, 92};
        for (int i = 0; i < test2.length; i++) {
            btree.insert(test2[i], "b");
        }
        btree.printTree();

        BtreeMap<Integer, String> btree2 = new BtreeMap<>(btree);
        btree2.printTree();

        btree.remove(35);
        btree.printTree();

        btree.remove(33);
        btree.printTree();

        btree.remove(50);
        btree.printTree();

        btree.remove(29);
        btree.printTree();

        btree.remove(32);
        btree.printTree();

        btree.remove(55);
        btree.printTree();

        btree2.printTree();

        System.out.println(btree2.getValue(33));
        btree2.setValue(33, "c");
        System.out.println(btree2.getValue(33));
        btree2.setValue(0, "c");
    }
}