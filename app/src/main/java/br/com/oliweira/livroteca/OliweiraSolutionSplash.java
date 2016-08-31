package br.com.oliweira.livroteca;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;

/**
 * Created by Rafael on 06/07/2016.
 */
public class OliweiraSolutionSplash extends AppCompatActivity{

    // Timer da splash screen
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_oliweira_solution);

        //Exibindo splash com um timer.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Esse método será executado sempre que o timer acabar
                // E inicia a activity principal
                Intent it = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(it);

                //fecha essa activity
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
