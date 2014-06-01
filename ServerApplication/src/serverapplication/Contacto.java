/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package serverapplication;

/**
 *
 * @author Victor
 */
import java.util.ArrayList;
import javax.xml.bind.annotation.*;

//Esta clase Contacto se usa para el manejo de archivos xml.

@XmlType( propOrder={ "nombre", "direccion_ip", "puerto" } )
public class Contacto {
    
    public Contacto (){}
    
    private String nombre;
    private String direccion_ip;
    private String puerto;

    /**
     * @return the nombre
     */
    @XmlElement
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the direccion_ip
     */
    @XmlElement
    public String getDireccion_ip() {
        return direccion_ip;
    }

    /**
     * @param direccion_ip the direccion_ip to set
     */
    public void setDireccion_ip(String direccion_ip) {
        this.direccion_ip = direccion_ip;
    }

    /**
     * @return the puerto
     */
    @XmlElement
    public String getPuerto() {
        return puerto;
    }

    /**
     * @param puerto the puerto to set
     */
    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }
    
    @XmlRootElement( name="root" )
    static public class ForSaveMultiple {

        private ArrayList<Contacto> list = new ArrayList<Contacto>();

        public ArrayList<Contacto> getList() {
            return list;
        }

        @XmlElementWrapper(name = "contactos")

        @XmlElement( name = "contacto" )
        public void setList(ArrayList<Contacto> orderDetailList) {
            this.list = orderDetailList;
        }
    }
}
