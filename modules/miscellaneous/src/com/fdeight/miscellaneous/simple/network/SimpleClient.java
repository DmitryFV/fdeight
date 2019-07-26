package com.fdeight.miscellaneous.simple.network;

import java.io.*;
import java.net.Socket;

public class SimpleClient {

    private static final int PORT = SimpleServer.PORT;

    private Socket clientSocket; //сокет для общения
    private BufferedReader reader; // нам нужен ридер читающий с консоли, иначе как
    // мы узнаем что хочет сказать клиент?
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет

    private void start() {
        try {
            try {
                // адрес - локальный хост, порт - такой же как у сервера
                clientSocket = new Socket("localhost", PORT); // этой строкой мы запрашиваем
                //  у сервера доступ на соединение
                reader = new BufferedReader(new InputStreamReader(System.in));
                // читать соообщения с сервера
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // писать туда же
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                System.out.println("Вы что-то хотели сказать? Введите это здесь:");
                // если соединение произошло и потоки успешно созданы - мы можем
                //  работать дальше и предложить клиенту что то ввести
                // если нет - вылетит исключение
                final String message = reader.readLine(); // ждём пока клиент что-нибудь
                // не напишет в консоль
                out.write(message + "\n"); // отправляем сообщение на сервер
                out.flush();
                final String serverWord = in.readLine(); // ждём, что скажет сервер
                System.out.println(serverWord); // получив - выводим на экран
            } finally { // в любом случае необходимо закрыть сокет и потоки
                System.out.println("Клиент был закрыт...");
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {
        final SimpleClient simpleClient = new SimpleClient();
        simpleClient.start();
    }
}
