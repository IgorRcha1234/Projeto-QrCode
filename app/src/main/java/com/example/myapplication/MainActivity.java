package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editNome, editCpf, editEmpresa, editTelefone;
    private Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editNome = findViewById(R.id.editNome);
        editCpf = findViewById(R.id.editCpf);
        editEmpresa = findViewById(R.id.editEmpresa);
        editTelefone = findViewById(R.id.editTelefone);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        // Máscara automática para o CPF (000.000.000-00)
        editCpf.addTextChangedListener(new MaskTextWatcher(editCpf, "###.###.###-##"));

        // Máscara automática para o Telefone ((00) 00000-0000)
        editTelefone.addTextChangedListener(new MaskTextWatcher(editTelefone, "(##) #####-####"));

        btnCadastrar.setOnClickListener(v -> {
            String nome = editNome.getText().toString().trim();
            String cpf = editCpf.getText().toString().trim();
            String empresa = editEmpresa.getText().toString().trim();
            String telefone = editTelefone.getText().toString().trim();

            if (nome.isEmpty() || cpf.isEmpty() || empresa.isEmpty() || telefone.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Abre a QrCodeActivity e passa os dados
            Intent intent = new Intent(MainActivity.this, QrCodeActivity.class);
            intent.putExtra("userId", cpf);
            intent.putExtra("nome", nome);
            intent.putExtra("empresa", empresa);
            startActivity(intent);
        });
    }

    private static class MaskTextWatcher implements TextWatcher {
        private final EditText editText;
        private final String mask;
        private boolean isUpdating;

        public MaskTextWatcher(EditText editText, String mask) {
            this.editText = editText;
            this.mask = mask;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isUpdating) {
                isUpdating = false;
                return;
            }

            String str = s.toString().replaceAll("[^\\d]", "");
            StringBuilder formatted = new StringBuilder();
            int i = 0;

            for (char m : mask.toCharArray()) {
                if (m == '#') {
                    if (i < str.length()) {
                        formatted.append(str.charAt(i));
                        i++;
                    } else {
                        break;
                    }
                } else {
                    if (i < str.length()) {
                        formatted.append(m);
                    } else {
                        break;
                    }
                }
            }

            isUpdating = true;
            editText.setText(formatted.toString());
            editText.setSelection(formatted.length());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}