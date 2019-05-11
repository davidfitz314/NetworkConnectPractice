package com.example.networkconnectpractice;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    private TextView headerView;
    private TextView descriptionView;
    private ImageView downloadedImageView;
    private Button connectNetworkButton;
    private ProgressDialog progressDialog;
    private Bitmap bitmap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headerView = findViewById(R.id.headerView);
        headerView.setText("Tutorials Point Networking");

        descriptionView = findViewById(R.id.textView);
        descriptionView.setText("Click to download the the logo from Tutorials Point!!!");

        downloadedImageView = findViewById(R.id.imageView);

        connectNetworkButton = findViewById(R.id.networkButton);

        connectNetworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //push to do the network connect and download the button
                //check internet connection
                Boolean isConnected = checkInternetConnection();
                if (isConnected){
                    downLoadImage("http://www.tutorialspoint.com/green/images/logo.png");
                }
            }
        });
    }

    private Boolean checkInternetConnection(){
        //init connecctivitymanager to make the connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        //check for a connection
        if (connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING
                || connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING
                || connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED){
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            return true;
        } else if (connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED
                || connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    private void downLoadImage(String urlString){
        progressDialog = ProgressDialog.show(this, "", "Downloading image from "+urlString);
        final String url = urlString;

        new Thread(){
            public void run(){
                InputStream in = null;

                Message msg = Message.obtain();
                msg.what = 1;

                try {
                    in = openHTTPConnection(url);
                    bitmap = BitmapFactory.decodeStream(in);
                    Bundle b = new Bundle();
                    b.putParcelable("bitmap", bitmap);
                    msg.setData(b);
                    in.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
                //message handler
                messageHandler.sendMessage(msg);
            }
        }.start();
    }

    private Handler messageHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            downloadedImageView.setImageBitmap((Bitmap) (msg.getData().getParcelable("bitmap")));
            progressDialog.dismiss();
        }
    };

    private InputStream openHTTPConnection(String urlString){
        InputStream inputStream = null;
        int resCode = -1;
        try {
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)){
                throw new IOException("Invalid URL type!");
            }

            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            resCode = httpURLConnection.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK){
                inputStream = httpURLConnection.getInputStream();
            }
        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return inputStream;
    }
}
