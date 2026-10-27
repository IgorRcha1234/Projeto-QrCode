package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ScannerActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        db = FirebaseFirestore.getInstance();

        ImageButton btnClose = findViewById(R.id.btnClose);
        ToggleButton btnFlash = findViewById(R.id.btnFlash);

        btnClose.setOnClickListener(v -> finish());

        btnFlash.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Lanterna ligada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lanterna desligada", Toast.LENGTH_SHORT).show();
            }
        });

        iniciarScanner();
    }

    private void iniciarScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Aponte para o QR Code no crachá");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Leitura cancelada", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                processarQrCode(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processarQrCode(String contents) {
        try {
            JSONObject json = new JSONObject(contents);
            String userId = json.getString("userId");
            String nome = json.getString("nome");
            String empresa = json.getString("empresa");

            Map<String, Object> log = new HashMap<>();
            log.put("userId", userId);
            log.put("nome", nome);
            log.put("empresa", empresa);
            log.put("status", "Entrada Registrada");
            log.put("timestamp", FieldValue.serverTimestamp());

            db.collection("access_logs")
                    .add(log)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Acesso Autorizado: " + nome, Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erro ao registrar no Firebase.", Toast.LENGTH_SHORT).show();
                        finish();
                    });

        } catch (Exception e) {
            Toast.makeText(this, "QR Code inválido ou corrompido!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}