package com.example.mariangeles.practica;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class Editar extends Activity {

    ArrayList<Palabra> palabras;
    int i=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        Bundle b = getIntent().getExtras();
        if(b!=null) {
            palabras = b.getParcelableArrayList("palabras");
            i=b.getInt("index");//si i es -1 no hay que editar
            if(i!=-1){
                mostrarPalabra();
            }
        }else{
            tostada("nulo");
        }
    }

    public void mostrarPalabra(){
        ((EditText) findViewById(R.id.eNombre)).setText(palabras.get(i).getNombre());
        ((EditText) findViewById(R.id.eIdioma)).setText(palabras.get(i).getIdioma());
        ((EditText) findViewById(R.id.eTraduccion)).setText(palabras.get(i).getTraduccion());
        ((EditText) findViewById(R.id.eSignificado)).setText(palabras.get(i).getSignificado());
    }

    public void guardar(View view){
        String nombre=((EditText) findViewById(R.id.eNombre)).getText().toString();
        String idioma=((EditText) findViewById(R.id.eIdioma)).getText().toString();
        String traduccion=((EditText) findViewById(R.id.eTraduccion)).getText().toString();
        String significado=((EditText) findViewById(R.id.eSignificado)).getText().toString();

        Palabra p=new Palabra(nombre,idioma,traduccion,significado);
        if(comprobarExiste(p)){
            tostada("Ya existe");
        }else{
            if(i==-1){
                //si estamos añadiendo:
                palabras.add(p);
            }else {
                //si estamos editando: modificamos la palabra i de palabras
                palabras.get(i).setNombre(nombre);
                palabras.get(i).setIdioma(idioma);
                palabras.get(i).setTraduccion(traduccion);
                palabras.get(i).setSignificado(significado);
            }
            //mandar palabras al Diccionario de vuelta
            Intent data=new Intent();
            data.putParcelableArrayListExtra("palabras",palabras);
            //cerrar editar
            setResult(Activity.RESULT_OK,data);
            finish();
        }

    }

    public boolean comprobarExiste(Palabra p){
        for(int i=0; i<palabras.size(); i++){
            if(palabras.get(i).equals(p)){
                //palabra p existe ya
                return true;
            }
        }
        return false;
    }

/*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void tostada(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
