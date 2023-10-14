package com.example.olxclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.olxclone.helper.ConfiguracaoFirebase;
import com.example.olxclone.helper.UsuarioFirebase;
import com.example.olxclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import com.example.olxclone.R;

public class CadastroActivity extends AppCompatActivity {
    private TextInputEditText editNome, editEmail, editSenha;
    private Button botaoRegistrar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        configuracoesIniciais();

        botaoRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCadastro(v);
            }
        });
    }
    private void configuracoesIniciais(){
        editNome = findViewById(R.id.textInputNome_signIn);
        editEmail = findViewById(R.id.textInputEmail_signIn);
        editSenha = findViewById(R.id.textInputSenha_signIn);
        botaoRegistrar = findViewById(R.id.buttonRegistrar_signIn);
    }

    public void validarCadastro(View view){
        String textNome = editNome.getText().toString();
        String textEmail = editEmail.getText().toString();
        String textSenha = editSenha.getText().toString();

        if(!textNome.isEmpty()){
            if(!textEmail.isEmpty()){
                if(!textSenha.isEmpty()){
                    Usuario usuario = new Usuario();
                    usuario.setNome(textNome);
                    usuario.setEmail(textEmail);
                    usuario.setSenha(textSenha);

                    cadastrarUsuario(usuario);
                }else{
                    Toast.makeText(CadastroActivity.this, "Preencha todos os campos para continuar", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(CadastroActivity.this, "Preencha todos os campos para continuar", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(CadastroActivity.this, "Preencha todos os campos para continuar", Toast.LENGTH_SHORT).show();
        }
    }
    public void cadastrarUsuario(Usuario usuario){
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuthReference();
        firebaseAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //recupera o uid do usuario
                            String id = task.getResult().getUser().getUid();
                            usuario.setId(id);

                            //salva o usuario no firebase database
                            usuario.salvarNoFirebase();

                            //salva o nome do usuario no profile do firebaseAuth
                            UsuarioFirebase.atualizaNomeUsuario(usuario.getNome());


                            Log.i("AUTH", "Cadastro de usuário completo");
                            Toast.makeText(CadastroActivity.this, "Cadastro completo com sucesso", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        }else{
                            String exception;
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                exception = "Digite uma senha mais forte";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                exception = "Digite um email válido";
                            }catch (FirebaseAuthUserCollisionException e){
                                exception = "Conta já cadastrada";
                            }catch (Exception e){
                                exception = "Erro ao cadastrar usuario: " + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(CadastroActivity.this, exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}