package com.example.administrador.inmobiliariafragmentos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class FragmentoLista extends Fragment {

    private ArrayList<Inmueble> listaInmuebles;
    private Adaptador ad;
    private ListView lvLista;

    private final int ACTIVIDAD_EDITAR = 1;
    private final int ACTIVIDAD_NUEVO = 2;

    private AlertDialog alerta;

    private EscuchadorLista escuchador;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        leerInmuebles();
        lvLista.setAdapter(ad);
        lvLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list, View view, int pos, long id) {
                if (escuchador!=null) {
                    escuchador.onInmuebleSeleccionado(
                            (Inmueble)lvLista.getAdapter().getItem(pos));
                }
            }
        });
        registerForContextMenu(lvLista);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            Bundle b = data.getExtras();
            Inmueble inm = (Inmueble)b.getSerializable(getString(R.string.inmueble));

            switch (requestCode){
                case ACTIVIDAD_EDITAR:
                    int posicion = b.getInt(getString(R.string.posicion));
                    listaInmuebles.set(posicion, inm);
                    break;
                case ACTIVIDAD_NUEVO:
                    listaInmuebles.add(inm);
                    guardarSharedPreferences(inm.getId()+1);
                    break;
            }
            actualizarLista();
            guardarInmuebles();
        }else{
            if(requestCode == ACTIVIDAD_NUEVO){
                borrarFotos(data.getIntExtra(getString(R.string.nuevo_id),-1));
            }
        }
    }

    /*Método que gestiona el clic sobre un elemento del menu contextual*/
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int index = info.position;
        if(id == R.id.contextual_borrar){
            return borrarInmueble(index);
        }else if(id == R.id.contextual_editar){
            return editarInmueble(index);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmento_lista, container, false);
        listaInmuebles = new ArrayList<>();
        lvLista = (ListView) v.findViewById(R.id.lvLista);
        ad = new Adaptador(this.getActivity(), R.layout.detalle_lista, listaInmuebles);
        return v;
    }

    /*Método que crea el menú contextual*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.contextual,menu);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(alerta != null){
            alerta.dismiss();
        }
    }

    private void actualizarLista(){
        Collections.sort(listaInmuebles);
        ad.notifyDataSetChanged();
    }

    private void borrarFotos(int index){
        if(index!=-1){
            File [] array = getActivity().getExternalFilesDir(null).listFiles();
            if(array != null && array.length >0){
                for(File a : array){
                    if(a.getPath().contains(getString(R.string.nombre_foto)+index+"_")){
                        a.delete();
                    }
                }
            }
        }
    }

    private boolean borrarInmueble(final int index) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getString(R.string.borrar_inmueble));
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View vista = inflater.inflate(R.layout.dialogo_borrar, null);
        alert.setView(vista);
        final String nombre = listaInmuebles.get(index).getTipo() + " (" + listaInmuebles.get(index).getCalle() + " " + listaInmuebles.get(index).getNumero() + ")";
        TextView texto = (TextView)vista.findViewById(R.id.tvBorrar);
        texto.setText(getString(R.string.seguro) + " " + nombre + "?");
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                borrarFotos(listaInmuebles.get(index).getId());
                listaInmuebles.remove(index);
                actualizarLista();
                guardarInmuebles();
                tostada(getString(R.string.elemento_borrado) + " " + nombre);
            }
        });
        alert.setNegativeButton(android.R.string.no, null);
        alerta = alert.create();
        alerta.show();
        return true;
    }

    public boolean editarInmueble(int index) {
        Intent i = new Intent(getActivity(), Editar.class);
        Bundle b = new Bundle();
        if(index > -1) {
            b.putSerializable(getString(R.string.inmueble), listaInmuebles.get(index));
            b.putInt(getString(R.string.posicion), index);
            i.putExtras(b);
            startActivityForResult(i, ACTIVIDAD_EDITAR);
        }else {
            b.putInt(getString(R.string.nuevo_id), leerSharedPreferences());
            i.putExtras(b);
            startActivityForResult(i, ACTIVIDAD_NUEVO);
        }

        return true;
    }

    private void guardarInmuebles(){
        if(isModificable()) {
            File f = new File(getActivity().getExternalFilesDir(null), getString(R.string.nombreFichero));
            FileOutputStream fosxml = null;
            try {
                fosxml = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                tostada(getString(R.string.noFicheroDatos));
            }
            XmlSerializer docxml = Xml.newSerializer();
            try {
                docxml.setOutput(fosxml, getString(R.string.codificacion));
                docxml.startDocument(null, true);
                docxml.setFeature(getString(R.string.cabeceraXML), true);
                docxml.startTag(null, getString(R.string.inmuebles));

                for (int i = 0; i < listaInmuebles.size(); i++) {
                    docxml.startTag(null, getString(R.string.inmueble));
                    docxml.attribute(null, getString(R.string.tipo), String.valueOf(listaInmuebles.get(i).getTipo()));
                    docxml.startTag(null, getString(R.string.direccion));
                    docxml.text(listaInmuebles.get(i).getCalle());
                    docxml.endTag(null, getString(R.string.direccion));
                    docxml.startTag(null, getString(R.string.numero));
                    docxml.text(listaInmuebles.get(i).getNumero());
                    docxml.endTag(null, getString(R.string.numero));
                    docxml.startTag(null, getString(R.string.localidad));
                    docxml.text(listaInmuebles.get(i).getLocalidad());
                    docxml.endTag(null, getString(R.string.localidad));
                    docxml.startTag(null, getString(R.string.precio));
                    docxml.text(String.valueOf(listaInmuebles.get(i).getPrecio()));
                    docxml.endTag(null, getString(R.string.precio));
                    docxml.startTag(null, getString(R.string.id));
                    docxml.text(String.valueOf(listaInmuebles.get(i).getId()));
                    docxml.endTag(null, getString(R.string.id));
                    docxml.endTag(null, getString(R.string.inmueble));
                }
                docxml.endDocument();
                docxml.flush();
                fosxml.close();
            } catch (IOException e) {
                tostada(getString(R.string.noEscribeMemoria));
            }
        }else{
            tostada(getString(R.string.noEscribeMemoria));
        }
    }

    private void guardarSharedPreferences(int valor){
        SharedPreferences pc;
        SharedPreferences.Editor ed;
        pc = getActivity().getSharedPreferences(getString(R.string.preferencias), getActivity().MODE_PRIVATE);
        ed = pc.edit();
        ed.putInt(getString(R.string.nuevo_id), valor);
        ed.commit();
    }

    /*Método para ver si podemos leer la memoria externa*/
    private boolean isLegible() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /*Metodo para comprobar si podemos escribir en la memoria externa*/
    private boolean isModificable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void leerInmuebles(){
        if(isLegible()) {
            XmlPullParser lectorxml = Xml.newPullParser();
            File f = new File(getActivity().getExternalFilesDir(null), getString(R.string.nombreFichero));
            int evento = -1;
            try {
                lectorxml.setInput(new FileInputStream(f), getString(R.string.codificacion));
                evento = lectorxml.getEventType();
            } catch (Exception e) {
                tostada(getString(R.string.noLeerDatos));
            }
            Inmueble inmueble = null;
            String etiqueta;
            try {
                while (evento != XmlPullParser.END_DOCUMENT) {
                    if (evento == XmlPullParser.START_TAG) {
                        etiqueta = lectorxml.getName();
                        if (etiqueta.compareTo(getString(R.string.inmueble)) == 0) {
                            inmueble = new Inmueble();
                            inmueble.setTipo(lectorxml.getAttributeValue(null, getString(R.string.tipo)));
                        }
                        if (etiqueta.compareTo(getString(R.string.direccion)) == 0) {
                            inmueble.setCalle(lectorxml.nextText());
                        } else if (etiqueta.compareTo(getString(R.string.numero)) == 0) {
                            inmueble.setNumero(lectorxml.nextText());
                        } else if (etiqueta.compareTo(getString(R.string.localidad)) == 0) {
                            inmueble.setLocalidad(lectorxml.nextText());
                        } else if (etiqueta.compareTo(getString(R.string.precio)) == 0) {
                            inmueble.setPrecio(Integer.valueOf(lectorxml.nextText()));
                        } else if (etiqueta.compareTo(getString(R.string.id)) == 0) {
                            inmueble.setId(Integer.valueOf(lectorxml.nextText()));
                        }
                    } else if (evento == XmlPullParser.END_TAG) {
                        etiqueta = lectorxml.getName();
                        if (etiqueta.compareTo(getString(R.string.inmueble)) == 0) {
                            listaInmuebles.add(inmueble);
                        }
                    }
                    evento = lectorxml.next();
                }
            } catch (Exception e) {
                tostada(getString(R.string.noLeeMemoria));
            }
        }else{
            tostada(getString(R.string.noLeeMemoria));
        }
    }

    private int leerSharedPreferences(){
        SharedPreferences pc;
        SharedPreferences.Editor ed;
        pc = getActivity().getSharedPreferences(getString(R.string.preferencias), getActivity().MODE_PRIVATE);
        return pc.getInt(getString(R.string.nuevo_id), 0);
    }

    public void setModoLista(int modo){
        lvLista.clearChoices();
        lvLista.setChoiceMode(modo);
    }

    private void tostada(String s){
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    public interface EscuchadorLista{
        public void onInmuebleSeleccionado(Inmueble i);
    }

    public void setEscuchadorLista(EscuchadorLista escuchador) {
        this.escuchador=escuchador;
    }
}
