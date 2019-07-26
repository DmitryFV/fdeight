package com.fdeight.miscellaneous.simple.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {

    static final int PORT = 4004;

    private Socket clientSocket; //сокет для общения
    private ServerSocket serverSocket; // серверсокет
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет

    private void start() {
        try {
            try  {
                serverSocket = new ServerSocket(PORT); // серверсокет прослушивает порт
                System.out.println("Сервер запущен!"); // хорошо бы серверу объявить о своем запуске
                clientSocket = serverSocket.accept(); // accept() будет ждать пока кто-нибудь не захочет подключиться
                try { // установив связь и воссоздав сокет для общения с клиентом можно перейти
                    // к созданию потоков ввода/вывода.
                    // теперь мы можем принимать сообщения
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // и отправлять
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    final String message = in.readLine(); // ждём пока клиент что-нибудь нам напишет
                    System.out.println(message);
                    // не долго думая отвечает клиенту
                    out.write("Привет, это Сервер! Подтверждаю, вы написали : " + message + "\n");
                    out.flush(); // выталкиваем все из буфера
                } finally { // в любом случае сокет будет закрыт
                    System.out.println("finally");
                    clientSocket.close();
                    // потоки тоже хорошо бы закрыть
                    in.close();
                    out.close();
                }
            } finally {
                System.out.println("Сервер закрыт!");
                serverSocket.close();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {
        final SimpleServer simpleServer = new SimpleServer();
        simpleServer.start();
    }
}
