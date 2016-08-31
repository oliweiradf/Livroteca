package br.com.oliweira.livroteca;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Rafael on 29/01/2016.
 */
public class CadastrarLivroActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_livro);

        //estancia db_repertorio
        SQLiteDatabase db = openOrCreateDatabase("db_livroteca", MODE_PRIVATE, null);

        //== Inicio Spinner spnEstado
        //estancia spinner spnEstado
        Spinner spnEstado = (Spinner)findViewById(R.id.spnEstado);

        //consulta tba_estado
        SQLiteCursor csrEstado = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_estado;", null);

        //adaptador para spnEstado
        String[] fromEstado = {"_id", "no_estado"};
        int[] toEstado = {R.id.lblIdEstado, R.id.lblEstado};
        SimpleCursorAdapter adptEstado = new SimpleCursorAdapter(
                getBaseContext(), R.layout.model_spn_estado, csrEstado, fromEstado, toEstado);

        spnEstado.setAdapter(adptEstado);
        //== Fim Spinner spnEstado

        //cria tabela tb_livros
        StringBuilder sqlLivros = new StringBuilder();
        sqlLivros.append("CREATE TABLE IF NOT EXISTS tb_livros(");
        sqlLivros.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlLivros.append("no_autor VARCHAR(30), ");
        sqlLivros.append("no_titulo VARCHAR(50), ");
        sqlLivros.append("no_edicao VARCHAR(30), ");
        sqlLivros.append("no_editora VARCHAR(50), ");
        sqlLivros.append("id_estado INTEGER(2), ");
        sqlLivros.append("nu_ano_publicacao INTEGER(4), ");
        sqlLivros.append("nu_isbn VARCHAR(20));");
        db.execSQL(sqlLivros.toString());

        db.close();
    }

    public void salvarLivro(View view){

        //estancia db_repertorio
        SQLiteDatabase db = openOrCreateDatabase("db_livroteca", MODE_PRIVATE, null);

        //recupera os valores
        final TextView etAutor = (TextView) findViewById(R.id.etAutor);
        final TextView etTitulo = (TextView) findViewById(R.id.etTitulo);
        final TextView etEdicao = (TextView) findViewById(R.id.etEdicao);
        final TextView etEditora = (TextView) findViewById(R.id.etEditora);
        final Spinner spnEstado = (Spinner) findViewById(R.id.spnEstado);
        int spnIdEstado = (int) spnEstado.getSelectedItemId();
        final TextView etAnoPublicacao = (TextView) findViewById(R.id.etAnoPublicacao);
        final TextView etIsbn = (TextView) findViewById(R.id.etISBN);

        //coloca os valores no container ctvLivros
        ContentValues ctvLivros = new ContentValues();
        ctvLivros.put("no_autor", etAutor.getText().toString());
        ctvLivros.put("no_titulo", etTitulo.getText().toString());
        ctvLivros.put("no_edicao", etEdicao.getText().toString());
        ctvLivros.put("no_editora", etEditora.getText().toString());
        ctvLivros.put("id_estado", spnIdEstado);
        ctvLivros.put("nu_ano_publicacao", etAnoPublicacao.getText().toString());
        ctvLivros.put("nu_isbn", etIsbn.getText().toString());

        //insere valores na tb_livros
        try{
            if(db.insert("tb_livros","_id",ctvLivros) > 0){
                Toast.makeText(getBaseContext(),"Livro cadastrado com sucesso!",Toast.LENGTH_SHORT).show();
//                finish();
                AlertDialog.Builder msg = new AlertDialog.Builder(CadastrarLivroActivity.this);

                msg.setMessage("Deseja cadastrar outro livro?");
                msg.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etAutor.setText("");
                        etTitulo.setText("");
                        etEdicao.setText("");
                        etEditora.setText("");
                        spnEstado.setSelection(0);
                        etAnoPublicacao.setText("");
                        etIsbn.setText("");
                        etAutor.requestFocus();
                    }
                });
                msg.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                msg.show();

            }else{
                Toast.makeText(getBaseContext(),"Não foi possivel salvar o Livro!",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}
