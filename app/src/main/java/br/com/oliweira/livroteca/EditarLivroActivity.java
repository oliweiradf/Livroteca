package br.com.oliweira.livroteca;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Rafael on 01/02/2016.
 */
public class EditarLivroActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_livro);

        //estancia db_livroteca
        SQLiteDatabase db = openOrCreateDatabase("db_livroteca",MODE_PRIVATE, null);

        //== Inicio Spinner spnEstado
        //estancia spinner spnEstado
        Spinner spnEstado = (Spinner)findViewById(R.id.spnEstadoE);

        //consulta tba_estado
        SQLiteCursor csrEstado = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_estado;", null);

        //adaptador para spnEstado
        String[] fromEstado = {"_id", "no_estado"};
        int[] toEstado = {R.id.lblIdEstado, R.id.lblEstado};
        SimpleCursorAdapter adptEstado = new SimpleCursorAdapter(
                getBaseContext(), R.layout.model_spn_estado, csrEstado, fromEstado, toEstado);

        spnEstado.setAdapter(adptEstado);
        //== Fim Spinner spnEstado

        //recupera o item clicado no lv_livro
        Intent itEditar = getIntent();
        int idLivroLV = itEditar.getIntExtra("id", 0);
//        Log.v("id:", "id: " + idLivroLV);

        //recupera o valor na tb_livros
        StringBuilder sqlLivros = new StringBuilder();
        sqlLivros.append("SELECT * FROM tb_livros WHERE _id = "+idLivroLV);
        SQLiteCursor crsLivros = (SQLiteCursor) db.rawQuery(sqlLivros.toString(), null);

        if(crsLivros.moveToFirst()){
            EditText etAutor = (EditText) findViewById(R.id.etAutorE);
            etAutor.setText(crsLivros.getString(1));
            EditText etTitulo = (EditText) findViewById(R.id.etTituloE);
            etTitulo.setText(crsLivros.getString(2));
            EditText etEdicao = (EditText) findViewById(R.id.etEdicaoE);
            etEdicao.setText(crsLivros.getString(3));
            EditText etEditora = (EditText) findViewById(R.id.etEditoraE);
            etEditora.setText(crsLivros.getString(4));
            spnEstado.setSelection(crsLivros.getInt(5)-1);
            EditText etAnoPublicacao = (EditText) findViewById(R.id.etAnoPublicacaoE);
            etAnoPublicacao.setText(crsLivros.getString(6));
            EditText etIsbn = (EditText) findViewById(R.id.etIsbnE);
            etIsbn.setText(crsLivros.getString(7));

        }else {
            Toast.makeText(getBaseContext(), "Não foi encontrado nenhum item!", Toast.LENGTH_LONG).show();
            finish();
        }

        db.close();
    }

    public void atualizarLivro(View view){
        try{
            //estancia db_livroteca
            SQLiteDatabase db = openOrCreateDatabase("db_livroteca", MODE_PRIVATE, null);

            //recupera id do livro clicado
            Intent itAtualizar = getIntent();
            int idLivroLV = itAtualizar.getIntExtra("id", 0);

            //recupera os valores
            EditText etAutor = (EditText) findViewById(R.id.etAutorE);
            EditText etTitulo = (EditText) findViewById(R.id.etTituloE);
            EditText etEdicao = (EditText) findViewById(R.id.etEdicaoE);
            EditText etEditora = (EditText) findViewById(R.id.etEditoraE);
            Spinner spnEstado = (Spinner) findViewById(R.id.spnEstadoE);
            int spnIdEstado = (int) spnEstado.getSelectedItemId();
            EditText etAnoPublicacao = (EditText) findViewById(R.id.etAnoPublicacaoE);
            EditText etIsbn = (EditText) findViewById(R.id.etIsbnE);

            //coloca os valores no container ctvLivros
            ContentValues ctvLivrosU = new ContentValues();
            ctvLivrosU.put("no_autor", etAutor.getText().toString());
            ctvLivrosU.put("no_titulo", etTitulo.getText().toString());
            ctvLivrosU.put("no_edicao", etEdicao.getText().toString());
            ctvLivrosU.put("no_editora", etEditora.getText().toString());
            ctvLivrosU.put("id_estado", spnIdEstado);
            ctvLivrosU.put("nu_ano_publicacao", etAnoPublicacao.getText().toString());
            ctvLivrosU.put("nu_isbn", etIsbn.getText().toString());

            if(db.update("tb_livros",ctvLivrosU,"_id = ?", new String[]{String.valueOf(idLivroLV)}) > 0){
                Toast.makeText(getBaseContext(),"Livro atualizado com sucesso!",Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getBaseContext(),"Não foi possível atualizar os dados do livro!",Toast.LENGTH_LONG).show();
                finish();
            }
        }catch (Exception e){
            Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void excluirLivro(View view) {

        try{
            //estancia db_livroteca
            final SQLiteDatabase db = openOrCreateDatabase("db_livroteca", MODE_PRIVATE, null);

            //recupera id do livro
            Intent itDelete = getIntent();
            final int idLivro = itDelete.getIntExtra("id", 0);

            AlertDialog.Builder msg = new AlertDialog.Builder(EditarLivroActivity.this);

            msg.setMessage("Deseja excluir esse livro?");
            msg.setNegativeButton("Não", null);
            msg.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (db.delete("tb_livros", "_id = ?", new String[]{String.valueOf(idLivro)}) > 0) {
                        Toast.makeText(getBaseContext(), "Livro excluido com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "Nao foi possivel excluir o item!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            msg.show();

        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
