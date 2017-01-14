package todolist.cursoandroid.com.todolist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText textoTarefa;
    private Button botaoAdicionar;
    private ListView listaTarefas;
    private SQLiteDatabase bancoDados;

    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            //Recuperar componentes
            textoTarefa = (EditText) findViewById(R.id.textoID);
            botaoAdicionar = (Button) findViewById(R.id.botaoID);
            listaTarefas = (ListView) findViewById(R.id.listaTarefasID);

            //banco de dados
            bancoDados = openOrCreateDatabase("appTarefas", MODE_PRIVATE, null);

            //criar tabelas
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas (id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR )");

            botaoAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String textoDigitado = textoTarefa.getText().toString();
                    salvarTarefa( textoDigitado );
                    recuperarTarefas();

                }
            });

            listaTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Log.i("ITEM: ", + position + " / " + ids.get(position));
                    removarTarefa(ids.get(position));
                }
            });

            //recuperar tarefas
            recuperarTarefas();

        }catch (Exception e){

            e.printStackTrace();

        }
    }

    private void salvarTarefa(String texto){

        try {

            if (texto.equals("") ){

                Toast.makeText(MainActivity.this, "Preencha uma tarefa", Toast.LENGTH_SHORT).show();

            }else{

                bancoDados.execSQL("INSERT INTO tarefas (tarefa) VALUES('" + texto + "') ");
                Toast.makeText(MainActivity.this, "Tarefa Salva com Sucesso", Toast.LENGTH_SHORT).show();
                recuperarTarefas();
                textoTarefa.setText("");

            }

        }catch (Exception e){

            e.printStackTrace();

        }
    }

    private void recuperarTarefas(){

        try{

            //cursor recupera as tarefas
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC",null);

            //inteiro para recuperar o indice da coluna com o método getColumnUndex, passando apenas o nome da coluna ele recupera o +indice
            int indiceColunaID = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            //lista
            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            //criar adaptador
            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text2,
                    itens);

            //atribuir os itens para a lista de tarefas, setamos o Adapter ao listaTarefa
            listaTarefas.setAdapter(itensAdaptador);

            //listar as tarefas
            cursor.moveToFirst();

            while (cursor != null) {

                //o cursor.getString(indiceColunaTarefa) recupera a tarefa
                Log.i("Resultado - ","ID Tarefa: "+ cursor.getString(indiceColunaID)+ " Tarefa: "+ cursor.getString(indiceColunaTarefa));

                //adicionar as tarefas ao itens e ao ids para facilitar a remoção/marcação
                itens.add( cursor.getString(indiceColunaTarefa) );
                ids.add( Integer.parseInt(cursor.getString(indiceColunaID) ) );

                cursor.moveToNext();

            }

        }catch (Exception e){

            e.printStackTrace();

        }
    }

    private void removarTarefa(Integer id){
        try {

                bancoDados.execSQL("DELETE FROM tarefas WHERE id ="+ id);
                Toast.makeText(MainActivity.this, "Tarefa Removida com Sucesso", Toast.LENGTH_SHORT).show();
                recuperarTarefas();

        }catch (Exception e){
            e.printStackTrace();

        }
    }
}

