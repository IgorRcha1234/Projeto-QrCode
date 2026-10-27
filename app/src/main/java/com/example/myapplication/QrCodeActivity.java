package com.example.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        ImageView imgQrCode = findViewById(R.id.imgQrCode);
        TextView txtNome = findViewById(R.id.txtNome);
        TextView txtEmpresa = findViewById(R.id.txtEmpresa);
        Button btnSalvar = findViewById(R.id.btnSalvar);
        Button btnCompartilhar = findViewById(R.id.btnCompartilhar);

        // Recebe os dados passados pela RegisterActivity
        String userId = getIntent().getStringExtra("userId");
        String nome = getIntent().getStringExtra("nome");
        String empresa = getIntent().getStringExtra("empresa");

        // Define os textos na tela
        if (nome != null) txtNome.setText(nome);
        if (empresa != null) txtEmpresa.setText(empresa);

        // Conteúdo formatado em JSON para codificar dentro do QR Code
        String dadosQr = "{\"userId\":\"" + userId + "\",\"nome\":\"" + nome + "\",\"empresa\":\"" + empresa + "\"}";

        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(dadosQr, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(bitMatrix);
            imgQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        btnSalvar.setOnClickListener(v ->
                Toast.makeText(this, "Crachá pronto para salvar na galeria!", Toast.LENGTH_SHORT).show()
        );

        btnCompartilhar.setOnClickListener(v ->
                Toast.makeText(this, "A abrir opções de partilha...", Toast.LENGTH_SHORT).show()
        );
    }
}