package com.example.projetoddesc;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.projetoddesc.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();


        setContentView(view);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("724197064327-6hvomug4bmun1lee436hbasmmd235nlo.apps.googleusercontent.com").requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);


        binding.botaoEntrar.setOnClickListener(view12 -> {
            if (TextUtils.isEmpty(binding.editTextUsuario.getText())){
                binding.editTextUsuario.setError("O Usuário não pode estar em branco");
                Toast.makeText(getApplicationContext(), "Por favor, preencha o nome de usuário", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(binding.editTextSenha.getText())){
                binding.editTextSenha.setError("A Senha não pode estar em branco");
                Toast.makeText(getApplicationContext(), "Por favor, preencha a senha", Toast.LENGTH_LONG).show();
            } else {
                loginUsuarioESenha(binding.editTextUsuario.getText().toString(),binding.editTextSenha.getText().toString());
            }
        });

        binding.botaoGoogle.setOnClickListener(view1 -> {
            sigIn();

        });
    }

    private void sigIn(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 1);
    }

    private void loginComGoogle(String token){
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
           if(task.isSuccessful()){
               Toast.makeText(getApplicationContext(), "Login com Google efetuado com sucesso", Toast.LENGTH_LONG).show();
               abrePrincipal();
           }else{
               Toast.makeText(getApplicationContext(), "Erro ao efetuar login com Google", Toast.LENGTH_LONG).show();

           }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == 1){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                GoogleSignInAccount conta = task.getResult(ApiException.class);
                loginComGoogle(conta.getIdToken());
            }catch (ApiException exception){
                Toast.makeText(getApplicationContext(), "Nenhum usuário Google está logado no aparelho", Toast.LENGTH_LONG).show();
                Log.d("Erro:", exception.toString());
            }
        }
    }




    private void loginUsuarioESenha(String usuario, String senha) {
        mAuth.signInWithEmailAndPassword(usuario, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCustomToken:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Login efetuado com sucesso", Toast.LENGTH_LONG).show();
                            abrePrincipal();

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Usuário e/ou senha incorretos", Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void abrePrincipal(){
        binding.editTextUsuario.setText("");
        binding.editTextSenha.setText("");
        Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        try {
            Toast.makeText(getApplicationContext(), "Usuário " + currentUser.getEmail() + " já está logado", Toast.LENGTH_LONG).show();
            abrePrincipal();

        }catch (Exception e){

        }
        //updateUI(currentUser);
    }
}

