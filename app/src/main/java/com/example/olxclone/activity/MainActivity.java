package com.example.olxclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.olxclone.R;
import com.example.olxclone.adapter.AdapterAnuncios;
import com.example.olxclone.helper.ConfiguracaoFirebase;
import com.example.olxclone.helper.RecyclerItemClickListener;
import com.example.olxclone.model.Anuncio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Button botaoRegiao, botaoCategoria;
    private RecyclerView recyclerAnunciosPublicos;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference databaseReference;
    private DatabaseReference estadosReference, categoriasReference;
    private ValueEventListener valueEventListener;
    private AlertDialog alertDialog;
    private String filtroEstado = "";
    private String filtroCategoria = "";
    private boolean estadoFiltrado = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configuracoesIniciais();

        //configuracoes recycler view
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(getApplicationContext(), listaAnuncios);
        recyclerAnunciosPublicos.setAdapter(adapterAnuncios);

        //recuperar anuncios
        recuperarAnuncios();

        //click listener recycler view
        recyclerAnunciosPublicos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerAnunciosPublicos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Anuncio anuncioSelecionado = listaAnuncios.get(position);
                                Intent intent = new Intent(MainActivity.this, VisualisarAnuncioActivity.class);
                                intent.putExtra("anuncioSelecionado", anuncioSelecionado);
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        botaoRegiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrarPorEstado(v);
            }
        });
        botaoCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrarPorCategoria(v);
            }
        });
    }

    private void configuracoesIniciais(){
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();
        botaoCategoria = findViewById(R.id.buttonCategoria);
        botaoRegiao = findViewById(R.id.buttonRegiao);
        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnuncios);
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("todos_anuncios");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(firebaseAuth.getCurrentUser() == null){
            //caso usuario esteja deslogado
            menu.setGroupVisible(R.id.group_deslogado, true);
        }else{
            //caso usuario esteja logado
            menu.setGroupVisible(R.id.group_logado, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    public void filtrarPorCategoria(View view){
        if(!estadoFiltrado){
            Toast.makeText(this, "Selecione uma região primeiro", Toast.LENGTH_SHORT).show();
        }else {
            //Configurações do spinner
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null); //recupera o layout
            Spinner spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro); //recupera o id
            String[] categoria = getResources().getStringArray(R.array.categoria);
            ArrayAdapter<String> arrayAdapterCategoria = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoria);
            arrayAdapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerCategoria.setAdapter(arrayAdapterCategoria);

            //configuracoes do alertdialog
            AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(this);
            dialogCategoria.setTitle("Selecione a categoria");
            dialogCategoria.setView(viewSpinner);
            dialogCategoria.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                    recuperarAnunciosPorCategoria();
                }
            });
            dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = dialogCategoria.create();
            dialog.show();
        }
    }
    public void filtrarPorEstado(View view){

        //Configurações do spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null); //recupera o layout
        Spinner spinnerEstado = viewSpinner.findViewById(R.id.spinnerFiltro); //recupera o id
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> arrayAdapterEstados = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);
        arrayAdapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerEstado.setAdapter(arrayAdapterEstados);

        //configuracoes do alertdialog
        AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
        dialogEstado.setTitle("Selecione o estado");
        dialogEstado.setView(viewSpinner);
        dialogEstado.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filtroEstado = spinnerEstado.getSelectedItem().toString();
                recuperarAnunciosPorEstado();
                estadoFiltrado = true;
            }
        });
        dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = dialogEstado.create();
        dialog.show();
    }
    public void recuperarAnuncios(){
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando anuncios...")
                .setCancelable(false)
                .build();
        alertDialog.show();

        listaAnuncios.clear();
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot estados : snapshot.getChildren()){
                    for(DataSnapshot categorias : estados.getChildren()){
                        for(DataSnapshot anuncios : categorias.getChildren()){
                            listaAnuncios.add(anuncios.getValue(Anuncio.class));
                        }
                    }
                }
                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void recuperarAnunciosPorEstado(){
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando anuncios...")
                .setCancelable(false)
                .build();
        alertDialog.show();

        listaAnuncios.clear();
        estadosReference = databaseReference.child(filtroEstado);
        valueEventListener = estadosReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot categorias : snapshot.getChildren()){
                    for(DataSnapshot anuncios : categorias.getChildren()){
                            listaAnuncios.add(anuncios.getValue(Anuncio.class));
                    }
                }
                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void recuperarAnunciosPorCategoria(){
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando anuncios...")
                .setCancelable(false)
                .build();
        alertDialog.show();

        listaAnuncios.clear();
        categoriasReference = estadosReference.child(filtroCategoria);
        valueEventListener = categoriasReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot anuncios : snapshot.getChildren()){
                    listaAnuncios.add(anuncios.getValue(Anuncio.class));
                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_sair){
            deslogar();
            invalidateOptionsMenu();
        }
        if(item.getItemId() == R.id.menu_fazerLogin){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        if(item.getItemId() == R.id.menu_anuncios){
            startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void deslogar(){
        try {
            firebaseAuth.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}