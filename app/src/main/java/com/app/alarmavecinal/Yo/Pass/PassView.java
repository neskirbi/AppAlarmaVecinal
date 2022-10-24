package com.app.alarmavecinal.Yo.Pass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.app.alarmavecinal.Metodos;
import com.app.alarmavecinal.R;
import com.google.android.material.textfield.TextInputEditText;

public class PassView extends AppCompatActivity implements Pass.PassView {

    PassPresenter passPresenter;
    Context context;
    Metodos metodos;
    TextInputEditText pass,pass1,pass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_view);
        context=this;
        metodos=new Metodos(context);
        passPresenter=new PassPresenter(this,context);
        pass=findViewById(R.id.pass);
        pass1=findViewById(R.id.pass1);
        pass2=findViewById(R.id.pass2);
    }

    public void Actualizar(View view){
        passPresenter.UpdatePass(pass.getText().toString(),pass1.getText().toString(),pass2.getText().toString());
    }

    @Override
    public void Salir() {
        finish();
    }
}