package com.example.olxclone.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.olxclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {

    /*
    CLASSE PARA FAZER ALGUMAS CONFIGURAÇÕES COM O USUARIO LOGADO NO APP
     */
    public static String getIdUsuario(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuthReference();
        return usuario.getUid();
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth user = ConfiguracaoFirebase.getFirebaseAuthReference();
        return user.getCurrentUser();
    }

    public static boolean atualizaNomeUsuario(String nome){
        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("Perfil", "Erro ao atualizar o nome de perfil");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static Usuario getDadosUsuarioLogado(){
        FirebaseUser firebaseUser = getUsuarioAtual();
        Usuario usuario = new Usuario();

        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());

        return usuario;
    }
}