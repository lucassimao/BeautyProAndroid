package br.com.beautybox.atendimentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.beautybox.R;
import br.com.beautybox.YesNoDialogFragment;
import br.com.beautybox.domain.Atendimento;
import br.com.beautybox.service.AtendimentoService;

/**
 * Created by lsimaocosta on 22/06/16.
 */
public class AtendimentosActionBarCallback implements ActionMode.Callback {

    private final AtendimentosListFragment fragment;
    private ProgressDialog progressDialog;

    public AtendimentosActionBarCallback(AtendimentosListFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.context_app_bar_atendimentos, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
        FragmentActivity ctx = fragment.getActivity();
        FragmentManager fragmentManager = ctx.getSupportFragmentManager();
        Atendimento atendimento = fragment.currentSelectedItem;
        String bucket = AtendimentoService.timestamp2Bucket(atendimento.getDataHorario());

        final DatabaseReference ref = FirebaseDatabase.getInstance().
                getReference(Atendimento.FIREBASE_NODE).child(bucket).child(atendimento.getKey()).getRef();

        switch (item.getItemId()) {
            case R.id.action_edit:
                mode.finish();
                AtendimentoFragment atendimentoFragment = AtendimentoFragment.newInstance(atendimento);
                fragmentManager.beginTransaction().
                        replace(R.id.fragment_container, atendimentoFragment, null).
                        addToBackStack(null).commit();
                break;
            case R.id.action_delete:

                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment prev = fragmentManager.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                YesNoDialogFragment.newInstance(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Context ctx = fragment.getContext();

                        progressDialog = ProgressDialog.show(ctx,"Aguarde","Excluindo atendimento ...",true,false);
                        AtendimentoService.delete(ref).addOnCompleteListener(onRemoveListener(mode));
                    }
                }).show(fragmentManager,"dialog");

                break;
            default:
                return false;
        }
        return true;
    }

    private OnCompleteListener<Void> onRemoveListener(final ActionMode mode) {
        return new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                mode.finish();
                Context ctx = fragment.getActivity();

                if (task.isSuccessful()) {
                    Toast.makeText(ctx, "Atendimento removido com sucesso", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(ctx, "Erro :" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        fragment.currentSelectedItem = null;
        fragment.viewSelecionado.setBackgroundColor(Color.WHITE);
        fragment.viewSelecionado = null;
    }
}
