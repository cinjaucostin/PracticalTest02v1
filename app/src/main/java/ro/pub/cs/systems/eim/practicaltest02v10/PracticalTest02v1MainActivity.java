package ro.pub.cs.systems.eim.practicaltest02v10;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ro.pub.cs.systems.eim.practicaltest02v10.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v10.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02v10.network.ServerThread;

public class PracticalTest02v1MainActivity extends AppCompatActivity {
    public static Handler mainHandler;
    private ServerThread serverThread;
    private EditText serverPortEditText;
    private EditText clientAddressEditText;
    private EditText clientPortEditText;
    private Button serverConnectButton;

    private EditText userInputEditText;
    private TextView serverResponseTextView;
    private Button getServerResponseButton;

    private ServerConnectButtonListener serverConnectButtonListener = new ServerConnectButtonListener();
    private class ServerConnectButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.w(Constants.MAIN_ACTIVITY, "Connect server button was clicked");
            String serverPortStr = serverPortEditText.getText().toString();
            if(serverPortStr == null || serverPortStr.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Server port is required", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPortStr));
            if(serverThread.getServerSocket() == null) {
                Log.e(Constants.MAIN_ACTIVITY, "Couldn't create server thread");
                return;
            }
            serverThread.startServer();
        }
    }

    private GetServerResponseButtonListener getServerResponseButtonListener = new GetServerResponseButtonListener();
    private class GetServerResponseButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.w(Constants.MAIN_ACTIVITY, "Get Server Response button was clicked");

            String serverAddress = clientAddressEditText.getText().toString();
            String serverPort = clientPortEditText.getText().toString();

            if(serverAddress == null || serverAddress.isEmpty() || serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Server address and port is required", Toast.LENGTH_SHORT).show();
                return;
            }

            String userInput = userInputEditText.getText().toString();
            if(userInput == null || userInput.isEmpty()) {
                Toast.makeText(getApplicationContext(), "User input is required", Toast.LENGTH_SHORT).show();
                return;
            }

            ClientThread clientThread = new ClientThread(serverAddress, Integer.parseInt(serverPort), userInput,
                    getApplicationContext());
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02v1_main);

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        serverConnectButton = findViewById(R.id.connect_button);
        serverConnectButton.setOnClickListener(serverConnectButtonListener);

        userInputEditText = findViewById(R.id.user_input_edit_text);
        serverResponseTextView = findViewById(R.id.server_response);
        getServerResponseButton = findViewById(R.id.get_response_from_server);
        getServerResponseButton.setOnClickListener(getServerResponseButtonListener);

        mainHandler = new Handler(Looper.getMainLooper());
    }
}