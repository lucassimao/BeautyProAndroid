package br.com.beautybox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by lsimaocosta on 24/06/16.
 */
public class YesNoDialogFragment extends DialogFragment {

    private DialogInterface.OnClickListener onPositiveButtonClickListener;

    public static YesNoDialogFragment newInstance(DialogInterface.OnClickListener onPositiveButtonClickListener){
        YesNoDialogFragment dialogFragment = new YesNoDialogFragment();
        dialogFragment.onPositiveButtonClickListener = onPositiveButtonClickListener;
        return dialogFragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmação")
                .setMessage("Deseja realmente remover esse registro ?")
                .setPositiveButton("Sim", onPositiveButtonClickListener)
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

}
