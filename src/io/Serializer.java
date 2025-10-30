package io;

import java.io.*;
import java.util.List;
import model.Droid;

public class Serializer {
    public static void saveDroids(List<Droid> droids, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(droids);
            System.out.println("Дроїдів збережено у файл " + filename);
        } catch (IOException e) {
            System.err.println("Помилка збереження: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Droid> loadDroids(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<Droid>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Помилка завантаження: " + e.getMessage());
            return null;
        }
    }
}
