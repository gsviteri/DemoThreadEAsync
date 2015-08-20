package br.com.fiap.demothreadeasync;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JsonActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private final String URL_SERVICE = "https://times-futebol-api.herokuapp.com/api/time";
    private ServiceTask serviceTask;
    private Button btTimes;
    private ListView lvLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);

        btTimes = (Button)findViewById(R.id.btnTime);
        lvLista = (ListView)findViewById(R.id.lvLista);

        btTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(JsonActivity.this, "Download", "Baixando a JSON");

                ServiceTask task = new ServiceTask();
                task.execute(URL_SERVICE);
            }
        });

        lvLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(JsonActivity.this, "Corinthians", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_json, menu);
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

    private class ServiceTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return listaTimes(params[0]);

            }catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            dialog.dismiss();
            if(json != null){
                try {
                    JSONArray array = new JSONArray(json);
                    String[] lista = new String[array.length()];

                    for (int i = 0; i < array.length(); i++){
                        lista[i] = array.getJSONObject(i).getString("nome");
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getApplicationContext(),
                                                     R.layout.row_item,
                                                     android.R.id.text1,
                                                     lista);

                    lvLista.setAdapter(adapter);

                }catch(JSONException e){
                    e.printStackTrace();
                }


            }

        }

        private String listaTimes(String url) throws IOException {
            String content = "";
            URL urlService = null;

            try {
                urlService = new URL(url);


                HttpURLConnection conn = (HttpURLConnection)urlService.openConnection();
                conn.setDoInput(true);
                conn.connect();

                // buscando inform de texto
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String linha = null;
                while((linha = reader.readLine()) != null){
                    sb.append(linha + "\n");
                }

                content = sb.toString();

            }catch (MalformedURLException e){
                e.printStackTrace();
                return null;
            }

            return content;
        }
    }
}
