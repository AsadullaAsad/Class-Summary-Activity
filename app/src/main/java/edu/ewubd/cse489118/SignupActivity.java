package edu.ewubd.cse489118;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPhone, etUserId, etPW, etRPW;
    private CheckBox cbRemUserId, cbRemLogin;
    private Button btnLogin, btnGo, btnExit;

    private static final String SHARED_PREF_NAME = "login_info";
    private static final String MY_INFO_PREF = "my_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decideNavigation();
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etUserId = findViewById(R.id.etUserId);
        etPW = findViewById(R.id.etPW);
        etRPW = findViewById(R.id.etRPW);

        cbRemUserId = findViewById(R.id.cbRemUserId);
        cbRemLogin = findViewById(R.id.cbRemLogin);

        btnLogin = findViewById(R.id.btnLogin);
        btnGo = findViewById(R.id.btnGo);
        btnExit = findViewById(R.id.btnExit);

        btnExit.setOnClickListener(v -> finish());

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnGo.setOnClickListener(v -> processSignup());
    }

    private void processSignup() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String userId = etUserId.getText().toString().trim();
        String pass = etPW.getText().toString().trim();
        String rpass = etRPW.getText().toString().trim();

        String errorMsg = validateInputs(name, email, pass, rpass);

        if (!errorMsg.isEmpty()) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            return;
        }

        saveUserCredentials(name, userId, email, phone, pass);

        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }

    private String validateInputs(String name, String email, String pass, String rpass) {
        StringBuilder errorMsg = new StringBuilder();
        if (name.length() < 4 || name.length() > 10) {
            errorMsg.append("Invalid name\n");
        }
        if (pass.length() != 4 || !pass.equals(rpass)) {
            errorMsg.append("Invalid password\n");
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMsg.append("Invalid email\n");
        }
        return errorMsg.toString();
    }

    private void saveUserCredentials(String name, String userId, String email, String phone, String pass) {
        SharedPreferences sp = getSharedPreferences(MY_INFO_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("USER_NAME", name);
        editor.putString("USER_ID", userId);
        editor.putString("USER_EMAIL", email);
        editor.putString("USER_PHONE", phone);
        editor.putString("USER_PASS", pass);
        editor.putBoolean("REM_LOGIN", cbRemLogin.isChecked());
        editor.putBoolean("REM_REMUSERID", cbRemUserId.isChecked());
        editor.apply();
    }

    private void decideNavigation() {
        SharedPreferences sp = getSharedPreferences(MY_INFO_PREF, MODE_PRIVATE);
        String userId = sp.getString("USER_ID", "DEFAULT");
        if (userId != "DEFAULT") {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}
