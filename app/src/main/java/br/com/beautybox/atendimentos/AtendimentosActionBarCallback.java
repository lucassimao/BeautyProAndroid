package br.com.beautybox.atendimentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;

import br.com.beautybox.R;
import br.com.beautybox.YesNoDialogFragment;
import br.com.beautybox.dao.AtendimentoDAO;
import br.com.beautybox.domain.Atendimento;
import br.com.beautybox.domain.Sessao;

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

        final Sessao sessao = fragment.currentSelectedItem;
        final Atendimento atendimento = sessao.getAtendimento();

        switch (item.getItemId()) {
            case R.id.action_edit:

                mode.finish();
                Intent intent = new Intent(ctx,AtendimentoActivity.class);
                intent.putExtra (AtendimentoActivity.ATENDIMENTO_OBJ,atendimento);
                ctx.startActivity(intent);
                break;
            case R.id.action_delete:


                DialogInterface.OnClickListener onRemoverAtendimentoListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Context ctx = fragment.getContext();
                        DatabaseReference ref = AtendimentoDAO.getRef(atendimento);

                        AtendimentoDAO.delete(ref).addOnCompleteListener(onCompleteTaskListener(mode, false));
                        progressDialog = ProgressDialog.show(ctx, "Aguarde", "Excluindo atendimento ...", true, false);
                    }
                };

                DialogInterface.OnClickListener onRemoverSessaoListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Context ctx = fragment.getContext();

                        atendimento.getSessoes().remove(sessao);
                        AtendimentoDAO.save(atendimento).addOnCompleteListener(onCompleteTaskListener(mode,true));

                        progressDialog = ProgressDialog.show(ctx, "Aguarde", "Excluindo sessão ...", true, false);
                    }
                };

                FragmentManager fragmentManager = ctx.getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment prev = fragmentManager.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                if (atendimento.getSessoes().size() == 1)
                    YesNoDialogFragment.newInstance(onRemoverAtendimentoListener).show(fragmentManager, "dialog");
                else
                    DeleteAtendimentoConfirmationDialogFragment.newInstance(onRemoverAtendimentoListener,
                            onRemoverSessaoListener).show(fragmentManager, "dialog");

                break;
            default:
                return false;
        }
        return true;
    }

    private OnCompleteListener<Void> onCompleteTaskListener(final ActionMode mode, final boolean isUpdating) {
        return new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                mode.finish();
                Context ctx = fragment.getActivity();
                String msg = isUpdating?"Sessão removida com sucesso" : "Atendimento removido com sucesso";

                if (task.isSuccessful()) {
                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, "Erro :" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    FirebaseCrash.report(task.getException());
                }
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
