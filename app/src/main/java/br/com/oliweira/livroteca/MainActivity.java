package br.com.oliweira.livroteca;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("84235F3CDE76BC8E85C7B17292CB406F").build() ;

        mAdView.loadAd(adRequest);


        //estacia banco de dados
        SQLiteDatabase db = openOrCreateDatabase("db_livroteca", MODE_PRIVATE, null);

        //cria tabela tb_tom_musica
        StringBuilder sqlEstado = new StringBuilder();
        sqlEstado.append("CREATE TABLE IF NOT EXISTS tba_estado(");
        sqlEstado.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlEstado.append("sg_estado VARCHAR(2), ");
        sqlEstado.append("no_estado VARCHAR(20));");
        db.execSQL(sqlEstado.toString());

        SQLiteCursor csrEstado = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_estado;", null);
        if (csrEstado.getCount() <= 0) {
            String[] sg_estado = {"AC","AL","AP","AM","BA","CE","DF","ES","GO","MA","MT","MS","MG",
                    "PA","PB","PR","PE","PI","RJ","RN","RS","RO","RR","SC","SP","SE","TO"};

            String[] no_estado = {"Acre","Alagoas","Amapá","Amazonas","Bahia","Ceará","Distrito Federal",
                    "Espirito Santo","Goias","Maranhão","Mato Grosso","Mato Grosso do Sul",
                    "Minas Gerais","Pará","Paraíba","Paraná","Pernambuco","Piauí","Rio de Janeiro",
                    "Rio Grande do Norte","Rio Grande do Sul","Rondônia","Roraima","Santa Catarina",
                    "São Paulo","Sergipe","Tocantins"};

            for (int i = 0; i < sg_estado.length; i++) {
                ContentValues ctvEstado = new ContentValues();
                ctvEstado.put("sg_estado", sg_estado[i]);
                ctvEstado.put("no_estado", no_estado[i]);
                db.insert("tba_estado", "_id", ctvEstado);
            }
        }

    }

    public void cadastrarLivro(View v){
        Intent it = new Intent(getApplicationContext(),CadastrarLivroActivity.class);
        startActivity(it);
    }

    public void listarLivros(View v){
        Intent it = new Intent(getApplication(),ListarLivroActivity.class);
        startActivity(it);
    }
}
