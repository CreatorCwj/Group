package com.example;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * GreenDao生成类
 */
public class Generator {

    public static void main(String[] args) throws IOException {
        Schema schema = new Schema(1, "com.dao.generate");
        addCity(schema);
        addArea(schema);
        addCategory(schema);
        try {
            new DaoGenerator().generateAll(schema, "app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addCategory(Schema schema) {
        Entity note = schema.addEntity("Category");
        note.addIdProperty();
        note.addIntProperty("categoryId").notNull();
        note.addStringProperty("name").notNull();
        note.addIntProperty("parentId").notNull();
    }

    private static void addArea(Schema schema) {
        Entity note = schema.addEntity("Area");
        note.addIdProperty();
        note.addIntProperty("areaId").notNull();
        note.addStringProperty("name").notNull();
        note.addIntProperty("parentId").notNull();
        note.addIntProperty("cityId").notNull();
    }

    private static void addCity(Schema schema) {
        Entity note = schema.addEntity("City");
        note.addIdProperty();
        note.addIntProperty("cityId").notNull();
        note.addStringProperty("name").notNull();
        note.addStringProperty("pinyin").notNull();
        note.addLongProperty("lastUseTime");
    }
}
