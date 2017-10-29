package application.android.irwinet.apiettravel;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Assign Action Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize Font Family
        Typeface myTypeFaceThree=Typeface.createFromAsset(getAssets(),getString(R.string.pathPrimary));

        //Initialize Controls
        TextView tvLogin = (TextView) findViewById(R.id.tvLogin);
        Button mRegisterInButton = (Button) findViewById(R.id.email_register_in_button);

        //Assign Font Family
        tvLogin.setTypeface(myTypeFaceThree);
        mRegisterInButton.setTypeface(myTypeFaceThree);

        //Event Login
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewLogin(null);
            }
        });

        //Event Action Bar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewLogin(null);
            }
        });
    }

    public void viewLogin(View v)
    {
        Intent intentLogin=new Intent(this,LoginActivity.class);
        startActivity(intentLogin);
    }
}
