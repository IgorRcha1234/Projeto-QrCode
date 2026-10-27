package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText editNome, editCpf, editEmpresa, editTelefone;
    private Button btnCadastrar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();

        editNome = findViewById(R.id.editNome);
        editCpf = findViewById(R.id.editCpf);
        editEmpresa = findViewById(R.id.editEmpresa);
        editTelefone = findViewById(R.id.editTelefone);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(v -> salvarECriarQrCode());
    }

    private void salvarECriarQrCode() {
        String nome = editNome.getText().toString().trim();
        String cpf = editCpf.getText().toString().trim();
        String empresa = editEmpresa.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();

        if (nome.isEmpty() || cpf.isEmpty() || empresa.isEmpty() || telefone.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nome", nome);
        usuario.put("cpf", cpf);
        usuario.put("empresa", empresa);
        usuario.put("telefone", telefone);

        db.collection("users")
                .add(usuario)
                .addOnSuccessListener(documentReference -> {
                    String userId = documentReference.getId();

                    Intent intent = new Intent(RegisterActivity.this, QrCodeActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("nome", nome);
                    intent.putExtra("empresa", empresa);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Erro ao salvar dados.", Toast.LENGTH_SHORT).show());
    }
}