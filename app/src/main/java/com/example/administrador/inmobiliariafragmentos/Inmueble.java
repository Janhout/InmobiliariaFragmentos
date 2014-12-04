package com.example.administrador.inmobiliariafragmentos;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class Inmueble implements Comparable<Inmueble>, Serializable{

    private int id;
    private String calle;
    private String numero;
    private String localidad;
    private String tipo;
    private int precio;

    public Inmueble(){
    }

    public Inmueble(int id, String calle, String numero, String localidad, String tipo, int precio) {
        this.id = id;
        this.calle = calle;
        this.numero = numero;
        this.localidad = localidad;
        this.tipo = tipo;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Inmueble inmueble = (Inmueble) o;

        if (!calle.equals(inmueble.calle)) return false;
        if (!localidad.equals(inmueble.localidad)) return false;
        if (numero != null ? !numero.equals(inmueble.numero) : inmueble.numero != null)
            return false;
        if (!tipo.equals(inmueble.tipo)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = calle.hashCode();
        result = 31 * result + (numero != null ? numero.hashCode() : 0);
        result = 31 * result + localidad.hashCode();
        result = 31 * result + tipo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Inmueble{" +
                "id=" + id +
                ", calle='" + calle + '\'' +
                ", numero='" + numero + '\'' +
                ", localidad='" + localidad + '\'' +
                ", tipo='" + tipo + '\'' +
                ", precio=" + precio +
                '}';
    }

    @Override
    public int compareTo(Inmueble another) {
        Collator collator = Collator.getInstance(Locale.getDefault());
        int compara;
        compara = collator.compare(this.localidad, another.localidad);
        if(compara == 0){
            compara = collator.compare(this.tipo, another.tipo);
        }
        if(compara == 0){
            compara = collator.compare(this.calle, another.calle);
        }
        if(compara == 0){
            compara = collator.compare(this.numero, another.numero);
        }
        return compara;
    }
}
