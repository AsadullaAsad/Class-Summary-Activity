package edu.ewubd.cse489118;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etUserId, etPW;
    private Button btnSignup, btnGo, btnExit;
    private CheckBox cbRemUserId, cbRemPass;

    // SharedPreferences file name
    private static final String SHARED_PREF_NAME = "login_info";

    // SharedPreferences keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER_USER_ID = "remember_user_id";
    private static final String KEY_REMEMBER_PASSWORD = "remember_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUserId = findViewById(R.id.etUserId);
        etPW = findViewById(R.id.etPW);
        btnSignup = findViewById(R.id.Signup);
        btnGo = findViewById(R.id.btnGo);
        btnExit = findViewById(R.id.btnExit);
        cbRemUserId = findViewById(R.id.cbRemUserId);
        cbRemPass = findViewById(R.id.cbRemPass);

        loadRememberedUserId();

        // Check if "Remember User ID" and "Remember Password" checkboxes were previously checked
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        boolean rememberUserId = sharedPreferences.getBoolean(KEY_REMEMBER_USER_ID, false);
        boolean rememberPassword = sharedPreferences.getBoolean(KEY_REMEMBER_PASSWORD, false);
        if (rememberUserId) {
            String savedUserId = sharedPreferences.getString(KEY_USER_ID, "");
            etUserId.setText(savedUserId);
            cbRemUserId.setChecked(true);
        }
        if (rememberPassword) {
            String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
            etPW.setText(savedPassword);
            cbRemPass.setChecked(true);
        }

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("my_info", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLogin();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadRememberedUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("login_info", MODE_PRIVATE);
        String rememberedUserId = sharedPreferences.getString("remembered_user_id", "");
        etUserId.setText(rememberedUserId);
        cbRemUserId.setChecked(!rememberedUserId.isEmpty()); // Check the checkbox if UserId is remembered
    }

    private void processLogin() {
        String userId = etUserId.getText().toString().trim();
        String password = etPW.getText().toString().trim();

        if (isValidCredentials(userId, password)) {
            // Save user ID and password if "Remember User ID" and/or "Remember Password" checkboxes are checked
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (cbRemUserId.isChecked()) {
                editor.putString(KEY_USER_ID, userId);
                editor.putBoolean(KEY_REMEMBER_USER_ID, true);
            } else {
                editor.remove(KEY_USER_ID);
                editor.putBoolean(KEY_REMEMBER_USER_ID, false);
            }
            if (cbRemPass.isChecked()) {
                editor.putString(KEY_PASSWORD, password);
                editor.putBoolean(KEY_REMEMBER_PASSWORD, true);
            } else {
                editor.remove(KEY_PASSWORD);
                editor.putBoolean(KEY_REMEMBER_PASSWORD, false);
            }
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Invalid UserId or Password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidCredentials(String userId, String password) {
        // Implement the logic to validate user credentials here
        SharedPreferences sp = getSharedPreferences("my_info", MODE_PRIVATE);
        String storedUserId = sp.getString("USER_ID", "");
        String storedPassword = sp.getString("USER_PASS", "");
        return userId.equals(storedUserId) && password.equals(storedPassword);
    }
}
