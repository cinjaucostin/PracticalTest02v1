package ro.pub.cs.systems.eim.practicaltest02v10.network;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02v10.general.Constants;

public class ServerThread extends Thread {
    private boolean isRunning;
    private ServerSocket serverSocket = null;

    public ServerThread(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Log.e(Constants.SERVER_TAG, "Error when trying to initialize server socket: " + e.getMessage());
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void startServer() {
        isRunning = true;
        start();
        Log.w(Constants.SERVER_TAG, "Server Thread was started.");
    }

    public void stopServer() {
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.e(Constants.SERVER_TAG, "Error when trying to close Server Socket - " + e.getMessage());
        }
        Log.e(Constants.SERVER_TAG, "Server Thread was stopped");
    }

    @Override
    public void run() {
        try {
            while(isRunning) {
                Socket socket = serverSocket.accept();
                if(socket != null) {
                    CommunicationThread communicationThread = new CommunicationThread(this, socket);
                    communicationThread.start();
                }
            }
        } catch (IOException e) {
            Log.e(Constants.SERVER_TAG, "Problem when trying to accept client - " + e.getMessage());
        }
    }
}
