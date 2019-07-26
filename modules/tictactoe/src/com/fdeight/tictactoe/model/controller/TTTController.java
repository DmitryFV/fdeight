package com.fdeight.tictactoe.model.controller;

/**
 * Согласно требованиям к "чистой архитектуре" модуль (package) model не должен зависеть от других модулей
 * (view и controller), поэтому все интерфейсы, от которых зависит модель, вынесены в модуль (package) model,
 * используя инверсию зависимостей.
 */
public interface TTTController {

    String getVersion();

    void run();
}
