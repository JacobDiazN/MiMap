package com.example.mymap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mymap.DataBase.AdminSQLiteOpenHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.util.Log;
import android.widget.Toast;

//Esto declara la clase MainActivity, que extiende de AppCompatActivity
// e implementa las interfaces OnMapReadyCallback, GoogleMap.OnMapClickListener y GoogleMap.OnMapLongClickListener.
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

  //Acá se declaran las variables de instancias para varios elementos de la interfaz de usuario
    private EditText txtLat, txtLon;
    private GoogleMap googleMap;
    private AdminSQLiteOpenHelper admin;
    private SQLiteDatabase BaseDeDatos;
    private Button buttonGuardar;
    private Button buttonEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Acá se inicializa el objeto AdminSQLiteOpenHelper, que es un helper para la gestión de la base de datos SQLite.
        admin = new AdminSQLiteOpenHelper(getApplicationContext(), "datosGoogleMap", null, 1);
        //Acá se Obtiene una instancia de la base de datos en modo escritura.
        BaseDeDatos = admin.getWritableDatabase();

        //Acá se crea la relación entre la parte lógica y la parte gráfica
        txtLat = (EditText)findViewById(R.id.txtLat);
        txtLon = (EditText)findViewById(R.id.txtLon);
        buttonGuardar = findViewById(R.id.button_Guardar);
        buttonEliminar = findViewById(R.id.button_Eliminar);

        //Acá se obtiene una referencia al fragmento del mapa y llama a getMapAsync(this) para iniciar la carga asincrónica del mapa.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Acá se define un listener para el botón "Guardar".
        // Cuando se hace clic en el mapa, se obtiene la latitud y longuitud actual y el boton guarda los datos en la base de datos.
        // y al final muestra un mensaje mediante un Toast
        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (googleMap != null) {
                    LatLng latLng = googleMap.getCameraPosition().target;
                    guardarEnBaseDeDatos(latLng.latitude, latLng.longitude);
                    Toast.makeText(MainActivity.this, "Datos Guardados", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Acá se define un listener para el botón "Eliminar".
        // Este botón elimina todos los datos de la tabla en la base de datos.
        // y al final muestra un mensaje mediante un Toast
        buttonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean datosEliminados = eliminarDatosDeTabla();
                if (datosEliminados) {
                    Toast.makeText(MainActivity.this, "Datos eliminados", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No hay datos para eliminar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Este método se llama cuando el mapa de Google está listo para ser utilizado.
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMapClickListener(this);
        this.googleMap.setOnMapLongClickListener(this);

        LatLng mapa = new LatLng(-33.441689643139455, -70.76829146593809);
        this.googleMap.addMarker(new MarkerOptions().position(mapa).title(""));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(mapa));
    }

    //Este método se llama cuando el usuario hace clic en el mapa.
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        txtLat.setText("Latitud: " + latLng.latitude);
        txtLon.setText("Longitud: " + latLng.longitude);

        googleMap.clear();
        LatLng mapa = new LatLng(latLng.latitude, latLng.longitude);
        googleMap.addMarker(new MarkerOptions().position(mapa).title(""));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mapa));
    }

    //Este método se llama cuando el usuario hace un clic largo mantenido en el mapa.
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        txtLat.setText("Latitud: "+latLng.latitude);
        txtLon.setText("Longitud: "+latLng.longitude);

        googleMap.clear();
        LatLng mapa = new LatLng(latLng.latitude,latLng.longitude);
        googleMap.addMarker(new MarkerOptions().position(mapa).title(""));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mapa));
    }

    //Este método insertar las coordenadas latitud y longitud a la tabla llamada "GoogleMap" en la base de datos.
    private void guardarEnBaseDeDatos(double latitud, double longitud) {
        try {
            ContentValues values = new ContentValues();
            values.put("lat", latitud);
            values.put("lon", longitud);
            long result = BaseDeDatos.insertOrThrow("GoogleMap", null, values);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show();
        }
    }

    //este método eliminar todos los registros de la tabla "GoogleMap" de la base de datos.
    private boolean eliminarDatosDeTabla() {
        try {
            int registrosEliminados = BaseDeDatos.delete("GoogleMap", null, null);
            return registrosEliminados > 0;
        } catch (Exception e) {
            return false;
        }
    }

    //Este método se utiliza para iniciar otra activity llamada MostrarDb cuando se hace clic en el botón ListarDb
    public void Mostrar_DB(View view){
        Intent read = new Intent(this, MostrarDb.class);
        startActivity(read);
    }

    //Este método se asegura de cerrar la conexión de la base de datos cuando la actividad se destruye
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BaseDeDatos != null && BaseDeDatos.isOpen()) {
            BaseDeDatos.close();
        }
    }
}