package com.fdeight.miscellaneous.simple.network;

public interface SettingsAccess {

    /**
     * Возвращает значение пункта настроек в виде целого числа (или значение по умолчанию, если такого пункта нет)
     * @param name          название пункта настроек
     * @param defaultValue  значение по умолчанию
     * @return {@code int}-значение настроек
     */
    int getIntSetting(String name, int defaultValue);


    /**
     * Возвращает значение пункта настроек в виде строки (или значение по умолчанию, если такого пункта нет)
     * @param name          название пункта настроек
     * @param defaultValue  значение по умолчанию
     * @return {@code String}-значение настроек
     */
    String getStringSetting(String name, String defaultValue);

}
