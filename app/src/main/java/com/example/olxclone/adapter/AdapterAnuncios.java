package com.example.olxclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.olxclone.R;
import com.example.olxclone.model.Anuncio;

import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {
    private Context context;
    private List<Anuncio> listaAnuncios;

    public AdapterAnuncios(Context context, List<Anuncio> listaAnuncios) {
        this.context = context;
        this.listaAnuncios = listaAnuncios;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncio, parent,false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Anuncio anuncio = listaAnuncios.get(position);

        holder.textValor.setText(anuncio.getValor());
        holder.textTitulo.setText(anuncio.getTitulo());

        //recupera a primeira foto da lista de fotos
        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);
        Glide.with(context).load(urlCapa).into(holder.imagemAnuncio);

    }

    @Override
    public int getItemCount() {
        return listaAnuncios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView imagemAnuncio;
        private TextView textTitulo, textValor;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagemAnuncio = itemView.findViewById(R.id.imageAnuncioAdapter);
            textTitulo = itemView.findViewById(R.id.textTituloAdapter);
            textValor = itemView.findViewById(R.id.textValorAdapter);
        }
    }
}
