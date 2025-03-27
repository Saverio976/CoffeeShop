package com.hwjustjava.app;

import java.io.IOException;

class Log
{
    private static Log SingleInstance = null;

    private java.util.List<String> logs;

    private Log()
    {
        this.logs = new java.util.LinkedList<String>();
    }

    public static synchronized Log GetInstance()
    {
        if (Log.SingleInstance == null) {
            Log.SingleInstance = new Log();
        }
        return Log.SingleInstance;
    }

    public void Print(String Message)
    {
        System.out.println(Message);
        synchronized (this.logs) {
            this.logs.add(Message);
        }
    }

    public void WriteLog(String LogFilePath)
    {
        synchronized (this.logs) {
            try {
                java.io.File fileCreate = new java.io.File(LogFilePath);
                fileCreate.createNewFile();
                java.io.FileWriter file = new java.io.FileWriter(LogFilePath);
                for (String log : this.logs) {
                    file.write(log + "\n");
                }
                file.close();
            } catch (IOException e) {
                System.out.println("Error when writing logs file: " + e.getMessage());
            }
        }
    }
}
