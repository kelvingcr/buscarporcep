package br.com.kelvingcr.consumindoapiviacep;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.kelvingcr.consumindoapiviacep.util.CEP;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {


    private EditText edt_cep, edt_logradouro, edt_bairro, edt_localidade, edt_uf;
    private Button btn_enviar;
    private ProgressBar progress_circular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt_cep = findViewById(R.id.edt_cep);
        edt_logradouro = findViewById(R.id.edt_logradouro);
        edt_bairro = findViewById(R.id.edt_bairro);
        edt_localidade = findViewById(R.id.edt_localidade);
        edt_uf = findViewById(R.id.edt_uf);
        btn_enviar = findViewById(R.id.btn_enviar);
        progress_circular = findViewById(R.id.progress_circular);
        configCliques();

    }

    private void configCliques(){
        btn_enviar.setOnClickListener(view -> {
            if(!edt_cep.getText().toString().isEmpty()){
                getCEP(edt_cep.getText().toString());
            }else{
                edt_cep.requestFocus();
                edt_cep.setError("Campo obrigatório.");
            }
        });
    }

    public void getCEP(String cep) {
        RequestParams params = new RequestParams();
        progress_circular.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://viacep.com.br/ws/" + cep + "/json/", params, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getBaseContext(), "Problema na conexao!"+statusCode, Toast.LENGTH_LONG).show();
                progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    JSONObject obj = new JSONObject(responseString);

                    CEP cepDados = new CEP();

                    if (!obj.has("erro")) {

                        cepDados.setBairro(obj.getString("bairro"));
                        cepDados.setLocalidade(obj.getString("localidade"));
                        cepDados.setLogradouro(obj.getString("logradouro"));
                        cepDados.setUf(obj.getString("uf"));

                        edt_bairro.setText(cepDados.getBairro());
                        edt_localidade.setText(cepDados.getLocalidade());
                        edt_logradouro.setText(cepDados.getLogradouro());
                        edt_uf.setText(cepDados.getUf());
                        progress_circular.setVisibility(View.GONE);
                    }

                }catch(JSONException e){

                }
            }
        });
    }
}