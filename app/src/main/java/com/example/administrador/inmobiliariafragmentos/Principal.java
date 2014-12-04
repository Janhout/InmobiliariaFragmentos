package com.example.administrador.inmobiliariafragmentos;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Principal extends Activity implements FragmentoLista.EscuchadorLista{

    private boolean hayDetalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        FragmentoLista fragmentoLista =(FragmentoLista)getFragmentManager().findFragmentById(R.id.fragmentoLista);
        fragmentoLista.setEscuchadorLista(this);

        FragmentoInmueble f2 = (FragmentoInmueble)getFragmentManager().findFragmentById(R.id.fragmentoInmueble);
        hayDetalle = (f2 != null && f2.isInLayout());
        if(hayDetalle) {
            fragmentoLista.setModoLista(1);
        }else{
            fragmentoLista.setModoLista(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_nuevo:
                FragmentoLista f = (FragmentoLista)getFragmentManager().findFragmentById(R.id.fragmentoLista);
                return f.editarInmueble(-1);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onInmuebleSeleccionado(Inmueble i) {
        if(hayDetalle) {
            if(i!=null) {
                ((FragmentoInmueble) getFragmentManager()
                        .findFragmentById(R.id.fragmentoInmueble)).fotosInmueble(i.getId());
            }else {
                ((FragmentoInmueble)getFragmentManager()
                        .findFragmentById(R.id.fragmentoInmueble)).fotosInmueble(-1);
            }
        }
        else {
            Intent intent = new Intent(this, DatosInmuebles.class);
            intent.putExtra(getString(R.string.identificador), i.getId());
            startActivity(intent);
        }
    }
}
