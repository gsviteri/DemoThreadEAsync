package br.com.fiap.demothreadeasync;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private ImageView ivImagem;

    private DownloadImageTask downloadImageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        ivImagem = (ImageView)findViewById(R.id.ivImagem);

    }

    @Override
    protected void onDestroy() {
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
            dialog = null;
        }

        if(downloadImageTask != null){
            downloadImageTask.cancel(true);
        }

        super.onDestroy();
    }

    public void downloadImage(View v){
        dialog = ProgressDialog.show(DownloadActivity.this, "Download", "Baixando a Imagem");

        downloadImageTask = new DownloadImageTask();
        downloadImageTask.execute("http://s01.video.glbimg.com/x720/4366752.jpg");

    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // aqui é só processamento
            try {
                return downloadBitmap(params[0]);

            }catch (IOException io){
                io.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            // atualizar tela
            super.onPostExecute(image);
            dialog.dismiss();
            if(image != null){
                ivImagem.setImageBitmap(image);
            }
        }

        private Bitmap downloadBitmap(String url) throws IOException {
            URL imageUrl = null;
            Bitmap bitmapImage = null;
            try {
                imageUrl = new URL(url);
            }catch (MalformedURLException m){
                m.printStackTrace();
                return null;
            }

            try{
                HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                bitmapImage = BitmapFactory.decodeStream(is);
            }catch (IOException e) {
                e.printStackTrace();
            }

            return bitmapImage;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_download, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
