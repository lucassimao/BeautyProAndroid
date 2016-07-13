package br.com.beautybox.atendimentos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by lsimaocosta on 24/06/16.
 */
public class DeleteAtendimentoConfirmationDialogFragment extends DialogFragment {

    private DialogInterface.OnClickListener onRemoverSessaoListener;
    private DialogInterface.OnClickListener onRemoverAtendimentoListener;

    public static DeleteAtendimentoConfirmationDialogFragment newInstance(DialogInterface.OnClickListener onRemoverAtendimentoListener, DialogInterface.OnClickListener onRemoverSessaoListener){
        DeleteAtendimentoConfirmationDialogFragment dialogFragment = new DeleteAtendimentoConfirmationDialogFragment();
        dialogFragment.onRemoverAtendimentoListener = onRemoverAtendimentoListener;
        dialogFragment.onRemoverSessaoListener = onRemoverSessaoListener;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmação")
                .setMessage("Deseja remover apenas a sessão selecionada ou o atendimento completo?")
                .setNeutralButton("Atendimento",onRemoverAtendimentoListener)
                .setPositiveButton("Sessão", onRemoverSessaoListener)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

}
