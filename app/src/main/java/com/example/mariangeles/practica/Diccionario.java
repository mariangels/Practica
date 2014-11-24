package com.example.mariangeles.practica;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Diccionario extends Activity {

    ArrayList<Palabra> palabras;
    private Adaptador adaptador;
    private ListView lt=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diccionario);

        palabras=new ArrayList<Palabra>();
        leerxml();

        lt = (ListView) findViewById(R.id.lista);
        // Para mostrar una lista de valores obtenida a partir de un array de cadenas, se puede
        //utilizar la clase ArrayAdapter.
        adaptador = new Adaptador(this, R.layout.elemento, palabras);
        lt.setAdapter(adaptador);

        //para la mierda del long click que sino no funciona
        registerForContextMenu(lt);

        lt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tostada(palabras.get(i).getNombre());
                //CLICK
            }
        });
    }

    //muestra el menu superior de los puntitos
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.principal, menu);
        return true;
    }

    //trabajamos con el menu superior
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.mnañadir){
            Intent i = new Intent(this,Editar.class);
            Bundle b=new Bundle();
            b.putInt("index", -1);//el indice del arraylist, como es uno nuevo, le pasamos un negativo
            b.putParcelableArrayList("palabras", palabras);//el arraylist
            i.putExtras(b);
            startActivityForResult(i, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    //Este método es el que muestra el menú contextual al realizar una pulsación larga sobre el elemento.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.diccionario, menu);
    }

    //Long click para utilizar el menu contextual
    public boolean onContextItemSelected(MenuItem item) {
        //Obtener el item del listView
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        //posicion selecicionada
        final int index= info.position;


        //Que hacemos con el item seleccionado:
        int id=item.getItemId();
        switch (id){
            case R.id.mnborrar:
                tostada("Borrado elemento "+palabras.get(index).getNombre());
                palabras.remove(index);
                //Notificamos el ListView los cambios
                adaptador.notifyDataSetChanged();
                xml();
                break;
            case R.id.mneditar:
                Intent i = new Intent(this,Editar.class);
                Bundle b=new Bundle();
                b.putInt("index", index);//el indice del arraylist
                b.putParcelableArrayList("palabras",palabras);//el arraylist
                i.putExtras(b);
                startActivityForResult(i, 1);
                break;
            case R.id.mnmostrar:
                alertMostrar(index);
                break;
        }
        return super.onContextItemSelected(item);
    }

    //A la vuelta del intent
    @Override
    public void onActivityResult(int requestCode,  int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            palabras=data.getParcelableArrayListExtra("palabras");
            adaptador.notifyDataSetChanged();
            //NO ME MUESTRA LOS CAMBIOS
            adaptador = new Adaptador(this, R.layout.elemento, palabras);
            lt.setAdapter(adaptador);
            //guardamos en el xml
            xml();
        }else{
            tostada("miausnasda");
        }
        adaptador.notifyDataSetChanged();
    }

    public void alertMostrar(int i){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.mostrar, null);
        Palabra pal=palabras.get(i);
        ((TextView) vista.findViewById(R.id.mNombre)).setText("Nombre: "+pal.getNombre());
        ((TextView) vista.findViewById(R.id.mIdioma)).setText("Idioma: "+pal.getIdioma());
        ((TextView) vista.findViewById(R.id.mTraduccion)).setText("Traduccion: "+pal.getTraduccion());
        ((TextView) vista.findViewById(R.id.mSignificado)).setText("Significado: "+pal.getSignificado());

        alert.setView(vista);
        alert.show();
    }

    private void tostada(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void xml(){
        try {
            //Preparamos el archivo
            FileOutputStream fosxml = new FileOutputStream(new File(getExternalFilesDir(null),"archivo.xml"));
            //Preparamos el documento XML
            XmlSerializer docxml = Xml.newSerializer();
            docxml.setOutput(fosxml, "UTF-8");
            docxml.startDocument(null, Boolean.valueOf(true));
            docxml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            crearxml(docxml);
            fosxml.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void crearxml(XmlSerializer docxml){
        try {
            //Creamos las etiquetas y cerramos el documento
            docxml.startTag(null, "palabras");
            for(int i=0; i<palabras.size();i++) {
                docxml.startTag(null, "palabra");
                docxml.startTag(null, "nombre");
                docxml.text(palabras.get(i).getNombre());
                docxml.endTag(null, "nombre");
                docxml.startTag(null, "idioma");
                docxml.text(palabras.get(i).getIdioma());
                docxml.endTag(null, "idioma");
                docxml.startTag(null, "traduccion");
                docxml.text(palabras.get(i).getTraduccion());
                docxml.endTag(null, "traduccion");
                docxml.startTag(null, "significado");
                docxml.text(palabras.get(i).getSignificado());
                docxml.endTag(null, "significado");
                docxml.endTag(null, "palabra");
            }
            docxml.endDocument();
            docxml.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void leerxml(){
        XmlPullParser lectorxml = Xml.newPullParser();
        try {
            lectorxml.setInput(new FileInputStream(new File( getExternalFilesDir(null),"archivo.xml")),"utf-8");
            int evento = lectorxml.getEventType();
            Palabra p=new Palabra();
            while (evento != XmlPullParser.END_DOCUMENT) {
                String etiqueta = lectorxml.getName();
                if (evento == XmlPullParser.START_TAG) {
                    if(etiqueta.equalsIgnoreCase("palabra")){
                        p=new Palabra();
                    }else if (etiqueta.equalsIgnoreCase("nombre")) {
                        p.setNombre(lectorxml.nextText());
                    } else if (etiqueta.equalsIgnoreCase("idioma")) {
                        p.setIdioma(lectorxml.nextText());
                    } else if (etiqueta.equalsIgnoreCase("traduccion")) {
                        p.setTraduccion(lectorxml.nextText());
                    } else if (etiqueta.equalsIgnoreCase("significado")) {
                        p.setSignificado(lectorxml.nextText());
                        palabras.add(p);
                    }
                }
                evento = lectorxml.next();
            }
            //adaptador.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}