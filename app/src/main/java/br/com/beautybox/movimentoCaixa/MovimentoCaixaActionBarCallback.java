package br.com.beautybox.movimentoCaixa;

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

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;

import br.com.beautybox.R;
import br.com.beautybox.YesNoDialogFragment;
import br.com.beautybox.dao.MovimentoCaixaDAO;
import br.com.beautybox.domain.MovimentoCaixa;

/**
 * Created by lsimaocosta on 22/07/16.
 */
public class MovimentoCaixaActionBarCallback implements  android.view.ActionMode.Callback {

    private final MovimentoCaixaListFragment movimentoCaixaListFragment;
    private ProgressDialog progressDialog;

    public MovimentoCaixaActionBarCallback(MovimentoCaixaListFragment movimentoCaixaListFragment) {
        this.movimentoCaixaListFragment = movimentoCaixaListFragment;
    }

    @Override
    public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.context_app_bar_movimento_caixa, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(final android.view.ActionMode mode, MenuItem item) {
        final FragmentActivity ctx = movimentoCaixaListFragment.getActivity();
        FragmentManager fragmentManager = ctx.getSupportFragmentManager();
        FirebaseListAdapter<MovimentoCaixa> adapter = movimentoCaixaListFragment.adapter;
        final MovimentoCaixa mc = adapter.getItem(movimentoCaixaListFragment.currentSelectedItem);

        switch (item.getItemId()) {
            case R.id.action_edit:
                MovimentoCaixaFragment fragment = MovimentoCaixaFragment.newInstance(mc);
                mode.finish();

                fragmentManager.beginTransaction().
                        replace(R.id.fragment_container, fragment, null).
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

                        progressDialog = ProgressDialog.show(ctx,"Aguarde","Excluindo movimento ...",true,false);
                        MovimentoCaixaDAO.delete(mc).addOnCompleteListener(onRemoveListener(mode));
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
                Context ctx = movimentoCaixaListFragment.getContext();

                if (task.isSuccessful())
                    Toast.makeText(ctx,"Movimento excluído com sucesso",Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(ctx,
                            "Erro ao excluir movimento: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    FirebaseCrash.report(task.getException());
                }
            }
        };
    }

    @Override
    public void onDestroyActionMode(android.view.ActionMode mode) {
        movimentoCaixaListFragment.currentSelectedItem = -1;
        movimentoCaixaListFragment.viewSelecionado.setBackgroundColor(Color.WHITE);
        movimentoCaixaListFragment.viewSelecionado = null;
    }
}
