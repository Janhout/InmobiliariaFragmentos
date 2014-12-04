package com.example.administrador.inmobiliariafragmentos;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Adaptador extends ArrayAdapter<Inmueble> {

    private List<Inmueble> lista;
    private int recurso;
    private static LayoutInflater i;

    public Adaptador(Context context, int resource, List<Inmueble> objects) {
        super(context, resource, objects);
        recurso = resource;
        lista = objects;
        this.i = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView == null){
            convertView = i.inflate(recurso, null);
            vh = new ViewHolder();
            vh.tvLocalidad = (TextView)convertView.findViewById(R.id.tvLocalidad);
            vh.tvPrecio = (TextView)convertView.findViewById(R.id.tvPrecio);
            vh.tvDireccion = (TextView)convertView.findViewById(R.id.tvDireccion);
            vh.ivTipo = (ImageView)convertView.findViewById(R.id.ivFoto);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        vh.tvLocalidad.setText(lista.get(position).getLocalidad());
        vh.tvPrecio.setText(String.valueOf(lista.get(position).getPrecio()));
        vh.tvDireccion.setText(lista.get(position).getCalle() + " " + lista.get(position).getNumero());

        String tipo = lista.get(position).getTipo();
        int imagen = conseguirImagen(tipo);
        vh.ivTipo.setImageResource(imagen);

        return convertView;
    }

    private int conseguirImagen(String tipo){
        int resultado = -1;
        if(tipo.equals(getContext().getString(R.string.local_tipo))){
            resultado = R.drawable.local;
        }else if(tipo.equals(getContext().getString(R.string.adosada_tipo))){
            resultado = R.drawable.adosada;
        }else if(tipo.equals(getContext().getString(R.string.chalet_tipo))){
            resultado = R.drawable.chalet;
        }else if(tipo.equals(getContext().getString(R.string.parcela_tipo))){
            resultado = R.drawable.parcela;
        }else if(tipo.equals(getContext().getString(R.string.cortijo_tipo))){
            resultado = R.drawable.cortijo;
        }else if(tipo.equals(getContext().getString(R.string.piso_tipo))){
            resultado = R.drawable.piso;
        }else {
            resultado = R.drawable.otro;
        }
        return resultado;
    }

    public static class ViewHolder{
        public TextView tvLocalidad, tvPrecio, tvDireccion;
        public ImageView ivTipo;
    }
}