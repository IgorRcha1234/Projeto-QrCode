package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AcessoAdapter extends RecyclerView.Adapter<AcessoAdapter.ViewHolder> {

    private final List<RegistroAcesso> lista;

    public AcessoAdapter(List<RegistroAcesso> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_acesso, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistroAcesso item = lista.get(position);

        if (holder.txtNome != null) holder.txtNome.setText(item.getNome());
        if (holder.txtEmpresa != null) holder.txtEmpresa.setText(item.getCargoEmpresa());
        if (holder.txtHora != null) holder.txtHora.setText(item.getHora());
        if (holder.txtStatus != null) holder.txtStatus.setText(item.getTipo()); // Usa getTipo()

        if (holder.txtInitials != null) {
            if (item.getNome() != null && item.getNome().length() >= 2) {
                holder.txtInitials.setText(item.getNome().substring(0, 2).toUpperCase());
            } else {
                holder.txtInitials.setText("--");
            }
        }

        // Clique no Card para abrir detalhes
        holder.itemView.setOnClickListener(v -> exibirDialogDetalhes(v.getContext(), item));
    }

    private void exibirDialogDetalhes(Context context, RegistroAcesso item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Detalhes do Acesso");

        String cpfTxt = (item.getCpfMasked() != null && !item.getCpfMasked().isEmpty()) ? item.getCpfMasked() : "Não informado";

        String mensagem = "• Nome: " + item.getNome() + "\n" +
                "• CPF/Doc: " + cpfTxt + "\n" +
                "• Empresa / Cargo: " + item.getCargoEmpresa() + "\n" +
                "• Status do Acesso: " + item.getTipo() + "\n" +
                "• Horário do Registro: " + item.getHora();

        builder.setMessage(mensagem);
        builder.setPositiveButton("Voltar para a Portaria", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtEmpresa, txtHora, txtStatus, txtInitials;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeItem);
            txtEmpresa = itemView.findViewById(R.id.txtEmpresaItem);
            txtHora = itemView.findViewById(R.id.txtHoraItem);
            txtStatus = itemView.findViewById(R.id.txtStatusItem);
            txtInitials = itemView.findViewById(R.id.txtInitials);
        }
    }
}