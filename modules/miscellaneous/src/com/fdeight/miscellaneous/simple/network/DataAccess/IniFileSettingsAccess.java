package com.fdeight.miscellaneous.simple.network.DataAccess;


import com.fdeight.miscellaneous.simple.network.SettingsAccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class IniFileSettingsAccess implements SettingsAccess {

    private final File file;
    private final Properties properties;

    public IniFileSettingsAccess(final String settingsNameFile) {
        properties = new Properties();
        file = getOrCreateFile(settingsNameFile);
        try {
            properties.load(new FileInputStream(file));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getIntSetting(final String name, final int defaultValue) {
        if(properties.containsKey(name)) {
            return Integer.parseInt(properties.getProperty(name));
        } else {
            return defaultValue;
        }
    }

    @Override
    public String getStringSetting(final String name, final String defaultValue) {
        if(properties.containsKey(name)) {
            return properties.getProperty(name);
        } else {
            return defaultValue;
        }
    }

    /**
     * Находит файл с заданным именем для использования при записи настроек (создает, если его нет)
     * @param settingsNameFile  путь к положению файла на диске
     * @return {@code File} - файл по заданному пути
     */
    private File getOrCreateFile(final String settingsNameFile) {
        final File file = new File(settingsNameFile);
        try {
            file.createNewFile();
        } catch (final IOException e) {
            System.out.println("Error while trying to create file: " + settingsNameFile);
            e.printStackTrace();
        }
        return file;
    }

}
