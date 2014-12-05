package com.example.administrador.inmobiliariafragmentos;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class Editar extends Activity {

    private ArrayList<String> tipo;
    private int id;
    private TextView tvFotos;
    private EditText etLocalidad, etNumero, etCalle, etPrecio;
    private Spinner spTipo;

    private boolean editar;
    private final int ACTIVIDAD_CAMARA = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Activity.RESULT_OK){
            switch (requestCode){
                case ACTIVIDAD_CAMARA:
                    cargarFotos();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        cancelar(null);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar);
        rellenarTipo();
        iniciarComponentes();
        cargarDatos();
        if(editar){cargarFotos();}
    }

    public void aceptar(View view){
        Inmueble inmueble = recogerDatos();
        Intent i = new Intent();
        Bundle b = new Bundle();
        if(editar) {
            b.putInt(getString(R.string.posicion), getIntent().getExtras().getInt(getString(R.string.posicion)));
        }
        b.putSerializable(getString(R.string.inmueble), inmueble);
        i.putExtras(b);
        setResult(RESULT_OK, i);
        finish();
    }

    public void cancelar(View view){
        if(editar) {
            setResult(RESULT_CANCELED);
        }else{
            Intent i = new Intent();
            i.putExtra(getString(R.string.nuevo_id), id);
            setResult(RESULT_CANCELED,i);
        }
        finish();
    }

    private void cargarDatos(){
        Inmueble inmueble = (Inmueble)getIntent().getExtras().getSerializable(getString(R.string.inmueble));
        if(inmueble!=null){
            editar = true;
            etCalle.setText(inmueble.getCalle());
            etPrecio.setText(inmueble.getPrecio()+"");
            etLocalidad.setText(inmueble.getLocalidad());
            etNumero.setText(inmueble.getNumero());
            spTipo.setSelection(tipo.indexOf(inmueble.getTipo()));

            id = inmueble.getId();
        }else{
            editar = false;
            id = getIntent().getExtras().getInt(getString(R.string.nuevo_id));
        }
    }

    private void cargarFotos(){
        File [] array = getExternalFilesDir(null).listFiles();
        tvFotos.setText("");
        if(array != null && array.length >0){
            String [] ruta;
            for(File a : array){
                if(a.getPath().contains(getString(R.string.nombre_foto)+id)){
                    ruta = a.getPath().split("/");
                    tvFotos.append(ruta[ruta.length-1]+"\n");
                }
            }
        }else{
            tvFotos.setText(getString(R.string.no_fotos));
        }
    }

    private void iniciarComponentes(){
        etCalle = (EditText)findViewById(R.id.etCalle);
        etPrecio = (EditText)findViewById(R.id.etPrecio);
        etLocalidad = (EditText)findViewById(R.id.etLocalidad);
        etNumero = (EditText)findViewById(R.id.etNumero);
        spTipo = (Spinner)findViewById(R.id.spTipo);
        tvFotos = (TextView)findViewById(R.id.tvFotos);

        spTipo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipo));
    }

    private String nombreFoto(){
        SimpleDateFormat formatoFecha = new SimpleDateFormat(getString(R.string.formato_fecha));
        String fecha = formatoFecha.format(new Date());
        String nombreFoto = getString(R.string.nombre_foto)+ id +"_" + fecha;
        return nombreFoto;
    }

    public void nuevaFoto(View view){
        Intent tomaFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tomaFoto.resolveActivity(getPackageManager()) != null) {
            File fichero = new File(getExternalFilesDir(null), nombreFoto() + getString(R.string.extension));

            if (fichero != null) {
                tomaFoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fichero));
                startActivityForResult(tomaFoto, ACTIVIDAD_CAMARA);
            }
        }
    }

    private Inmueble recogerDatos(){
        String calle = etCalle.getText().toString();
        String tipo = spTipo.getSelectedItem().toString();
        String num = etNumero.getText().toString();
        String localidad = etLocalidad.getText().toString();
        int precio = 0;
        try {
            precio = Integer.parseInt(etPrecio.getText().toString());
        }catch (NumberFormatException e){
            precio = 0;
        }
        return new Inmueble(id, calle, num, localidad, tipo, precio);
    }

    private void rellenarTipo(){
        tipo = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.valoresTipo)));
    }
}
