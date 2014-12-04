package com.example.administrador.inmobiliariafragmentos;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class DatosInmuebles extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos_inmuebles);

        FragmentoInmueble fragmentoInmueble =
                (FragmentoInmueble)getFragmentManager()
                        .findFragmentById(R.id.fragmentoInmueble);

        fragmentoInmueble.fotosInmueble(getIntent().getIntExtra(getString(R.string.identificador), -1));
    }
}
