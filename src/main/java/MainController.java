import com.google.gson.Gson;
import java.io.IOException;
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
    String URL_audio = "https://bit.ly/3yxKEIY";
    Gson gson;
    MainController() throws URISyntaxException, IOException, InterruptedException {
        postRequest(URL_transcript, Api_key, URL_audio);
        getResponse();
        getRequest(URL_transcript, Api_key);
    }

   private void convertToJson( HttpResponse response) {
        String json = response.body().toString();
        gson = new Gson();
        transcript = gson.fromJson(json, Transcript.class);
    }
    public void setPostRequest(String URL_transcript,String api_key, String jsonRequest ) throws URISyntaxException {
        postRequest = HttpRequest.newBuilder()
                .uri(new URI(URL_transcript))
                .header("Authorization", Api_key)
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
    }
    public void postRequest(String URL_transcript, String Api_key,String URL_audio) throws URISyntaxException {
        gson = new Gson();
        transcript = new Transcript();
        transcript.setAudio_url(URL_audio);
        String jsonRequest = gson.toJson(transcript);
        //System.out.println(jsonRequest);
        setPostRequest(URL_transcript, Api_key, jsonRequest);
    }

    // Send the request and get the response
    public void getResponse() throws IOException, InterruptedException {
        httpClient = HttpClient.newHttpClient();
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        //System.out.println(postResponse.body());
        convertToJson(postResponse);
        //System.out.println(transcript.getId());
    }
  public void setGetRequest(String URL_transcript, String Api_key) throws URISyntaxException, IOException, InterruptedException {
      HttpRequest gutRequest = HttpRequest.newBuilder()
              .uri(new URI(URL_transcript + "/" + transcript.getId()))
              .header("Authorization", Api_key)
              .GET()
              .build();
        httpClient = HttpClient.newHttpClient();
        HttpResponse<String> getResponse = httpClient.send(gutRequest, HttpResponse.BodyHandlers.ofString());
         convertToJson(getResponse);
    }
    public void getRequest(String URL_transcript ,String Api_key) throws URISyntaxException, IOException, InterruptedException {

        while (true) {
            setGetRequest(URL_transcript, Api_key);
            System.out.println(transcript.getStatus());
            if("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())) {
                break;
            }
            Thread.sleep(1000);
        }
        System.out.println("Transcript is complete");
        System.out.println(transcript.getText());
    }
}

