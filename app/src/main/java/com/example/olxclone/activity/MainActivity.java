package com.example.olxclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.olxclone.R;
import com.example.olxclone.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configuracoesIniciais();

    }

    private void configuracoesIniciais(){
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();
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