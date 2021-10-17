package edu.cs.birzeit.restapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SecondActivity extends AppCompatActivity {
    private EditText edtBookTitle;
    private EditText edtBookCategory;
    private EditText edtBookPages;
    private TextView txtResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);
        setUpViews();

    }

    private void setUpViews() {

        edtBookTitle =  findViewById(R.id.edtBookTitle);
        edtBookCategory = findViewById(R.id.edtBookCategory);
        edtBookPages = findViewById(R.id.edtBookPages);
        txtResult = findViewById(R.id.txtResult);
    }

    private String processRequest(String restUrl) throws UnsupportedEncodingException {
        String title = edtBookTitle.getText().toString();
        String category = edtBookCategory.getText().toString();
        String pages = edtBookPages.getText().toString();

        String data = URLEncoder.encode("title", "UTF-8")
                + "=" + URLEncoder.encode(title, "UTF-8");

        data += "&" + URLEncoder.encode("cat", "UTF-8") + "="
                + URLEncoder.encode(category, "UTF-8");

        data += "&" + URLEncoder.encode("pages", "UTF-8")
                + "=" + URLEncoder.encode(pages, "UTF-8");

        String text = "";
        BufferedReader reader=null;

        // Send data
        try
        {

            // Defined URL  where to send data
            URL url = new URL(restUrl);

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = "";

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {

                reader.close();
            }

            catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        // Show response on activity
        return text;



    }

   class MyTask2 implements Runnable{
        private String url;
        public MyTask2(String url){
            this.url = url;
        }

        @Override
        public void run() {
             final String result;
            try {
                result = processRequest(url);
                txtResult.post(new Runnable() {
                    @Override
                    public void run() {
                        txtResult.setText(result);
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    public void btnAddOnClick(View view) {
        String restUrl = "http://10.0.2.2:84/rest/addbook.php";
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    123);

        } else{
            /*SendPostRequest runner = new SendPostRequest();
            runner.execute(restUrl);*/

            Thread thread = new Thread(new MyTask2(restUrl));
            thread.start();
        }
    }
}
