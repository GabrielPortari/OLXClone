package com.example.olxclone.model;

import com.example.olxclone.helper.ConfiguracaoFirebase;
import com.example.olxclone.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Anuncio implements Serializable {
    private String idAnuncio;
    private List<String> fotos;
    private String estado;
    private String categoria;
    private String titulo;
    private String valor;
    private String telefone;
    private String descricao;


    public Anuncio() {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference meusAnuncio = databaseReference.child("meus_anuncios");
        setIdAnuncio(meusAnuncio.push().getKey());
    }
    public void salvarNoFirebase(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference meusAnuncios = databaseReference.child("meus_anuncios");
        DatabaseReference todosAnuncios = databaseReference.child("todos_anuncios");

        /* *
        * SALVAR NO DATABASE DOS MEUS ANUNCIOS
        * meus_anuncios
        *   .id_usuarioLogado
        *       .id_anuncio
        *           .anuncio
        * */
        meusAnuncios
                .child(UsuarioFirebase.getIdUsuario())
                .child(getIdAnuncio())
                .setValue(this);

        /* *
         * SALVAR NO DATABASE DE TODOS OS ANUNCIOS
         * todos_anuncios
         *   .estado
         *       .categoria
         *           .id_anuncio
         *              .anuncio
         * */
        todosAnuncios
                .child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio())
                .setValue(this);
    }
    public void excluirAnuncio(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();

        //remove a postagem dos meus anuncios
        DatabaseReference meusAnuncios = databaseReference.child("meus_anuncios")
                        .child(UsuarioFirebase.getIdUsuario())
                        .child(getIdAnuncio());
        meusAnuncios.removeValue();

        //remove a anuncio no tipo especifico
        DatabaseReference todosAnuncios = databaseReference.child("todos_anuncios")
                        .child(getEstado())
                        .child(getCategoria())
                        .child(getIdAnuncio());
        todosAnuncios.removeValue();
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
