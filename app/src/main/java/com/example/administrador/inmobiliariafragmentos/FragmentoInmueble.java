package com.example.administrador.inmobiliariafragmentos;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class FragmentoInmueble extends Fragment {

    private int foto;
    private ArrayList<File> fotos;
    private int idInmueble;
    private ImageView ivImage;
    private Button bSiguiente, bAnterior;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmento_inmueble, container, false);
        return v;
    }

    private void buscarFotos(){
        File [] array = getActivity().getExternalFilesDir(null).listFiles();
        fotos = new ArrayList<File>();
        if(array != null && array.length >0){
            for(File a : array){
                if(a.getPath().contains(getString(R.string.nombre_foto)+idInmueble+"_")){
                    fotos.add(a);
                }
            }
        }
    }

    public void fotosInmueble(int id){
        inicializarTodo();
        idInmueble = id;
        if(idInmueble!=-1) {
            buscarFotos();
        }else{
            fotos = null;
        }
        foto = 0;
        mostrarFoto();
    }

    private void inicializarTodo(){
        ivImage = (ImageView)getView().findViewById(R.id.ivImagen);
        bSiguiente = (Button)getView().findViewById(R.id.botonSiguiente);
        bAnterior = (Button)getView().findViewById(R.id.botonAnterior);

        View.OnClickListener escuchaFotoSiguiente = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idInmueble!=-1) {
                    if (foto == fotos.size() - 1) {
                        foto = 0;
                    } else {
                        foto++;
                    }
                    mostrarFoto();
                }
            }
        };
        ivImage.setOnClickListener(escuchaFotoSiguiente);
        bSiguiente.setOnClickListener(escuchaFotoSiguiente);
        bAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idInmueble!=-1) {
                    if (foto == 0) {
                        foto = fotos.size() - 1;
                    } else {
                        foto--;
                    }
                    mostrarFoto();
                }
            }
        });
    }

    public void mostrarFoto(){
        if(idInmueble!=-1) {
            if (fotos != null && fotos.size() > 0 && fotos.get(foto).exists()) {
                Bitmap d = new BitmapDrawable(getResources(), fotos.get(foto).getAbsolutePath()).getBitmap();
                int nh = (int) (d.getHeight() * (512.0 / d.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
                ivImage.setImageBitmap(scaled);
            } else {
                ivImage.setImageResource(R.drawable.nohay);
            }
        }else{
            ivImage.setImageBitmap(null);
        }
    }
}
