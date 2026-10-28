package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
    private boolean jaLeu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        db = FirebaseFirestore.getInstance();

        barcodeScannerView = findViewById(R.id.scannerContainer);
        ImageButton btnClose = findViewById(R.id.btnClose);

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        // Leitura do QR Code
        barcodeScannerView.decodeSingle(result -> {
            if (result != null && !jaLeu) {
                jaLeu = true;

                // Interrompe imediatamente a captura para evitar congelamento
                runOnUiThread(() -> {
                    try {
                        barcodeScannerView.pause();
                    } catch (Exception ignored) {}
                });

                String qrJson = result.getText();

                try {
                    JSONObject jsonObject = new JSONObject(qrJson);
                    String userId = jsonObject.optString("userId", "");
                    String nome = jsonObject.optString("nome", "Desconhecido");
                    String empresa = jsonObject.optString("empresa", "Sem Empresa");
                    String telefone = jsonObject.optString("telefone", "");

                    String chaveBusca = userId.isEmpty() ? nome : userId;

                    // Consulta no Firestore
                    db.collection("access_logs")
                            .whereEqualTo("userId", chaveBusca)
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                String novoStatus = "ENTRADA";

                                if (!queryDocumentSnapshots.isEmpty()) {
                                    String ultimoStatus = queryDocumentSnapshots.getDocuments().get(0).getString("status");
                                    if ("ENTRADA".equalsIgnoreCase(ultimoStatus)) {
                                        novoStatus = "SAÍDA";
                                    }
                                }
                                salvarEEncerrar(chaveBusca, nome, empresa, telefone, novoStatus);
                            })
                            .addOnFailureListener(e -> {
                                salvarEEncerrar(chaveBusca, nome, empresa, telefone, "ENTRADA");
                            });

                } catch (JSONException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "⚠️ QR Code inválido!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }
        });
    }

    private void salvarEEncerrar(String userId, String nome, String empresa, String telefone, String status) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("userId", userId);
        logData.put("nome", nome);
        logData.put("empresa", empresa);
        logData.put("telefone", telefone);
        logData.put("status", status);
        logData.put("timestamp", System.currentTimeMillis());

        db.collection("access_logs")
                .add(logData)
                .addOnSuccessListener(documentReference -> {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "✅ " + status + " REGISTRADA: " + nome, Toast.LENGTH_LONG).show();
                        finish();
                    });
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "❌ Erro ao salvar acesso.", Toast.LENGTH_SHORT).show();
                        finish();
                    });
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
        super.onSaveInstanceState(outState); // <- Linha corrigida aqui
        capture.onSaveInstanceState(outState);
    }
}