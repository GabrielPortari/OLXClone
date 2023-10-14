package com.example.olxclone.model;

import com.example.olxclone.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Usuario {
    private String email;
    private String senha;
    private String nome;
    private String id;
    private String foto;

    public Usuario() {
    }

    public void salvarNoFirebase(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("usuarios").child(getId());
        databaseReference.setValue(this);
    }

    public void atualizarNoFirebase(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference().child("usuarios").child(getId());
        Map objeto = new HashMap<>();
        objeto.put("nome", getNome());
        objeto.put("foto", getFoto());
        databaseReference.updateChildren(objeto);
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
