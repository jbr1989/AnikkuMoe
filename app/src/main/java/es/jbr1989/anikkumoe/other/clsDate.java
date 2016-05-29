package es.jbr1989.anikkumoe.other;

/**
 * Created by jbr1989 on 06/12/2015.
 */
public class clsDate {

    public clsDate(){}

    public String DateDiff(Long inicio, Long fin){

        Long diff = fin-inicio;
        Long aux = (long) 0;

        aux=diff/(1000*60*60*24*(long)365.25); //Años
        if(aux==1) return "hace 1 año";
        if(aux!=0) return "hace "+aux.toString() + " años";

        aux=diff/(1000*60*60*24*(long)30); //Meses
        if(aux==1) return "hace 1 mes";
        if(aux!=0) return "hace "+aux.toString() + " meses";

        aux =diff/(1000*60*60*24); //Días
        if(aux==1) return "hace 1 día";
        if(aux!=0) return "hace "+aux.toString() + " días";

        aux =diff/(1000*60*60); //Horas
        if(aux==1) return "hace 1 hora";
        if(aux!=0) return "hace "+aux.toString() + " horas";

        aux =diff/(1000*60); //Minutos
        if(aux==1) return "hace 1 minuto";
        if(aux!=0) return "hace "+aux.toString() + " minutos";

        aux =diff/1000; //Segundos
        if(aux!=0) return "hace "+aux.toString() + " segundos";

        return "Ahora";

    }

}
