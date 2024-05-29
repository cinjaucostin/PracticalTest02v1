package ro.pub.cs.systems.eim.practicaltest02v10.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import ro.pub.cs.systems.eim.practicaltest02v10.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v10.general.Utilities;

public class CommunicationThread extends Thread {
    private Socket socket;

    private ServerThread serverThread;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    public String getAutocompleteWords(String keyword) throws IOException, JSONException {
        HttpClient httpClient = new DefaultHttpClient();
        String requestURL = Constants.GOOGLE_AUTOCOMPLETE_API_URL + "&q=" + keyword;
        HttpGet httpGet = new HttpGet(requestURL);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String response = httpClient.execute(httpGet, responseHandler);
        if(response == null || response.isEmpty()) {
            Log.e(Constants.COMMUNICATION_THREAD_TAG, "Empty response received for url - " + requestURL);
            return "";
        }
        Log.w(Constants.COMMUNICATION_THREAD_TAG, "Response received from API - " + response);
        try {
            int firstBracketIndex = response.indexOf('[');
            int secondBracketIndex = response.indexOf('[', firstBracketIndex + 1);
            if (secondBracketIndex == -1) {
                System.out.println("Invalid input format");
                return "";
            }
            String substringFromSecondBracket = response.substring(secondBracketIndex);
            int endOfArrayIndex = substringFromSecondBracket.indexOf(']') + 1;
            String arrayString = substringFromSecondBracket.substring(0, endOfArrayIndex);
            JSONArray keywordsArray = new JSONArray(arrayString);
            List<String> keywordsList = new ArrayList<>();
            for (int i = 0; i < keywordsArray.length(); i++) {
                Log.w(Constants.COMMUNICATION_THREAD_TAG, keywordsArray.getString(i));
                keywordsList.add(keywordsArray.getString(i));
            }
            List<String> onlyNeededKeywords = keywordsList.subList(0, Math.min(5, keywordsList.size()));
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < onlyNeededKeywords.size(); i++) {
                stringBuilder.append(onlyNeededKeywords.get(i));
                if(i != onlyNeededKeywords.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append("\n");
            return stringBuilder.toString();
        } catch (JSONException e) {
            Log.e(Constants.COMMUNICATION_THREAD_TAG, "Unable to parse GOOGLE AUTOCOMPLETE API response");
        }
        return response;
    }

    @Override
    public void run() {
        try {
            String result = "";
            Log.w(Constants.COMMUNICATION_THREAD_TAG, "Connection opened with - " + socket.getInetAddress() + ":" + socket.getLocalPort());

            BufferedReader bufferedReader = Utilities.getReader(socket);
            String lineFromClient = bufferedReader.readLine();
            Log.w(Constants.COMMUNICATION_THREAD_TAG, "Message received from Client - " + lineFromClient);

            if(lineFromClient == null || lineFromClient.isEmpty()) {
                Log.e(Constants.COMMUNICATION_THREAD_TAG, "Line from client is required");
                return;
            }

            PrintWriter printWriter = Utilities.getWriter(socket);
            result += getAutocompleteWords(lineFromClient);
            printWriter.println(result);
            socket.close();
            Log.w(Constants.COMMUNICATION_THREAD_TAG, "Connection closed.");
        } catch (IOException e) {
            Log.e(Constants.COMMUNICATION_THREAD_TAG, "There was an issue - " + e.getMessage());
        } catch (JSONException e) {
            Log.e(Constants.COMMUNICATION_THREAD_TAG, "There was an issue when trying to parse response JSON - " + e.getMessage());
        }
    }
}
