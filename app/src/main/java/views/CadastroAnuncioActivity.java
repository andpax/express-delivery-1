package views;

import android.app.Activity;
import android.os.Bundle;

import com.example.anderson.expressdelivery.R;

/**
 * Created by Allan-PC on 17/04/2016.
 */
public class CadastroAnuncioActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.cadastro_anuncio_activity);
    }
}
