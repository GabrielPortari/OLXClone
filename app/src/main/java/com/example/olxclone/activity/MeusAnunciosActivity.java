package com.example.olxclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.olxclone.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MeusAnunciosActivity extends AppCompatActivity {
    private FloatingActionButton fabAdicionarDespesa;
    private RecyclerView recyclerAnuncios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);
        configuracoesIniciais();

        fabAdicionarDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));
            }
        });
    }
    private void configuracoesIniciais(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fabAdicionarDespesa = findViewById(R.id.fabAdicionarAnuncio);
        recyclerAnuncios = findViewById(R.id.recyclerAnuncios);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}