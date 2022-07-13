import com.google.gson.Gson;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MainController {
    HttpRequest postRequest;
    Transcript transcript;
    HttpClient httpClient;
    String Api_key = "4be5acd8e3d841a38e158f695a97cb78";
    String URL_transcript = "https://api.assemblyai.com/v2/transcript";
    Gson gson;
    MainController() throws URISyntaxException, IOException, InterruptedException {
        postRequest(URL_transcript, Api_key);
        getResponse();
        getRequest(URL_transcript);
    }



    public void postRequest(String URL_transcript, String Api_key) throws URISyntaxException {
        gson = new Gson();
        transcript = new Transcript();
        transcript.setAudio_url("https://bit.ly/3yxKEIY");
        String jsonRequest = gson.toJson(transcript);
        System.out.println(jsonRequest);
        postRequest = HttpRequest.newBuilder()
                .uri(new URI(URL_transcript))
                .header("Authorization", Api_key)
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
    }

    // Send the request and get the response
    public void getResponse() throws IOException, InterruptedException {
        httpClient = HttpClient.newHttpClient();
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(postResponse.body());
        // Convert the response to a Transcript object
        transcript = gson.fromJson(postResponse.body(), Transcript.class);
        System.out.println(transcript.getId());
    }

    public void getRequest(String URL_transcript) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest gutRequest = HttpRequest.newBuilder()
                .uri(new URI(URL_transcript + transcript.getId()))
                .GET()
                .build();
        httpClient = HttpClient.newHttpClient();
        while (true) {
            HttpResponse<String> getResponse = httpClient.send(gutRequest, HttpResponse.BodyHandlers.ofString());
            // Convert the response to a Transcript object and print it
            transcript = gson.fromJson(getResponse.body(), Transcript.class);
            System.out.println(transcript.getStatus());
            if ("completed".equals(transcript.getStatus()) || "failed".equals(transcript.getStatus())) {
                break;
            }
            Thread.sleep(1000);
        }
        System.out.println("Transcription completed");
        System.out.println(transcript.getText());
    }
}
