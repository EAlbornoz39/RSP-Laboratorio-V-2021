package com.example.rsplaboratoriov2021;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.util.Log.d;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = super.getSupportActionBar();
        actionBar.setTitle("RSP Laboratorio V 2021");
        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        String jsonContactos = preferences.getString("json", null);

        if (jsonContactos==null)
        {
            TextView json = super.findViewById(R.id.jContactos);
            json.setText("Todavía no hay contactos guardados.");
        }

        else
        {
            TextView json = super.findViewById(R.id.jContactos);
            json.setText(jsonContactos);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.buscar);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.agregarPersona:
                d("Click", "Se hizo click en Agregar Persona");


                DialogGenerico dialog = new DialogGenerico(this);
                dialog.show(getSupportFragmentManager(), "Datos:");
                break;
            case R.id.buscar:
                d("Click", "Se hizo click en Buscar Persona");

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {


        String lista = this.ObtenerSharedPreferences();

        boolean encontrado=false;

        List<Persona> auxList = new ArrayList<Persona>();

        for(Persona auxPersona: ObtenerTodas(lista))
        {
            if(auxPersona.getNombre().contains(query))
            {
                DialogNotificarError dialog= new DialogNotificarError(this, auxPersona.getNombre(),auxPersona.getTelefono(),true,false,true);
                dialog.show(getSupportFragmentManager(),"dialog");
                  Toast toast = Toast.makeText(this, "LA PERSONA SE ENCUENTRA AQUI", Toast.LENGTH_SHORT);
                toast.show();
                encontrado=true;
                break;
            }
        }
        if(!encontrado)
        {
            DialogNotificarError dialog= new DialogNotificarError(this,"No encontrada","La persona que busco no esta dentro de la lista",false,true,false);
            dialog.show(getSupportFragmentManager(),"dialog");
        }

        return false;

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }



    public String ObtenerSharedPreferences()
    {
        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        String jsonContactos = preferences.getString("json", null);

        return jsonContactos;
    }


    public void GuardarSharedPreferences(String valor)
    {
        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("json", valor);
        editor.commit();
    }



    public void RefrescarLista()
    {
        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        String jsonContactos = preferences.getString("json", null);

        TextView json = super.findViewById(R.id.jContactos);
        json.setText(jsonContactos);

    }


    public List<Persona> ObtenerTodas(String json) {
        List<Persona> personas = new ArrayList<>();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(json);
            for (int i=0;i<jsonArray.length();i++)
            {
                Persona persona = new Persona();
                JSONObject obj=jsonArray.getJSONObject(i);
                persona.setNombre(obj.getString("nombre"));
                persona.setTelefono(obj.getString("telefono"));
                personas.add(persona);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return personas;
    }
}
