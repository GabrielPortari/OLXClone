package com.example.olxclone.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.olxclone.R;
import com.example.olxclone.helper.ConfiguracaoFirebase;
import com.example.olxclone.model.Anuncio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageAnuncio0, imageAnuncio1, imageAnuncio2;
    private Spinner spinnerEstado, spinnerCategoria;
    private EditText editTitulo, editDescricao;
    private CurrencyEditText editValor;
    private MaskEditText editTelefone;
    private Button botaoCadastrarAnuncio;
    private int imageViewSelecionada;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaFotosFirebase = new ArrayList<>();
    private Anuncio anuncio;

    private android.app.AlertDialog alertDialog;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        configuracoesIniciais();
        carregarDadosSpinner();
    }
    private void configuracoesIniciais(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageAnuncio0 = findViewById(R.id.imageAnuncio0);
        imageAnuncio0.setOnClickListener(this);
        imageAnuncio1 = findViewById(R.id.imageAnuncio1);
        imageAnuncio1.setOnClickListener(this);
        imageAnuncio2 = findViewById(R.id.imageAnuncio2);
        imageAnuncio2.setOnClickListener(this);

        spinnerEstado = findViewById(R.id.spinnerEstado);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);

        editTitulo = findViewById(R.id.editTextTitulo);
        editValor = findViewById(R.id.editTextValor);
        editDescricao = findViewById(R.id.editTextDescricao);
        editTelefone = findViewById(R.id.editTextTelefone);

        botaoCadastrarAnuncio = findViewById(R.id.buttonCadastrarAnuncio);
        botaoCadastrarAnuncio.setOnClickListener(this);

        //configurar a localidade da moeda para R$ BRL,
        Locale localeBR = new Locale("pt", "BR");
        editValor.setLocale(localeBR);

        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
    }

    private void carregarDadosSpinner(){
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> arrayAdapterEstados = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);
        arrayAdapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerEstado.setAdapter(arrayAdapterEstados);

        String[] categorias = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> arrayAdapterCategorias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        arrayAdapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerCategoria.setAdapter(arrayAdapterCategorias);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int permissoes : grantResults){
            if(permissoes == PackageManager.PERMISSION_DENIED){
                alertaPermissao();
            }
        }
    }

    private void alertaPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas")
                .setMessage("Para utilizar o app, é necessario aceitar as permissões")
                .setCancelable(false)
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageAnuncio0) {
            escolherImagem(0);
        } else if (id == R.id.imageAnuncio1) {
            escolherImagem(1);
        } else if (id == R.id.imageAnuncio2) {
            escolherImagem(2);
        } else if (id == R.id.buttonCadastrarAnuncio){
            validarDadosAnuncio();
        }
    }

    public void escolherImagem(int imgAnuncio){
        imageViewSelecionada = imgAnuncio;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galeriaActivityResult.launch(intent);
    }

    private ActivityResultLauncher<Intent> galeriaActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        //recuperar imagem
                        try{
                            Uri imagemSelecionada = result.getData().getData();
                            String caminhoImagem = imagemSelecionada.toString();

                            if(imageViewSelecionada == 0) {
                                imageAnuncio0.setImageURI(imagemSelecionada);
                                listaFotosRecuperadas.add(caminhoImagem);
                            }else if(imageViewSelecionada == 1){
                                imageAnuncio1.setImageURI(imagemSelecionada);
                                listaFotosRecuperadas.add(caminhoImagem);
                            }else if (imageViewSelecionada == 2){
                                imageAnuncio2.setImageURI(imagemSelecionada);
                                listaFotosRecuperadas.add(caminhoImagem);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    public void validarDadosAnuncio(){
        //recuperar spinners
        String estado = spinnerEstado.getSelectedItem().toString();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        //recuperar entradas
        String titulo = editTitulo.getText().toString();
        String descricao = editDescricao.getText().toString();
        String valor = editValor.getText().toString();
        boolean telefoneIsDone = editTelefone.isDone();
        String telefone = editTelefone.getMasked();
        if(listaFotosRecuperadas.size() != 0){
            if(!estado.isEmpty()){
                if(!categoria.isEmpty()){
                    if(!titulo.isEmpty()){
                        if(!descricao.isEmpty()){
                            if(!valor.isEmpty() && !valor.equals("0")){
                                if(telefoneIsDone){
                                    //CASO TUDO ESTEJA PREENCHIDO CRIA O OBJETO ANUNCIO
                                    anuncio = new Anuncio();
                                    anuncio.setEstado(estado);
                                    anuncio.setCategoria(categoria);
                                    anuncio.setTitulo(titulo);
                                    anuncio.setDescricao(descricao);
                                    anuncio.setValor(valor);
                                    anuncio.setTelefone(telefone);
                                    //objeto anuncio criado, chama a funcao para salvar no firebase
                                    salvarAnuncio();
                                }else{
                                    Toast.makeText(this, "Preencha o campo telefone", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(this, "Preencha o campo valor", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this, "Preencha o campo descrição", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this, "Preencha o campo titulo", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Preencha o campo categoria", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Preencha o campo estado", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Selecione ao menos 1 imagem", Toast.LENGTH_SHORT).show();
        }
    }
    private void salvarAnuncio(){
        /*
            Salva as imagens inicialmente
         */

        for(int i = 0; i<listaFotosRecuperadas.size(); i++){
            alertDialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage("Salvando...")
                    .setCancelable(false)
                    .build();
            alertDialog.show();

            String urlImagem = listaFotosRecuperadas.get(i);
            if(!urlImagem.isEmpty()){
                salvarImagemStorage(urlImagem, listaFotosRecuperadas.size(), i);
            }
        }
    }
    private void salvarImagemStorage(String urlString, int tamLista, int index){
        //Criar o nó storage
        StorageReference anuncioReference = storageReference.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem"+index+".jpg");

        UploadTask uploadTask = anuncioReference.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //sucesso ao fazer upload da imagem
                anuncioReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String stringUrl = task.getResult().toString();
                        listaFotosFirebase.add(stringUrl);

                        //caso todas as fotos tenham sido upadas, salva o resto no database
                        if(listaFotosFirebase.size() == tamLista){
                            anuncio.setFotos(listaFotosFirebase);
                            anuncio.salvarNoFirebase();
                            Toast.makeText(CadastrarAnuncioActivity.this, "Anuncio publicado", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //falha ao fazer upload da imagem
                Toast.makeText(CadastrarAnuncioActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
            }
        });
    }
}