/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicacion;

/**
 *
 * @author 2108616
 */
public abstract class Elemento {
    private String id;
    private String tipo;
    private String name;
    private String descripcion;
    
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Elemento(String id, String name, String descripcion, String tipo) {
        this.id = id;
        this.name = name;
        this.descripcion = descripcion;
        this.tipo= tipo;
    }   
    
    public String toString(){
        return name+" "+descripcion+" "+tipo;
    }
    
}
