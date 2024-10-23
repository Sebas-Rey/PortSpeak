package com.example.chatps.ui.chat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.chatps.databinding.ActivitySignInBinding;
import com.example.chatps.ui.MainResidenteActivity;
import com.example.chatps.ui.MainVigilanteActivity;
import com.example.chatps.ui.chat.utilities.Constants;
import com.example.chatps.ui.chat.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Check if user is already signed in
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            navigateToAppropriateActivity();
            return; // Early return to avoid further code execution
        }

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
    }

    private void signIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                        // Save user details to preferences
                        saveUserDetails(documentSnapshot);

                        // Navigate to the appropriate activity
                        navigateToAppropriateActivity();
                    } else {
                        loading(false);
                        showToast("No se ha podido acceder");
                    }
                });
    }

    private void saveUserDetails(DocumentSnapshot documentSnapshot) {
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
        preferenceManager.putString(Constants.KEY_PHONE, documentSnapshot.getString(Constants.KEY_PHONE));
        preferenceManager.putString(Constants.KEY_CEDULA, documentSnapshot.getString(Constants.KEY_CEDULA));
        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
        preferenceManager.putString(Constants.KEY_VIVIENDA, documentSnapshot.getString(Constants.KEY_VIVIENDA));
        preferenceManager.putString(Constants.KEY_PASSWORD, documentSnapshot.getString(Constants.KEY_PASSWORD));
        preferenceManager.putString(Constants.KEY_ROL, documentSnapshot.getString(Constants.KEY_ROL));
    }

    private void navigateToAppropriateActivity() {
        String role = preferenceManager.getString(Constants.KEY_ROL);
        Class<?> activityClass = role != null && role.equals("Residente") ? MainResidenteActivity.class : MainVigilanteActivity.class;

        Intent intent = new Intent(getApplicationContext(), activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails() {
        String email = binding.inputEmail.getText().toString().trim();
        String password = binding.inputPassword.getText().toString().trim();

        if (email.isEmpty()) {
            showToast("Introduce un Correo Electrónico");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Introduce un Correo Electrónico Válido");
            return false;
        } else if (password.isEmpty()) {
            showToast("Introduce una Contraseña");
            return false;
        }
        return true;
    }
}
