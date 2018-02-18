/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicacion;

import java.util.Scanner;

/**
 *
 * @author diana
 */
public class main {
    public static void main(String[] args) throws Exception {
        Scanner entrada=new Scanner(System.in);
        System.out.println("Digite el nombre del archivo bpmn");
        String filename = entrada.nextLine();
        BpmnDoc.leerBPMN2(filename);
        BpmnDoc.crearArchivoHtml(filename);
        
    }
}
