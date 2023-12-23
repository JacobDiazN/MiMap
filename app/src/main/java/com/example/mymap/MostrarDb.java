package com.example.mymap;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mymap.DataBase.AdminSQLiteOpenHelper;

public class MostrarDb extends AppCompatActivity {

    //Acá se declaran las variables de instancias para varios elementos de la interfaz de usuario
    private TextView listarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrardb);

        //Acá se crea la relación entre la parte lógica y la parte gráfica
        listarText = findViewById(R.id.mostrarText);

        //Este método se encarga de mostrar los datos que se encuentran en la base de datos en la interfaz gráfica.
        listarDatos();
    }

    //Este método se encarga de mostrar los datos que se encuentran en la base de datos en la interfaz gráfica.
    private void listarDatos() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "datosGoogleMap", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getReadableDatabase();

        Cursor cursor = BaseDeDatos.rawQuery("SELECT * FROM GoogleMap", null);
        if (cursor.moveToFirst()) {
            StringBuilder dataBuilder = new StringBuilder();
            do {
                double lat = cursor.getDouble(0);
                double lon = cursor.getDouble(1);

                listarText.setPadding(60, 250, 60, 0);

                dataBuilder.append("\n");
                dataBuilder.append("Latitud: ").append(lat).append("\n");
                dataBuilder.append("Longitud: ").append(lon).append("\n");
                dataBuilder.append("\n------------------------------------------------------------------------\n");
            } while (cursor.moveToNext());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Ajusta el tamaño según tus necesidades
                    listarText.setTextColor(Color.WHITE);// Configura el color del texto a blanco (Color.WHITE)
                    listarText.setText(dataBuilder.toString());
                }
            });

        } else {
            listarText.setPadding(60, 350, 60, 0);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20); // Ajusta el tamaño según tus necesidades
                    listarText.setTextColor(Color.WHITE);
                    listarText.setText("No hay datos en la base de datos.");
                }
            });
        }
        cursor.close();
        BaseDeDatos.close();
    }

    //Este método nos lleva de regreso al activity principal
    public void Regresar_Mostrar(View view) {
        Intent regresar = new Intent(this, MainActivity.class);
        startActivity(regresar);
    }
}
