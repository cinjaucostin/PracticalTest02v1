package ro.pub.cs.systems.eim.practicaltest02v10.network;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02v10.PracticalTest02v1MainActivity;
import ro.pub.cs.systems.eim.practicaltest02v10.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v10.general.Utilities;

public class ClientThread extends Thread {
    /*
   Pentru conexiunea cu serverul.
   */
    private String serverAddress;
    private int serverPort;

    /*
        Pentru cererea de la server.
     */
    private String userInput;
    private TextView serverResponseTextView;
    private Context applicationContext;
    public ClientThread(String serverAddress, int serverPort, String userInput,
                        Context applicationContext) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.userInput = userInput;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(serverAddress, serverPort);
            Log.w(Constants.CLIENT_TAG, "Connection established with " + socket.getInetAddress() + ":" + socket.getLocalPort());
            BufferedReader reader = Utilities.getReader(socket);
            PrintWriter writer = Utilities.getWriter(socket);
            writer.println(userInput);
            writer.flush();
            String line = "";
            line = reader.readLine();
            Log.w(Constants.CLIENT_TAG, "Received from server - " + line);
            final String responseFromServerStr = line;
//            serverResponseTextView.post(() -> serverResponseTextView.setText(responseFromServerStr));
//            Toast.makeText(applicationContext, "Suggestions: " + responseFromServerStr, Toast.LENGTH_SHORT).show();
            PracticalTest02v1MainActivity.mainHandler.post(() -> Toast.makeText(applicationContext, "Suggestions: " + responseFromServerStr, Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            Log.e(Constants.CLIENT_TAG, "Error when trying to connect to server - "
                    + serverAddress + ":" + serverPort + " - " + e.getMessage());
        } finally {
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(Constants.CLIENT_TAG, "Error when trying to close socket - " + e.getMessage());
                }
            }
        }
    }
}
