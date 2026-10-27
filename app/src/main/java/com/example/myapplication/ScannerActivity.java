package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class ScannerActivity extends AppCompatActivity {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        barcodeScannerView = findViewById(R.id.scannerContainer);
        ImageButton btnClose = findViewById(R.id.btnClose);

        btnClose.setOnClickListener(v -> finish());

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);

        // Inicia a leitura contínua do QR Code
        capture.decode();

        // Configura o evento de leitura bem-sucedida
        barcodeScannerView.decodeSingle(result -> {
            if (result != null) {
                String conteudoLido = result.getText();
                // Mostra o resultado lido do QR Code (contém o JSON com o nome, empresa e ID)
                Toast.makeText(this, "Acesso validado: " + conteudoLido, Toast.LENGTH_LONG).show();

                // Podes fechar o scanner após ler com sucesso
                finish();
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