package aplicacion;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//Java DOM parser classes
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

//These classes read the sample XML file and manage archivoput:
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

//Finally, import the W3C definitions for a DOM, DOM exceptions, entities and nodes:
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author 2108310
 */
public class BpmnDoc {
    static final String archivoputEncoding = "UTF-8";
    private static ArrayList<Elemento> procesos=new ArrayList();
    private static ArrayList<Elemento> actividades=new ArrayList();
    private static ArrayList<Flujo> reglasDeNegocio=new ArrayList();
    private static ArrayList<Elemento> sistemas=new ArrayList();
    private static ArrayList<Elemento> eventosDeInicio=new ArrayList();
    private static ArrayList<Elemento> eventosDeSalida=new ArrayList();
    private static ArrayList<Elemento> lineasDeNado=new ArrayList();
    private static ArrayList<Flujo> flujos=new ArrayList();
    private static ArrayList<Elemento> eventos=new ArrayList<>();
    private static HashMap<String, Elemento> ids=new HashMap<String,Elemento>();

    private static void usage() {
        System.out.println("Usage: xmlechoparser file1");
    }

    
    public static void leerBPMN2(String nombre){
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            //filename = args[0]; 
            Document doc = db.parse(new File(nombre));
            navigate(doc);
        } catch (Exception e){
            
        }
        
    }

    private static void navigate(Node n) {
        navigate(n.getChildNodes().item(0).getChildNodes().item(3), "");
        crearProcesos(n.getChildNodes().item(0).getChildNodes().item(3));
        navigate(n.getChildNodes().item(0).getChildNodes().item(3), 5);
    }

    private static void navigate(Node n, String prefix) {
        /*
        System.out.println(prefix + "Node name : " + n.getNodeName());
        System.out.println(prefix + "Node type : " + getNodeTypeName(n.getNodeType()));
        System.out.println(prefix + "Node value: " + n.getNodeValue());
        */
        // Navegar los atributos del nodo
        NamedNodeMap childAttributes = n.getAttributes();
        if (childAttributes != null) {
            for (int i = 0; i < childAttributes.getLength(); i++) {
                navigate(childAttributes.item(i), prefix + "|a----");
            }
        }
        
        //Navegar los nodos hijo del nodo actual
        NodeList childnodes = n.getChildNodes();
        for (int i = 0; i < childnodes.getLength(); i++) {
            navigate(childnodes.item(i), prefix + "|-----");
        }
        
    }
    
    private static void navigate(Node n, int s){
        NodeList nodes=n.getChildNodes();
        for (int i=0; i<nodes.getLength();i++){
            if (nodes.item(i).getNodeType()==Node.ELEMENT_NODE){
                Element elemento= (Element) nodes.item(i);
                switch (elemento.getTagName().split(":")[1]) {
                    case ("laneSet"):
                        crearLineasDeNado(elemento.getChildNodes().item(1));
                        
                        break;
                    case ("startEvent"):
                        crearEventosDeInicio(elemento);
                        break;
                    case ("endEvent"):
                        crearEventosDeFinalizacion(elemento);
                        break;
                    case ("intermediateThrowEvent"):
                    case ("intermediateCatchEvent"):
                        crearEventos(elemento);
                        break;
                    case ("serviceTask"):
                    case ("userTask"):
                    case ("scriptTask"):
                    case ("receiveTask"):
                    case ("sendTask"):
                    case ("task"):
                        crearActividades(elemento);
                        break;
                    case ("sequenceFlow"):
                        if (elemento.getChildNodes().getLength()>0){
                            crearReglasDeNegocio(elemento);
                        }else {
                            crearFlujos(elemento);
                        }
                    }
                }
            }
        }

        private static String getNodeTypeName(short nodeType) {
            String respuesta = "";
            switch (nodeType) {
                case Node.ATTRIBUTE_NODE:
                    respuesta = "ATTRIBUTE_NODE";
                    break;
                case Node.CDATA_SECTION_NODE:
                    respuesta = "CDATA_SECTION_NODE";
                    break;
                case Node.COMMENT_NODE:
                    respuesta = "COMMENT_NODE";
                    break;
                case Node.DOCUMENT_FRAGMENT_NODE:
                    respuesta = "DOCUMENT_FRAGMENT_NODE";
                    break;
                case Node.DOCUMENT_NODE:
                    respuesta = "DOCUMENT_NODE";
                    break;
                case Node.DOCUMENT_POSITION_CONTAINED_BY:
                    respuesta = "DOCUMENT_POSITION_CONTAINED_BY";
                    break;
                case Node.DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC:
                    respuesta = "DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC";
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                    respuesta = "DOCUMENT_TYPE_NODE";
                    break;
                case Node.ELEMENT_NODE:
                    respuesta = "ELEMENT_NODE";
                    break;
                case Node.ENTITY_NODE:
                    respuesta = "ENTITY_NODE";
                    break;
                case Node.TEXT_NODE:
                    respuesta = "TEXT_NODE";
                    break;
                default:
                    respuesta = "other type";
                    break;
        }

        return respuesta;
    }
    
    private static void crearProcesos(Node n){
        String nombre;
        nombre=n.getAttributes().getNamedItem("name").getNodeValue();
        int total=procesos.size();
        for (int i=0; i<n.getChildNodes().getLength();i++){
            if (n.getChildNodes().item(i).getNodeName().equals("model:documentation")){
                procesos.add(new ElementoSimple(n.getAttributes().getNamedItem("id").getNodeValue(),nombre,n.getChildNodes().item(i).getChildNodes().item(0).getNodeValue(),'P'+String.valueOf(procesos.size()+1)));
                
            }
        }
        if (procesos.size()==total){
            procesos.add(new ElementoSimple(n.getAttributes().getNamedItem("id").getNodeValue(),nombre,"",'P'+String.valueOf(procesos.size()+1)));
        }
        ids.put(procesos.get(procesos.size()-1).getId(), procesos.get(procesos.size()-1));
    }
    
    private static void crearActividades(Node n){
        for (int i=0; i<n.getChildNodes().getLength();i++){
            if (n.getChildNodes().item(i).getNodeName().equals("model:documentation")){
                actividades.add(new ElementoSimple(n.getAttributes().getNamedItem("id").getNodeValue(),n.getAttributes().getNamedItem("name").getNodeValue(),n.getChildNodes().item(i).getChildNodes().item(0).getNodeValue(),"AC"+String.valueOf(actividades.size()+1)));
            }
        }
        ids.put(actividades.get(actividades.size()-1).getId(), actividades.get(actividades.size()-1));
    }
    private static void crearReglasDeNegocio(Node n){
        for (int i=0; i<n.getChildNodes().getLength();i++){
            if (n.getChildNodes().item(i).getNodeName().equals("model:conditionExpression")){
                Flujo nuevo=new Flujo(n.getAttributes().getNamedItem("id").getNodeValue(),"",n.getChildNodes().item(i).getTextContent(),"RN"+String.valueOf(reglasDeNegocio.size()+1));
                nuevo.setOrigen(n.getAttributes().getNamedItem("sourceRef").getNodeValue());
                nuevo.setDestino(n.getAttributes().getNamedItem("targetRef").getNodeValue());
                reglasDeNegocio.add(nuevo);
            }
        }
    }
    private static void crearEventosDeInicio(Node n){
        for (int i=0; i<n.getChildNodes().getLength();i++){
            if (n.getChildNodes().item(i).getNodeName().equals("model:documentation")){
                eventosDeInicio.add(new ElementoSimple(n.getAttributes().getNamedItem("id").getNodeValue(),n.getAttributes().getNamedItem("name").getNodeValue(),n.getChildNodes().item(i).getChildNodes().item(0).getNodeValue(),"EI"+String.valueOf(eventosDeInicio.size()+1)));
            }
        }
        ids.put(eventosDeInicio.get(eventosDeInicio.size()-1).getId(), eventosDeInicio.get(eventosDeInicio.size()-1));
    }
    
    private static void crearEventos(Node n){
        int total=eventosDeInicio.size();
        for (int i=0; i<n.getChildNodes().getLength();i++){
            if (n.getChildNodes().item(i).getNodeName().equals("model:documentation")){
                eventos.add(new ElementoSimple(n.getAttributes().getNamedItem("id").getNodeValue(),n.getAttributes().getNamedItem("name").getNodeValue(),n.getChildNodes().item(i).getChildNodes().item(0).getNodeValue(),"E"+String.valueOf(eventos.size()+1)));
            }
        }       
        ids.put(eventos.get(eventos.size()-1).getId(), eventos.get(eventos.size()-1));
        if (eventosDeInicio.size()==total){
            eventos.add(new ElementoSimple(n.getAttributes().getNamedItem("id").getNodeValue(),n.getAttributes().getNamedItem("name").getNodeValue(),"","E"+String.valueOf(eventos.size()+1)));            
        }
    }
    
    private static void crearEventosDeFinalizacion(Node n){
        int total=eventosDeSalida.size();
        for (int i=0; i<n.getChildNodes().getLength();i++){
            if (n.getChildNodes().item(i).getNodeName().equals("model:documentation")){
                eventosDeSalida.add(new ElementoSimple(n.getAttributes().getNamedItem("id").getNodeValue(),n.getAttributes().getNamedItem("name").getNodeValue(),n.getChildNodes().item(i).getChildNodes().item(0).getNodeValue(),"EF"+String.valueOf(eventosDeSalida.size()+1)));
            }
        }
        if (eventosDeSalida.size()==total){
            eventosDeSalida.add(new ElementoSimple(n.getAttributes().getNamedItem("id").getNodeValue(),n.getAttributes().getNamedItem("name").getNodeValue(),"","EF"+String.valueOf(eventosDeSalida.size()+1))); 
        }
        ids.put(eventosDeSalida.get(eventosDeSalida.size()-1).getId(), eventosDeSalida.get(eventosDeSalida.size()-1));
    }
    
    private static void crearLineasDeNado(Node n){
        int total=lineasDeNado.size();
        for (int i=0; i<n.getChildNodes().getLength();i++){
            if (n.getChildNodes().item(i).getNodeName().equals("model:documentation")){
                lineasDeNado.add(new ElementoSimple(n.getAttributes().getNamedItem("id").getNodeValue(),n.getAttributes().getNamedItem("name").getNodeValue(),n.getChildNodes().item(i).getChildNodes().item(0).getNodeValue(),"LN"+String.valueOf(lineasDeNado.size()+1)));
            }
        }
        if (lineasDeNado.size()==total){
            lineasDeNado.add(new ElementoSimple(n.getAttributes().getNamedItem("id").getNodeValue(),n.getAttributes().getNamedItem("name").getNodeValue(),"","LN"+String.valueOf(lineasDeNado.size()+1)));
        }
        ids.put(lineasDeNado.get(lineasDeNado.size()-1).getId(), lineasDeNado.get(lineasDeNado.size()-1));
    }
    
    private static void crearFlujos(Node n){
        Flujo nuevo=new Flujo(n.getAttributes().getNamedItem("id").getNodeValue(),n.getAttributes().getNamedItem("name").getNodeValue(),"","F"+String.valueOf(flujos.size()+1));
        nuevo.setOrigen(n.getAttributes().getNamedItem("sourceRef").getNodeValue());
        nuevo.setDestino(n.getAttributes().getNamedItem("targetRef").getNodeValue());
        flujos.add(nuevo);
    }
    
    public static void crearArchivoHtml(String nombre){
        try (BufferedWriter archivo = new BufferedWriter(new PrintWriter(new File("documentacion" + nombre + ".html")));) {
            archivo.append("<h1 style=\"color: #000000;\">DOCUMENTACI&Oacute;N " + nombre.toUpperCase() + "&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; </h1>\n");
            crearTablaProcesos(archivo);
            crearTablaActividades(archivo);
            crearTablaReglasDeNegocio(archivo);
            crearTablaEventosDeInicio(archivo);
            crearTablaEventos(archivo);
            crearTablaEventosDeFin(archivo);
            crearTablaLineasDeNado(archivo);
            crearTablaFlujos(archivo);
        } catch (Exception e) {
        }
    }

    private static void crearTablaProcesos(BufferedWriter archivo) throws IOException{
        if (!procesos.isEmpty()) {
            archivo.append("<h2 style=\"color: #000000;\">PROCESOS:</h2>\n");
            archivo.append("<table class=\"table\" style=\"width: 717px;\" border = \"2\" cellpadding = \"2\" cellspacing = \"2\">\n");
            archivo.append("<thead>\n");
            archivo.append("<tr style=\"height: 13.5667px;\">\n");
            archivo.append("<td style=\"height: 13.5667px; width: 78px;\">Tipo</td>");
            archivo.append("<td style=\"height: 13.5667px; width: 202px;\">Nombre</td>\n");
            archivo.append("<td style=\"height: 13.5667px; width: 452px;\">Descripcion</td>\n");
            archivo.append("</tr>\n");
            archivo.append("</thead>\n");
            archivo.append("<tbody>\n");
            for (Elemento i : procesos) {
                archivo.append("<tr style=\"height: 21px;\">\n");
                archivo.append("<td style=\"height: 21px; width: 78px;\"><strong>" + i.getTipo() + "</strong></td>\n");
                archivo.append("<td style=\"height: 21px; width: 202px;\">" + i.getName() + "</td>\n");
                archivo.append("<td style=\"height: 21px; width: 452px;\">" + i.getDescripcion() + "</td>\n");
                archivo.append("</tr>\n");
            }
            archivo.append("</tbody>\n");
            archivo.append("</table>\n");
        }
    }
    
    private static void crearTablaActividades(BufferedWriter archivo) throws IOException{
        if (!actividades.isEmpty()) {
            archivo.append("<h2 style=\"color: #000000;\">ACTIVIDADES:</h2>\n");
            archivo.append("<table class=\"table\" style=\"width: 717px;\" border = \"2\" cellpadding = \"2\" cellspacing = \"2\">\n");
            archivo.append("<thead>\n");
            archivo.append("<tr style=\"height: 13.5667px;\">\n");
            archivo.append("<td style=\"height: 13.5667px; width: 78px;\">Tipo</td>");
            archivo.append("<td style=\"height: 13.5667px; width: 202px;\">Nombre</td>\n");
            archivo.append("<td style=\"height: 13.5667px; width: 452px;\">Descripcion</td>\n");
            archivo.append("</tr>\n");
            archivo.append("</thead>\n");
            archivo.append("<tbody>\n");

            for (Elemento i : actividades) {
                archivo.append("<tr style=\"height: 21px;\">\n");
                archivo.append("<td style=\"height: 21px; width: 78px;\"><strong>" + i.getTipo() + "</strong></td>\n");
                archivo.append("<td style=\"height: 21px; width: 202px;\">" + i.getName() + "</td>\n");
                archivo.append("<td style=\"height: 21px; width: 452px;\">" + i.getDescripcion() + "</td>\n");
                archivo.append("</tr>\n");
            }

            archivo.append("</tbody>\n");
            archivo.append("</table>\n");
        }
    }
    
    private static void crearTablaReglasDeNegocio(BufferedWriter archivo) throws IOException{
        if (!actividades.isEmpty()) {
            archivo.append("<h2 style=\"color: #000000;\">REGLAS DE NEGOCIO:</h2>\n");
            archivo.append("<table class=\"table\" style=\"width: 717px;\" border = \"2\" cellpadding = \"2\" cellspacing = \"2\">\n");
            archivo.append("<thead>\n");
            archivo.append("<tr style=\"height: 13.5667px;\">\n");
            archivo.append("<td style=\"height: 13.5667px; width: 78px;\">Tipo</td>");
            archivo.append("<td style=\"height: 13.5667px; width: 202px;\">Nombre</td>\n");
            archivo.append("<td style=\"height: 13.5667px; width: 452px;\">Expresion</td>\n");
            archivo.append("<td style=\"height: 13.5667px; width: 452px;\">Origen</td>\n");
            archivo.append("<td style=\"height: 13.5667px; width: 452px;\">Objetivo</td>\n");
            archivo.append("</tr>\n");
            archivo.append("</thead>\n");
            archivo.append("<tbody>\n");

            for (Flujo i : reglasDeNegocio) {
                archivo.append("<tr style=\"height: 21px;\">\n");
                archivo.append("<td style=\"height: 21px; width: 78px;\"><strong>" + i.getTipo() + "</strong></td>\n");
                archivo.append("<td style=\"height: 21px; width: 202px;\">" + i.getName() + "</td>\n");
                archivo.append("<td style=\"height: 21px; width: 452px;\">" + i.getDescripcion() + "</td>\n");
                archivo.append("<td style=\"height: 21px; width: 452px;\">" + ids.get(i.getOrigen())+ "</td>\n");
                archivo.append("<td style=\"height: 21px; width: 452px;\">" + ids.get(i.getDestino())+ "</td>\n");
                archivo.append("</tr>\n");
            }

            archivo.append("</tbody>\n");
            archivo.append("</table>\n");
        }
    }
    
    private static void crearTablaEventosDeInicio(BufferedWriter archivo) throws IOException{
        if (!actividades.isEmpty()) {
            archivo.append("<h2 style=\"color: #000000;\">EVENTOS DE INICIO:</h2>\n");
            archivo.append("<table class=\"table\" style=\"width: 717px;\" border = \"2\" cellpadding = \"2\" cellspacing = \"2\">\n");
            archivo.append("<thead>\n");
            archivo.append("<tr style=\"height: 13.5667px;\">\n");
            archivo.append("<td style=\"height: 13.5667px; width: 78px;\">Tipo</td>");
            archivo.append("<td style=\"height: 13.5667px; width: 202px;\">Nombre</td>\n");
            archivo.append("<td style=\"height: 13.5667px; width: 452px;\">Descripcion</td>\n");
            archivo.append("</tr>\n");
            archivo.append("</thead>\n");
            archivo.append("<tbody>\n");

            for (Elemento i : eventosDeInicio) {
                archivo.append("<tr style=\"height: 21px;\">\n");
                archivo.append("<td style=\"height: 21px; width: 78px;\"><strong>" + i.getTipo() + "</strong></td>\n");
                archivo.append("<td style=\"height: 21px; width: 202px;\">" + i.getName() + "</td>\n");
                archivo.append("<td style=\"height: 21px; width: 452px;\">" + i.getDescripcion() + "</td>\n");
                archivo.append("</tr>\n");
            }

            archivo.append("</tbody>\n");
            archivo.append("</table>\n");
        }
    }
    
    private static void crearTablaEventos(BufferedWriter archivo) throws IOException{
        if (!actividades.isEmpty()) {
            archivo.append("<h2 style=\"color: #000000;\">EVENTOS:</h2>\n");
            archivo.append("<table class=\"table\" style=\"width: 717px;\" border = \"2\" cellpadding = \"2\" cellspacing = \"2\">\n");
            archivo.append("<thead>\n");
            archivo.append("<tr style=\"height: 13.5667px;\">\n");
            archivo.append("<td style=\"height: 13.5667px; width: 78px;\">Tipo</td>");
            archivo.append("<td style=\"height: 13.5667px; width: 202px;\">Nombre</td>\n");
            archivo.append("<td style=\"height: 13.5667px; width: 452px;\">Descripcion</td>\n");
            archivo.append("</tr>\n");
            archivo.append("</thead>\n");
            archivo.append("<tbody>\n");

            for (Elemento i : eventos) {
                archivo.append("<tr style=\"height: 21px;\">\n");
                archivo.append("<td style=\"height: 21px; width: 78px;\"><strong>" + i.getTipo() + "</strong></td>\n");
                archivo.append("<td style=\"height: 21px; width: 202px;\">" + i.getName() + "</td>\n");
                archivo.append("<td style=\"height: 21px; width: 452px;\">" + i.getDescripcion() + "</td>\n");
                archivo.append("</tr>\n");
            }

            archivo.append("</tbody>\n");
            archivo.append("</table>\n");
        }
    }
    
    private static void crearTablaEventosDeFin(BufferedWriter archivo) throws IOException{
        if (!actividades.isEmpty()) {
            archivo.append("<h2 style=\"color: #000000;\">EVENTOS DE FINALIZACION:</h2>\n");
            archivo.append("<table class=\"table\" style=\"width: 717px;\" border = \"2\" cellpadding = \"2\" cellspacing = \"2\">\n");
            archivo.append("<thead>\n");
            archivo.append("<tr style=\"height: 13.5667px;\">\n");
            archivo.append("<td style=\"height: 13.5667px; width: 78px;\">Tipo</td>");
            archivo.append("<td style=\"height: 13.5667px; width: 202px;\">Nombre</td>\n");
            archivo.append("<td style=\"height: 13.5667px; width: 452px;\">Descripcion</td>\n");
            archivo.append("</tr>\n");
            archivo.append("</thead>\n");
            archivo.append("<tbody>\n");

            for (Elemento i : eventosDeSalida) {
                archivo.append("<tr style=\"height: 21px;\">\n");
                archivo.append("<td style=\"height: 21px; width: 78px;\"><strong>" + i.getTipo() + "</strong></td>\n");
                archivo.append("<td style=\"height: 21px; width: 202px;\">" + i.getName() + "</td>\n");
                archivo.append("<td style=\"height: 21px; width: 452px;\">" + i.getDescripcion() + "</td>\n");
                archivo.append("</tr>\n");
            }

            archivo.append("</tbody>\n");
            archivo.append("</table>\n");
        }
    }
    
    private static void crearTablaLineasDeNado(BufferedWriter archivo) throws IOException{
        if (!actividades.isEmpty()) {
            archivo.append("<h2 style=\"color: #000000;\">LINEAS DE NADO:</h2>\n");
            archivo.append("<table class=\"table\" style=\"width: 717px;\" border = \"2\" cellpadding = \"2\" cellspacing = \"2\">\n");
            archivo.append("<thead>\n");
            archivo.append("<tr style=\"height: 13.5667px;\">\n");
            archivo.append("<td style=\"height: 13.5667px; width: 78px;\">Tipo</td>");
            archivo.append("<td style=\"height: 13.5667px; width: 202px;\">Nombre</td>\n");
            archivo.append("<td style=\"height: 13.5667px; width: 452px;\">Descripcion</td>\n");
            archivo.append("</tr>\n");
            archivo.append("</thead>\n");
            archivo.append("<tbody>\n");

            for (Elemento i : lineasDeNado) {
                archivo.append("<tr style=\"height: 21px;\">\n");
                archivo.append("<td style=\"height: 21px; width: 78px;\"><strong>" + i.getTipo() + "</strong></td>\n");
                archivo.append("<td style=\"height: 21px; width: 202px;\">" + i.getName() + "</td>\n");
                archivo.append("<td style=\"height: 21px; width: 452px;\">" + i.getDescripcion() + "</td>\n");
                archivo.append("</tr>\n");
            }

            archivo.append("</tbody>\n");
            archivo.append("</table>\n");
        }
    }
    
    private static void crearTablaFlujos(BufferedWriter archivo) throws IOException{
        if (!actividades.isEmpty()) {
            archivo.append("<h2 style=\"color: #000000;\">FLUJOS:</h2>\n");
            archivo.append("<table class=\"table\" style=\"width: 717px;\" border = \"2\" cellpadding = \"2\" cellspacing = \"2\">\n");
            archivo.append("<thead>\n");
            archivo.append("<tr style=\"height: 13.5667px;\">\n");
            archivo.append("<td style=\"height: 13.5667px; width: 78px;\">Tipo</td>");
            archivo.append("<td style=\"height: 13.5667px; width: 202px;\">Nombre</td>\n");
            archivo.append("<td style=\"height: 13.5667px; width: 452px;\">Descripcion</td>\n");
            archivo.append("<td style=\"height: 13.5667px; width: 452px;\">Origen</td>\n");
            archivo.append("<td style=\"height: 13.5667px; width: 452px;\">Objetivo</td>\n");
            archivo.append("</tr>\n");
            archivo.append("</thead>\n");
            archivo.append("<tbody>\n");

            for (Flujo i : flujos) {
                archivo.append("<tr style=\"height: 21px;\">\n");
                archivo.append("<td style=\"height: 21px; width: 78px;\"><strong>" + i.getTipo() + "</strong></td>\n");
                archivo.append("<td style=\"height: 21px; width: 202px;\">" + i.getName() + "</td>\n");
                archivo.append("<td style=\"height: 21px; width: 452px;\">" + i.getDescripcion() + "</td>\n");
                archivo.append("<td style=\"height: 21px; width: 452px;\">" + ids.get(i.getOrigen())+ "</td>\n");
                archivo.append("<td style=\"height: 21px; width: 452px;\">" + ids.get(i.getDestino())+ "</td>\n");
                archivo.append("</tr>\n");
            }

            archivo.append("</tbody>\n");
            archivo.append("</table>\n");
        }
    }
}
