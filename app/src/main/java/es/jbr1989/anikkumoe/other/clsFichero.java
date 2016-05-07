package es.jbr1989.anikkumoe.other;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Jaume on 11/12/2015.
 */
public class clsFichero {

    private String estado;
    private File carpeta;

    public clsFichero(){
        estado = Environment.getExternalStorageState();
        carpeta =Environment.getExternalStorageDirectory();
    }

    public Boolean guardar_archivo(){
        try{
            File f = new File(carpeta.getAbsolutePath(), "prueba_sd.txt");
            OutputStreamWriter fout =new OutputStreamWriter( new FileOutputStream(f));

            fout.write("Texto de prueba.");
            fout.close();

            return Boolean.TRUE;

        } catch (Exception ex){
            Log.e("Fichero", "Error al escribir fichero a tarjeta SD");
            return Boolean.FALSE;
        }
    }
}
