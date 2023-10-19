package com.example.olxclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.olxclone.R;
import com.example.olxclone.model.Anuncio;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class VisualisarAnuncioActivity extends AppCompatActivity {
    private CarouselView carouselView;
    private TextView textTitulo, textDescricao, textValor, textRegiao;
    private Button buttonVisualizar;
    private Anuncio anuncioSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualisar_anuncio);
        configuracoesIniciais();

        anuncioSelecionado = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");
        if(anuncioSelecionado != null){
            textTitulo.setText(anuncioSelecionado.getTitulo());
            textValor.setText(anuncioSelecionado.getValor());
            textRegiao.setText(anuncioSelecionado.getEstado());
            textDescricao.setText(anuncioSelecionado.getDescricao());

            //Configuração do carousel view
            ImageListener imageListener = (position, imageView) -> {
                String urlString = anuncioSelecionado.getFotos().get(position);
                Glide.with(VisualisarAnuncioActivity.this).load(urlString).into(imageView);
            };
            carouselView.setPageCount(anuncioSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);
        }
        buttonVisualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visualizarTelefone();
            }
        });
    }

    public void visualizarTelefone(){
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", anuncioSelecionado.getTelefone(), null));
        startActivity(i);
    }

    private void configuracoesIniciais(){
        carouselView = findViewById(R.id.carouselView);
        textTitulo = findViewById(R.id.textTituloVisualizar);
        textDescricao = findViewById(R.id.textDescricaoVisualizar);
        textValor = findViewById(R.id.textValorVisualizar);
        textRegiao = findViewById(R.id.textRegiaoVisualizar);
        buttonVisualizar = findViewById(R.id.buttonTelefoneVisualizar);

    }
}