package updater;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import okhttp3.*;
import org.apache.log4j.Logger;
import sample.Controller;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Класс с общими методами
 */
public class Base {
    private static final Logger log = Logger.getLogger(Controller.class);

    protected String username;
    protected String password;
    private static int responseCode;

    public Base(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Запрос с авторизацией
     * @param setRequest запрос
     * @return Возвращает ответ
     */
    protected String getResponse(final String setRequest) {
        final OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .callTimeout(20,TimeUnit.SECONDS)
                .build();
        String response1 = "";
        String credential = Credentials.basic(username, password);
        Request request = new Request.Builder()
                .url(setRequest)
                .addHeader("Authorization", credential)
                .addHeader("Accept", "application/json")
                .addHeader("Coockie",
                        "JSESSIONID=D70C04C50FE50EA25CF5504B571C73FD")
                .addHeader("User-Agent", "OkHttp Bot")
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            responseCode = response.code();
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            response1 = response.body().string();
            httpClient.connectionPool().evictAll();
        } catch (IOException e) {
            log.info("" + e);
            httpClient.connectionPool().evictAll();
        }
        return response1;
    }

    protected String getResponse2(final String url) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(url)
                    .basicAuth(username, password)
                    .header("Accept", "application/json")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
            getResponse(url);
        }

        assert response != null;
        return response.getBody().toString();
    }

    public String getResponsePost(final String setRequest, final String bodyRequest) {
        final OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .callTimeout(20,TimeUnit.SECONDS)
                .build();
        String response1 = "";
        String credential = Credentials.basic(username, password);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), bodyRequest);
        Request request = new Request.Builder()
                .addHeader("Authorization", credential)
                .addHeader("Accept", "application/json")
                .addHeader("Coockie",
                        "JSESSIONID=D70C04C50FE50EA25CF5504B571C73FD")
                .addHeader("User-Agent", "OkHttp Bot")
                .url(setRequest)
                .post(body)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            responseCode = response.code();
            response1 = response.body().string();
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            httpClient.connectionPool().evictAll();
        } catch (IOException e) {
            System.out.println(e);
            httpClient.connectionPool().evictAll();
        }
        return response1;
    }

    public static int getResponseCode() {
        return responseCode;
    }
}
