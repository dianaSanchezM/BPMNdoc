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
public class Flujo extends Elemento{
    private String origen;
    private String destino;

    public Flujo(String id, String name, String descripcion, String tipo) {
        super(id, name, descripcion, tipo);
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

   
}
