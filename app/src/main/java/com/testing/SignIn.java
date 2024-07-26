package com.testing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    Button btnSignIn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnSignIn = findViewById(R.id.btnSignIn);
        auth = FirebaseAuth.getInstance();

        // Cập nhật URL của Firebase Database
        database = FirebaseDatabase.getInstance("https://doan3-e4046-default-rtdb.asia-southeast1.firebasedatabase.app");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        if (auth.getCurrentUser() != null){
            Intent intent = new Intent(SignIn.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void signIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    Log.d(TAG, "Google sign in successful, ID Token: " + account.getIdToken());
                    firebaseAuth(account.getIdToken());
                } else {
                    Log.e(TAG, "Google Sign-In account is null");
                }
            } catch (ApiException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuth(String idToken) {
        if (idToken == null) {
            Log.e(TAG, "idToken is null");
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        HashMap<String, Object> map = new HashMap<>();
                        if (user.getDisplayName() != null) {
                            map.put("name", user.getDisplayName());
                        } else {
                            map.put("name", "Unknown");
                        }
                        map.put("email", user.getEmail());
                        if (user.getPhotoUrl() != null) {
                            map.put("profile", user.getPhotoUrl().toString());
                        } else {
                            map.put("profile", "None");
                        }

                        database.getReference().child("GoogleAuth").child(user.getUid())
                                .setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SignIn.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Đăng nhập thành công");

                                        Intent intent = new Intent(SignIn.this, UserActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(e -> {
                                    Log.e(TAG, "Cập nhật database thất bại", e);
                                });
                    } else {
                        Log.e(TAG, "FirebaseUser is null after sign in");
                    }
                } else {
                    Toast.makeText(SignIn.this, "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "signInWithCredential:failure", task.getException());
                }
            }
        });
    }
}
