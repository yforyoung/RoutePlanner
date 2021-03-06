package com.example.y.routeplanner.util;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import com.example.y.routeplanner.StartActivity;
import com.example.y.routeplanner.gson.User;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Util {
    private handleResponse handleResponse;
    public Util() {

    }

    public void setHandleResponse(Util.handleResponse handleResponse) {
        this.handleResponse = handleResponse;
    }

    public void save(String data, Context context) {
        FileOutputStream out;
        BufferedWriter writer = null;
        try {
            out = context.openFileOutput("userFile", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String read(Context context) {
        FileInputStream inputStream;
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStream = context.openFileInput("userFile");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }


    public void login(AppCompatActivity context, User user) {
        SharedPreferences.Editor editor = context.getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        editor.putInt("login", 1); //标志已登陆
        editor.apply();
        String gen=user.getGender();
        if (gen.equals("1"))
            user.setGender("男");
        else if (gen.equals("0"))
            user.setGender("女");
        save(new Gson().toJson(user), context);//存用户信息到本地
        Intent intent = new Intent(context, StartActivity.class);
        context.startActivity(intent);
        context.finish();
    }


    public void doPost(AppCompatActivity context,final Request request){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String s=response.body().string();
                        handleResponse(s);
                    }
                });
            }
        });
    }

    private void handleResponse(String s){
        if (handleResponse!=null){
            handleResponse.handleResponses(s);
        }
    }

    public interface handleResponse{
        void handleResponses(String response);
    }
}
