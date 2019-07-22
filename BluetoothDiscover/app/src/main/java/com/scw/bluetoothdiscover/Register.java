package com.scw.bluetoothdiscover;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Register extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = Register.this;

    private Button createButton;

    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText passwordCheck;

    private InputValidation inputValidation;
    private User user;


    private HttpRequest httpRequest;

    String checkResult = null;

    private Handler handler_data;
    private int SUCCESS_MSG = 1;
    //获取失败返回massage
    private int FAILURE_MSG = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        initViews();
        initObjects();

    }

    /**
     * This method is to initialize views
     */
    private void initViews() {
        nameInput = (EditText) findViewById(R.id.nameInput);
        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        passwordCheck = (EditText) findViewById(R.id.passwordCheck);
        createButton = (Button) findViewById(R.id.create);

        createButton.setOnClickListener(this);

    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        inputValidation = new InputValidation(activity);
        //HttpRequest
        httpRequest = new HttpRequest(this);
        user = new User();

    }


    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.create:
                postDataToSQLite();
                break;

        }
    }

    /**
     * This method is to validate the input text fields and post data to SQLite
     */
    @SuppressLint("HandlerLeak")
    private void postDataToSQLite() {
        /*
        if (!inputValidation.isInputEditTextFilled(textInputEditTextName, textInputLayoutName, getString(R.string.error_message_name))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return;
        }
        if (!inputValidation.isInputEditTextMatches(textInputEditTextPassword, textInputEditTextConfirmPassword,
                textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
            return;
        }

        if (!databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim())) {

            user.setName(textInputEditTextName.getText().toString().trim());
            user.setEmail(textInputEditTextEmail.getText().toString().trim());
            user.setPassword(textInputEditTextPassword.getText().toString().trim());

            databaseHelper.addUser(user);

            // Snack Bar to show success message that record saved successfully
            Snackbar.make(nestedScrollView, getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
            emptyInputEditText();


        } else {
            // Snack Bar to show error message that record already exists
            Snackbar.make(nestedScrollView, getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show();
        }
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkResult = httpRequest.doPostRegister(nameInput.getText().toString().trim(), passwordInput.getText().toString(), emailInput.getText().toString());
                if (checkResult.isEmpty()) {
                    handler_data.obtainMessage(FAILURE_MSG, checkResult).sendToTarget();

                } else {
                    handler_data.obtainMessage(SUCCESS_MSG, checkResult).sendToTarget();
                }
            }
        }).start();
        handler_data = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        switch (checkResult) {
                            case "nothing":
                                System.out.println("fail to connect");
                                break;
                            case "exist":
                                System.out.println("exist");
                                break;
                            case "good":
                                System.out.println("good");
                                finish();
                                break;

                        }
                        break;
                    case 0:
                        Utils.toast(getApplicationContext(), "fail to connect the server");
                        break;
                }
            }
        };


    }

}


