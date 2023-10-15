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

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.olxclone.R;
import com.example.olxclone.helper.Permissoes;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CadastrarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageAnuncio1, imageAnuncio2, imageAnuncio3;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        configuracoesIniciais();
        carregarDadosSpinner();

        botaoCadastrarAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void configuracoesIniciais(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageAnuncio1 = findViewById(R.id.imageAnuncio1);
        imageAnuncio1.setOnClickListener(this);
        imageAnuncio2 = findViewById(R.id.imageAnuncio2);
        imageAnuncio2.setOnClickListener(this);
        imageAnuncio3 = findViewById(R.id.imageAnuncio3);
        imageAnuncio3.setOnClickListener(this);

        spinnerEstado = findViewById(R.id.spinnerEstado);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);

        editTitulo = findViewById(R.id.editTextTitulo);
        editValor = findViewById(R.id.editTextValor);
        editDescricao = findViewById(R.id.editTextDescricao);
        editTelefone = findViewById(R.id.editTextTelefone);

        botaoCadastrarAnuncio = findViewById(R.id.buttonCadastrarAnuncio);

        //configurar a localidade da moeda para R$ BRL,
        Locale localeBR = new Locale("pt", "BR");
        editValor.setLocale(localeBR);

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
        if (id == R.id.imageAnuncio1) {
            escolherImagem(1);
        } else if (id == R.id.imageAnuncio2) {
            escolherImagem(2);
        } else if (id == R.id.imageAnuncio3) {
            escolherImagem(3);
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
                        Bitmap imagem = null;
                        try{
                            Uri imagemSelecionada = result.getData().getData();
                            String caminhoImagem = imagemSelecionada.toString();

                            if(imageViewSelecionada == 1){
                                imageAnuncio1.setImageURI(imagemSelecionada);
                            }else if (imageViewSelecionada == 2){
                                imageAnuncio2.setImageURI(imagemSelecionada);
                            }else if (imageViewSelecionada == 3){
                                imageAnuncio3.setImageURI(imagemSelecionada);
                            }
                            listaFotosRecuperadas.add(caminhoImagem);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
}