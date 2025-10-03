package Entities;

import java.util.ArrayList;

public class HashTable {

    private AVLTree<Movie>[] hashTable;
    private int size;
    private int capacity;

    public HashTable() {
        this.capacity = nextPrime(11);
        this.size = 0;
        hashTable = new AVLTree[capacity];
        for (int i = 0; i < capacity; i++) {
            hashTable[i] = new AVLTree<>();
        }
    }

    public HashTable(int size) {
        this.capacity = nextPrime(size);
        this.size = 0;
        hashTable = new AVLTree[capacity];
        for (int i = 0; i < capacity; i++) {
            hashTable[i] = new AVLTree<>();
        }
    }

    private int hash(String title) {
        return Math.abs(title.hashCode()) % capacity;
    }

    public void insert(Movie movie) {
        int index = hash(movie.getTitle());
        if (hashTable[index].find(movie) == null) {
            hashTable[index].insert(movie);
            size++;
            if (avg() > 3)
                rehash();
        } else {
            hashTable[index].delete(movie);
            hashTable[index].insert(movie);
        }
    }

    public void delete(String title) {
        int index = hash(title);
        Movie dummyMovie = new Movie(title, "", 0, 0.0);
        if (hashTable[index].find(dummyMovie) != null) {
            hashTable[index].delete(dummyMovie);
            size--;
        }
    }

    public boolean search(String title) {
        int index = hash(title);
        Movie dummyMovie = new Movie(title, "", 0, 0.0);
        return hashTable[index].find(dummyMovie) != null;
    }

    public Movie get(String title) {
        int index = hash(title);
        Movie dummyMovie = new Movie(title, "", 0, 0.0);
        AVLTree<Movie>.Node node = hashTable[index].find(dummyMovie);
        if (node != null) {
            return node.data;
        }
        return null;
    }

    private double avg() {
        int totalHeight = 0;
        int totalTrees = 0;
        for (int i = 0; i < hashTable.length; i++) {
            if (hashTable[i].height() != 0) {
                totalTrees++;
                totalHeight += hashTable[i].height();
            }
        }
        if (totalTrees == 0)
            return 0;
        return (double) totalHeight / totalTrees;
    }

    private void rehash() {
        int prevCapacity = capacity;
        capacity = nextPrime(capacity * 2);
        AVLTree<Movie>[] prevTable = hashTable;

        hashTable = new AVLTree[capacity];
        for (int i = 0; i < capacity; i++) {
            hashTable[i] = new AVLTree<>();
        }
        size = 0;

        for (int i = 0; i < prevCapacity; i++) {
            ArrayList<Movie> list = prevTable[i].toList();
            for (Movie movie : list) {
                int index = hash(movie.getTitle());
                if (hashTable[index].find(movie) == null) {
                    hashTable[index].insert(movie);
                    size++;
                }
            }
        }
    }

    public void deallocate() {
        for (int i = 0; i < capacity; i++) {
            hashTable[i] = null;
        }
        hashTable = null;
        size = 0;
        capacity = 0;
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public AVLTree<Movie> getTree(int index) {
        return hashTable[index];
    }

    private int nextPrime(int n) {
        while (!isPrime(n))
            n++;
        return n;
    }

    private boolean isPrime(int n) {
        if (n < 2)
            return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    public int previousTree(int currentIndex) throws IllegalStateException {
        if (size == 0)
            throw new IllegalStateException("Table is Empty");

        int index = currentIndex;
        do {
            index = (index - 1 + hashTable.length) % hashTable.length;
            try {
                if (hashTable[index].height() > 0) {
                    return index;
                }
            } catch (NullPointerException e) {
                continue;
            }
        } while (index != currentIndex);

        throw new IllegalStateException("All Trees empty");
    }

    public int nextTree(int currentIndex) throws IllegalStateException {
        if (size == 0)
            throw new IllegalStateException("Hash table is empty");

        int index = currentIndex;
        do {
            index = (index + 1) % hashTable.length;
            if (hashTable[index].height() > 0) {
                return index;
            }
        } while (index != currentIndex);

        throw new IllegalStateException("All Trees empty");
    }

}