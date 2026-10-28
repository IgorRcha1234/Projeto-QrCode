package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OperatorActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView listLogs;
    private TextView txtUltimoNome, txtUltimoDetalhe, txtUltimaHora, txtUltimoStatus;

    private AcessoAdapter adapter;
    private List<RegistroAcesso> listaAcessos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator);

        db = FirebaseFirestore.getInstance();

        // Mapeamento das views do layout
        listLogs = findViewById(R.id.listLogs);
        txtUltimoNome = findViewById(R.id.txtUltimoNome);
        txtUltimoDetalhe = findViewById(R.id.txtUltimoDetalhe);
        txtUltimaHora = findViewById(R.id.txtUltimaHora);
        txtUltimoStatus = findViewById(R.id.txtUltimoStatus);
        Button btnEscanear = findViewById(R.id.btnEscanear);

        // Configuração do RecyclerView e Adapter
        listaAcessos = new ArrayList<>();
        listLogs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AcessoAdapter(listaAcessos);
        listLogs.setAdapter(adapter);

        // Ouve atualizações do Firestore em tempo real
        carregarLogsDoFirebase();

        // Botão para abrir a tela de Scanner
        if (btnEscanear != null) {
            btnEscanear.setOnClickListener(v -> {
                Intent intent = new Intent(OperatorActivity.this, ScannerActivity.class);
                startActivity(intent);
            });
        }
    }

    private void carregarLogsDoFirebase() {
        db.collection("access_logs")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Erro ao buscar logs: " + error.getMessage());
                        return;
                    }

                    if (value == null) return;

                    listaAcessos.clear();
                    boolean primeiroItem = true;

                    for (DocumentSnapshot doc : value) {
                        try {
                            String id = doc.getId();
                            String nome = doc.getString("nome");
                            String empresa = doc.getString("empresa");
                            String status = doc.getString("status");

                            // Trata timestamp de forma segura (caso venha como Long ou String)
                            Long timestamp = 0L;
                            if (doc.get("timestamp") instanceof Long) {
                                timestamp = doc.getLong("timestamp");
                            } else if (doc.get("timestamp") instanceof Number) {
                                timestamp = ((Number) doc.get("timestamp")).longValue();
                            }

                            // Formata a hora
                            String horaFormatada = "--:--";
                            if (timestamp != null && timestamp > 0) {
                                horaFormatada = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timestamp));
                            }

                            // Cria o objeto modelo sem quebrar a app
                            RegistroAcesso registro = new RegistroAcesso(
                                    id,
                                    nome != null ? nome : "Desconhecido",
                                    empresa != null ? empresa : "Sem Empresa",
                                    "",
                                    status != null ? status : "ENTRADA",
                                    horaFormatada,
                                    timestamp != null ? timestamp : 0
                            );

                            listaAcessos.add(registro);

                            // Atualiza o Card Superior com segurança
                            if (primeiroItem) {
                                if (txtUltimoNome != null) txtUltimoNome.setText(registro.getNome());
                                if (txtUltimoDetalhe != null) txtUltimoDetalhe.setText(registro.getCargoEmpresa());
                                if (txtUltimaHora != null) txtUltimaHora.setText(horaFormatada);

                                String statusTxt = status != null ? status.toUpperCase() : "REGISTRADO";
                                if (txtUltimoStatus != null) {
                                    if ("ENTRADA".equalsIgnoreCase(statusTxt)) {
                                        txtUltimoStatus.setText("⇒ ENTRADA REGISTRADA");
                                    } else {
                                        txtUltimoStatus.setText("⇐ SAÍDA REGISTRADA");
                                    }
                                }

                                primeiroItem = false;
                            }
                        } catch (Exception e) {
                            Log.e("ParseError", "Erro ao processar documento individual: " + e.getMessage());
                        }
                    }

                    if (listaAcessos.isEmpty()) {
                        if (txtUltimoNome != null) txtUltimoNome.setText("Nenhuma leitura recente");
                        if (txtUltimoDetalhe != null) txtUltimoDetalhe.setText("Aguardando scanner...");
                        if (txtUltimaHora != null) txtUltimaHora.setText("--:--");
                        if (txtUltimoStatus != null) txtUltimoStatus.setText("AGUARDANDO ACESSO");
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}