package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class OperatorActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView listLogs;
    private TextView txtUltimoNome, txtUltimoDetalhe, txtUltimaHora, txtUltimoStatus;
    private List<String> logsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator);

        db = FirebaseFirestore.getInstance();

        listLogs = findViewById(R.id.listLogs);
        txtUltimoNome = findViewById(R.id.txtUltimoNome);
        txtUltimoDetalhe = findViewById(R.id.txtUltimoDetalhe);
        txtUltimaHora = findViewById(R.id.txtUltimaHora);
        txtUltimoStatus = findViewById(R.id.txtUltimoStatus);
        Button btnEscanear = findViewById(R.id.btnEscanear);

        logsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, logsList);
        listLogs.setAdapter(adapter);

        carregarLogsDoFirebase();

        btnEscanear.setOnClickListener(v -> {
            Intent intent = new Intent(OperatorActivity.this, ScannerActivity.class);
            startActivity(intent);
        });
    }

    private void carregarLogsDoFirebase() {
        db.collection("access_logs")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    logsList.clear();
                    boolean primeiraVez = true;

                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot doc : value) {
                            String nome = doc.getString("nome");
                            String empresa = doc.getString("empresa");
                            String status = doc.getString("status");

                            String linha = nome + " - " + empresa + " (" + status + ")";
                            logsList.add(linha);

                            if (primeiraVez) {
                                txtUltimoNome.setText(nome != null ? nome : "Desconhecido");
                                txtUltimoDetalhe.setText(empresa != null ? empresa : "");
                                txtUltimaHora.setText("Agora");
                                txtUltimoStatus.setText("→ " + (status != null ? status.toUpperCase() : "REGISTRADO"));
                                primeiraVez = false;
                            }
                        }
                    } else {
                        txtUltimoNome.setText("Nenhuma leitura recente");
                        txtUltimoDetalhe.setText("Aguardando scanner...");
                        txtUltimaHora.setText("--");
                        txtUltimoStatus.setText("AGUARDANDO ACESSO");
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}