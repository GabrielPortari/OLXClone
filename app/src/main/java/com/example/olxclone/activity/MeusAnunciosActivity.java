package com.example.olxclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.olxclone.R;
import com.example.olxclone.adapter.AdapterAnuncios;
import com.example.olxclone.helper.ConfiguracaoFirebase;
import com.example.olxclone.helper.UsuarioFirebase;
import com.example.olxclone.model.Anuncio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MeusAnunciosActivity extends AppCompatActivity {
    private FloatingActionButton fabAdicionarDespesa;
    private RecyclerView recyclerAnuncios;
    private List<Anuncio> meusAnuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private String idUsuario;
    private Anuncio anuncio;
    private android.app.AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        configuracoesIniciais();
        swipe();

        //configuracoes recycler view
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(getApplicationContext(), meusAnuncios);
        recyclerAnuncios.setAdapter(adapterAnuncios);

        //recuperar anuncios
        recuperarAnuncios();

        fabAdicionarDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));
            }
        });
    }
    private void recuperarAnuncios(){
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando anuncios...")
                .setCancelable(false)
                .build();
        alertDialog.show();

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                meusAnuncios.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    meusAnuncios.add(ds.getValue(Anuncio.class));
                }
                adapterAnuncios.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        alertDialog.dismiss();
    }

    private void configuracoesIniciais(){
        idUsuario = UsuarioFirebase.getIdUsuario();
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference()
                .child("meus_anuncios")
                .child(idUsuario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fabAdicionarDespesa = findViewById(R.id.fabAdicionarAnuncio);
        recyclerAnuncios = findViewById(R.id.recyclerMeusAnuncios);
    }
    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlag = ItemTouchHelper.ACTION_STATE_IDLE; // drag faz menção a mover o item da recycler para qualquer lado
                int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END; // swipe faz menção para arrastar para os lados, neste caso vai para direita/esquerda
                return makeMovementFlags(dragFlag, swipeFlag); // retorna drag = inativo, swipe = para esquerda e direita
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirPublicacao(viewHolder); // ao usar swipe na horizontal, chama o método excluirMovimentacao para o viewHolder que foi swipado
            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerAnuncios);
    }
    private void excluirPublicacao(RecyclerView.ViewHolder viewHolder){
        //Configuração do AlertDialog, que pede a confirmação do usuário para excluir ou não uma movimentação do app/firebase
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exclusão");
        alertDialog.setMessage("Deseja excluir essa publicação?");
        alertDialog.setCancelable(false);
        //Configuração para caso o usuário deseja confirmar a exclusão
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Recupera a posição do viewHolder swipado no adapter;
                int posicao = viewHolder.getAdapterPosition();
                //Apos recuperar a posição do viewHolder, recupera a posição da movimentação na lista
                anuncio = meusAnuncios.get(posicao);
                anuncio.excluirAnuncio();
            }
        });
        //Configuração caso o usuário cancele a exclusão
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Apenas é mostrado um toast e o item swipado retorna para a tela
                Toast.makeText(MeusAnunciosActivity.this, "Exclusão cancelada", Toast.LENGTH_SHORT).show();
                adapterAnuncios.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarAnuncios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListener);
    }
}