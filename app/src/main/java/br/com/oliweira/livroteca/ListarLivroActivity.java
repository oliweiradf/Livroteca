package br.com.oliweira.livroteca;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.mediation.customevent.CustomEventAdapter;

import br.com.oliweira.swipemenulistview.BaseSwipListAdapter;
import br.com.oliweira.swipemenulistview.SwipeMenu;
import br.com.oliweira.swipemenulistview.SwipeMenuCreator;
import br.com.oliweira.swipemenulistview.SwipeMenuItem;
import br.com.oliweira.swipemenulistview.SwipeMenuListView;


import java.util.List;

/**
 * Created by Rafael on 29/01/2016.
 */
public class ListarLivroActivity extends AppCompatActivity{

    private List<ApplicationInfo> mAppList;
    private SimpleCursorAdapter adptLivros;
    private SwipeMenuListView lv_Livros;
    private RadioGroup rgOrdenar;
    private String orderBy;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_livros);
    }

    @Override
    protected void onResume() {
        super.onResume();

        abreConsulta("");

        rgOrdenar = (RadioGroup)findViewById(R.id.rgOrdenar);

        rgOrdenar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbTitulo:
                            // trata radioValor1
                        abreConsulta("no_titulo");
                        //Toast.makeText(getApplicationContext(), "orderBy: " + group, Toast.LENGTH_LONG).show();
                        break;
                    case R.id.rbAutor:
                        // trata radioValor2
                        abreConsulta("no_autor");
                        //Toast.makeText(getApplicationContext(), "orderBy: " + group, Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });
    }

    private void abreConsulta(String orderBy){
        //estacia db_livroteca
        db = openOrCreateDatabase("db_livroteca",MODE_PRIVATE,null);

        //cria uma string para montar o sql
        StringBuilder sqlLivros = new StringBuilder();
        sqlLivros.append("SELECT a._id, a.no_autor, a.no_titulo, a.no_edicao, a.no_editora, b.sg_estado, a.nu_ano_publicacao, a.nu_isbn FROM tb_livros a ");
        sqlLivros.append("INNER JOIN tba_estado b ON ");
        sqlLivros.append(" b._id = id_estado");

        if(!orderBy.equals("")){
            sqlLivros.append(" ORDER BY a."+orderBy+";");
        }else{
            sqlLivros.append(";");
        }

//        Log.d("sqlLivros: ",sqlLivros.toString());
//        Toast.makeText(getApplicationContext(), "sqlLivros: " + sqlLivros.toString(), Toast.LENGTH_LONG).show();

        //recupera os livros cadastrados
        Cursor csrLivros = db.rawQuery(sqlLivros.toString(),null);

        //estancia lvLivros
        lv_Livros = (SwipeMenuListView)findViewById(R.id.lvLivros);

        String[] fromLivros = {"_id","no_autor","no_titulo","no_edicao","no_editora","sg_estado","nu_ano_publicacao","nu_isbn"};
        int[] toLivros = {R.id.tvIdLivrosLV,R.id.tvAutorLV,R.id.tvTituloLV,R.id.tvEdicaoLV,R.id.tvEditoraLV,R.id.tvEstadoLV,R.id.tvAnoPublicacaoLV,R.id.tvISBN};
        adptLivros = new SimpleCursorAdapter(getBaseContext(),R.layout.model_lv_livros,csrLivros,fromLivros,toLivros);

        //seta os valores na lvLivros
        lv_Livros.setAdapter(adptLivros);

        db.close();

/*        lv_Livros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapter, View view, int position, long id) {
                SQLiteCursor csrItem = (SQLiteCursor) adapter.getAdapter().getItem(position);

                Intent inttEdit = new Intent(getBaseContext(), EditarLivroActivity.class);
                inttEdit.putExtra("id", csrItem.getInt(0));
                startActivity(inttEdit);
            }
        });
*/

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "update" item
                SwipeMenuItem updateItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                updateItem.setBackground(new ColorDrawable(Color.rgb(0, 0, 0)));
                // set item width
                updateItem.setWidth(dp2px(90));
                // set item title
//                updateItem.setTitle("Update");
                // set item title fontsize
//                updateItem.setTitleSize(18);
                // set item title font color
//                updateItem.setTitleColor(Color.WHITE);
                // set a icon
                updateItem.setIcon(R.drawable.icon_editar_azul_marinho_48x48);
                // add to menu
                menu.addMenuItem(updateItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0, 0, 0)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.icon_excluir_vermelho_48x48);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        lv_Livros.setMenuCreator(creator);

        // step 2. listener item click event
        lv_Livros.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                long item = adptLivros.getItemId(position);

                switch (index) {
                    case 0:
                        // update
                        update(item);
                        break;
                    case 1:
                        // delete
                        delete(item);
                        break;
                }
                return false;
            }
        });
        // set MenuStateChangeListener
        lv_Livros.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {
            }

            @Override
            public void onMenuClose(int position) {
            }
        });

        // other setting
//		listView.setCloseInterpolator(new BounceInterpolator());

        // test item long click
        lv_Livros.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Toast.makeText(getApplicationContext(), position + " long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void delete(long position) {
        // delete app
        try{
            //estancia db_livroteca
            final SQLiteDatabase db = openOrCreateDatabase("db_livroteca", MODE_PRIVATE, null);

            //recupera id do livro
            AlertDialog.Builder msg = new AlertDialog.Builder(ListarLivroActivity.this);

            final long idLivro = position;

            msg.setMessage("Deseja excluir esse livro?");
            msg.setNegativeButton("Não", null);
            msg.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (db.delete("tb_livros", "_id = ?", new String[]{String.valueOf(idLivro)}) > 0) {

                        RadioGroup rgOrdenar = (RadioGroup) findViewById(R.id.rgOrdenar);
                        switch (rgOrdenar.getCheckedRadioButtonId()) {
                            case R.id.rbTitulo:
                                orderBy = "no_titulo";
                                break;
                            case R.id.rbAutor:
                                orderBy = "no_autor";
                                break;
                            default:
                                orderBy = "";
                                break;
                        }

                        abreConsulta(orderBy);

                        Toast.makeText(getBaseContext(), "Livro excluido com sucesso!", Toast.LENGTH_SHORT).show();
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

    private void update(long position) {
        // update app
        int id = (int)position;

        Intent inttEdit = new Intent(getBaseContext(), EditarLivroActivity.class);
        inttEdit.putExtra("id", id);
        startActivity(inttEdit);
    }

    class AppAdapter extends BaseSwipListAdapter {

        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public ApplicationInfo getItem(int position) {
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.item_list_app, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            ApplicationInfo item = getItem(position);
            holder.iv_icon.setImageDrawable(item.loadIcon(getPackageManager()));
            holder.tv_name.setText(item.loadLabel(getPackageManager()));
            holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ListarLivroActivity.this, "iv_icon_click", Toast.LENGTH_SHORT).show();
                }
            });
            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ListarLivroActivity.this,"iv_icon_click",Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;

            public ViewHolder(View view) {
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
                view.setTag(this);
            }
        }

        @Override
        public boolean getSwipEnableByPosition(int position) {
            return position % 2 != 0;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_left) {
            lv_Livros.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
            return true;
        }
        if (id == R.id.action_right) {
            lv_Livros.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
