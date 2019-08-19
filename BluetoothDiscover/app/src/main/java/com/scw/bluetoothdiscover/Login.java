package com.scw.bluetoothdiscover;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Login extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private final AppCompatActivity activity = Login.this;


    private static Handler handler_data;
    private static final int SUCCESS_MSG = 1;
    private static final int FAILURE_MSG = 0;

    private Button loginButton;
    private Button createButton;
    private Button enterButton;
    private Button checkButton; // test

    private EditText nameInput;
    private EditText passwordInput;
    private EditText ipInput;
    private TextView nameCheck;
    private TextView passwordCheck;

    private InputMethodManager imm;

    private HttpRequest httpRequest;

    private boolean nameCheckResult = false;
    private boolean passwordCheckResult = false;

    private User user;

    // Check sensor
    private BluetoothAdapter mBluetoothAdapter;
    private LocationManager mLocationManager;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_ENABLE_GPS = 1;

    String checkResult = null;
    String username = null;
    String password = null;
    String loginResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        checkPhone();
        initViews();
        initObjects();

        handler_data = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1://SUCCESS_MSG
                        switch (checkResult) {
                            case "nothing":
                                System.out.println("Test fail to connect");
                                break;
                            case "null":
                                System.out.println("Test id or password");
                                break;
                            case "good":
                                System.out.println("good");
                                Intent intent = new Intent(Login.this, MapBoxActivity.class);
                                intent.putExtra("username", username);
                                intent.putExtra("login", true);
                                startActivity(intent);
                                break;

                        }
                        break;
                    case 0://FAILURE_MSG
                        System.out.println("Test fail message");
                        //Utils.toast(getApplicationContext(), "fail to connect the server");
                        break;
                }
            }
        };


    }


    /**
     * This method is to initialize views
     */
    private void initViews() {

        loginButton = (Button) findViewById(R.id.login);
        createButton = (Button) findViewById(R.id.create);
        enterButton = (Button) findViewById(R.id.enter);
        checkButton = (Button) findViewById(R.id.check);

        nameInput = (EditText) findViewById(R.id.nameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        ipInput = (EditText) findViewById(R.id.ipInput);

        nameCheck = (TextView) findViewById(R.id.nameCheck);
        passwordCheck = (TextView) findViewById(R.id.passwordCheck);

        loginButton.setOnClickListener(this);
        createButton.setOnClickListener(this);
        enterButton.setOnClickListener(this);
        checkButton.setOnClickListener(this);

        nameInput.setOnFocusChangeListener(this);
        passwordInput.setOnFocusChangeListener(this);

        loginButton.setEnabled(false);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {

        user = new User();

    }

    private void checkPhone() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Utils.toast(getApplicationContext(), "Bluetooth not supported");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gps) {
            Intent enableIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(enableIntent, REQUEST_ENABLE_GPS);
        }
        //boolean network = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Utils.toast(getApplicationContext(), "Bluetooth is on");
                } else {
                    Utils.toast(getApplicationContext(), "Bluetooth error");
                }
                break;
            case REQUEST_ENABLE_GPS:
                if (resultCode == Activity.RESULT_OK) {
                    Utils.toast(getApplicationContext(), "GPS is on");
                } else {
                    Utils.toast(getApplicationContext(), "GPS error");
                }
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (Login.this.getCurrentFocus() != null) {
                if (Login.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(Login.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    checkInput();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                //HttpRequest
                httpRequest = new HttpRequest(this, ipInput.getText().toString());
                login();
                break;
            case R.id.create:
                // Navigate to Register Activity
                Intent intentRegister = new Intent(getApplicationContext(), Register.class);
                startActivity(intentRegister);
                //finish();
                break;
            case R.id.enter:


                // Navigate to Map Activity
                Intent intentEnter = new Intent(getApplicationContext(), MapBoxActivity.class);
                // Get ip address from ipInput
                String ipAddress = ipInput.getText().toString();
                intentEnter.putExtra("ipAddress", ipAddress);
                startActivity(intentEnter);

                break;
            case R.id.check:
                Intent intentCheck = new Intent(getApplicationContext(), Check.class);
                startActivity(intentCheck);
                break;
            default:
                break;
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.nameInput:
                if (hasFocus) {

                } else {
                    checkInput();
                }
                break;
            case R.id.passwordInput:
                if (hasFocus) {

                } else {
                    checkInput();
                }
                break;
        }

    }


    private void login() {
        user.setName(nameInput.getText().toString().trim());
        user.setPassword(passwordInput.getText().toString());


        Thread loginThread = new Thread(new Runnable() {
            @Override
            public void run() {
                httpRequest.doPostLogin(user.getName(), user.getPassword());
            }
        });
        loginThread.start();
        try {
            loginThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Test login " + httpRequest.serverResponse);
        if (httpRequest.serverResponse.equals("good")) {
            Intent intentStart = new Intent(getApplicationContext(), MapBoxActivity.class);
            // Get ip address from ipInput
            String ipAddress = ipInput.getText().toString();
            intentStart.putExtra("username", user.getName());
            intentStart.putExtra("ipAddress", ipAddress);
            startActivity(intentStart);
        }


    }

    private void checkInput() {
        if (nameInput.getText().toString().trim().equals("")) {
            nameCheckResult = false;
            nameCheck.setText("Input Name");
        } else {
            nameCheckResult = true;
            nameCheck.setText("");
        }
        if (passwordInput.getText().toString().equals("")) {
            passwordCheckResult = false;
            passwordCheck.setText("Input Name");
        } else {
            passwordCheckResult = true;
            passwordCheck.setText("");
        }
        if (nameCheckResult && passwordCheckResult) {
            loginButton.setEnabled(true);
        } else {
            loginButton.setEnabled(false);
        }
    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void verifyInfo() {

        // trim >> remove space
        username = nameInput.getText().toString().trim();
        password = passwordInput.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkResult = httpRequest.doPostLogin(username, password);
                if (checkResult.isEmpty()) {
                    handler_data.obtainMessage(FAILURE_MSG, checkResult).sendToTarget();

                } else {
                    handler_data.obtainMessage(SUCCESS_MSG, checkResult).sendToTarget();
                }
            }
        }).start();

    }


}
