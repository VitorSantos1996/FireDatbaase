package com.example.vitorpereira.firedatbaase;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText nomeP, apelidoP, emailP, passwdP;
    ListView listPessoas;

    Pessoa pessoaSelecionada;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dataBaseReference;

    private List<Pessoa> listaPessoa = new ArrayList<Pessoa>();
    ArrayAdapter<Pessoa> arrayAdapterPessoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nomeP = findViewById(R.id.nome);
        apelidoP = findViewById(R.id.apelido);
        emailP = findViewById(R.id.email);
        passwdP = findViewById(R.id.senha);

        listPessoas = findViewById(R.id.id_listview);

        listPessoas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelecionada = (Pessoa) parent.getItemAtPosition(position);

                nomeP.setText(pessoaSelecionada.getNome());
                apelidoP.setText(pessoaSelecionada.getApelido());
                emailP.setText(pessoaSelecionada.getEmail());
                passwdP.setText(pessoaSelecionada.getPassword());
            }
        });

        inicializeFirebase();

        listaDados();
    }

    private void inicializeFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        dataBaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String nome = nomeP.getText().toString();
        String apelido = apelidoP.getText().toString();
        String email = emailP.getText().toString();
        String senha = passwdP.getText().toString();

        switch (item.getItemId()) {
            case R.id.icon_add: {
                if (nome.equals("") || email.equals("") || apelido.equals("")) {
                    valida();
                } else {
                    Pessoa p = new Pessoa();
                    p.setUid(UUID.randomUUID().toString());
                    p.setNome(nome);
                    p.setEmail(email);
                    p.setPassword(senha);

                    dataBaseReference.child("Pessoa").child(p.getUid()).setValue(p);
                    Toast.makeText(this, "Adicionado com sucesso", Toast.LENGTH_LONG).show();

                    limpaCampos();
                }
                break;
            }
            case R.id.icon_del:{
                Pessoa p = new Pessoa();
                p.setUid(pessoaSelecionada.getUid());
                dataBaseReference.child("Pessoa").child(p.getUid()).removeValue();
                Toast.makeText(this,"Registro Excluido",Toast.LENGTH_LONG).show();

                limpaCampos();
                break;
            }
            case R.id.icon_save:{

                Pessoa p = new Pessoa();
                p.setUid(pessoaSelecionada.getUid());
                p.setNome(nomeP.getText().toString());
                p.setEmail(emailP.getText().toString());
                p.setApelido(apelidoP.getText().toString());
                p.setPassword(passwdP.getText().toString());

                dataBaseReference.child("Pessoa").child(p.getUid()).setValue(p);
                Toast.makeText(this, "Alterado", Toast.LENGTH_LONG).show();

                limpaCampos();

                break;
            }
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void valida() {

        String nome = nomeP.getText().toString();
        String apelido = apelidoP.getText().toString();
        String email = emailP.getText().toString();
        String senha = passwdP.getText().toString();

        if (nome.equals("")) {
            nomeP.setError("Campo requerido");
        }
        if (email.equals("")) {
            emailP.setError("Campo requerido");
        }
        if (apelido.equals("")) {
            apelidoP.setError("Campo requerido");
        }
        if (senha.equals("")) {
            passwdP.setError("Campo requerido");
        }
    }

    private void listaDados() {
        dataBaseReference.child("Pessoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPessoa.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Pessoa p = dataSnapshot1.getValue(Pessoa.class);
                    listaPessoa.add(p);

                }
                arrayAdapterPessoa = new ArrayAdapter<Pessoa>(MainActivity.this, android.R.layout.simple_list_item_1, listaPessoa);
                listPessoas.setAdapter(arrayAdapterPessoa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void limpaCampos() {
        nomeP.setText("");
        apelidoP.setText("");
        emailP.setText("");
        passwdP.setText("");
    }
}