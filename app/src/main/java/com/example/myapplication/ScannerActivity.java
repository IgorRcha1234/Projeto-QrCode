package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ScannerActivity extends AppCompatActivity {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        db = FirebaseFirestore.getInstance();

        barcodeScannerView = findViewById(R.id.scannerContainer);
        ImageButton btnClose = findViewById(R.id.btnClose);

        btnClose.setOnClickListener(v -> finish());

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        // Quando o leitor detetar o QR Code com sucesso:
        barcodeScannerView.decodeSingle(result -> {
            if (result != null) {
                String qrJson = result.getText();

                try {
                    // Decodifica o JSON gerado no crachá
                    JSONObject jsonObject = new JSONObject(qrJson);
                    String userId = jsonObject.getString("userId");
                    String nome = jsonObject.getString("nome");
                    String empresa = jsonObject.getString("empresa");
                    String telefone = jsonObject.optString("telefone", "");

                    // Prepara os dados para enviar ao Firebase Firestore
                    Map<String, Object> logData = new HashMap<>();
                    logData.put("userId", userId);
                    logData.put("nome", nome);
                    logData.put("empresa", empresa);
                    logData.put("telefone", telefone);
                    logData.put("status", "Liberado");
                    logData.put("timestamp", Timestamp.now());

                    // Grava na coleção "access_logs" (o que faz a OperatorActivity atualizar-se sozinha)
                    db.collection("access_logs")
                            .add(logData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(this, "Acesso Liberado: " + nome, Toast.LENGTH_LONG).show();
                                finish(); // Fecha o scanner e volta para o painel do operador
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Erro ao registar o acesso no servidor.", Toast.LENGTH_SHORT).show();
                            });

                } catch (JSONException e) {
                    Toast.makeText(this, "QR Code inválido ou não reconhecido!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }
}