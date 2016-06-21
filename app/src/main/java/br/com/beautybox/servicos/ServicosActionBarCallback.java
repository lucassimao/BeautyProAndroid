package br.com.beautybox.servicos;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import br.com.beautybox.R;
import br.com.beautybox.domain.Servico;
import br.com.beautybox.service.ServicoService;

/**
 * Created by lsimaocosta on 20/06/16.
 */
public class ServicosActionBarCallback implements ActionMode.Callback {

    private final ServicosListFragment servicosListFragment;
    private ProgressDialog progressDialog;

    public ServicosActionBarCallback(ServicosListFragment servicosListFragment) {
        this.servicosListFragment = servicosListFragment;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.context_app_bar_servico, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        FragmentActivity ctx = servicosListFragment.getActivity();
        FragmentManager supportFragmentManager = ctx.getSupportFragmentManager();
        ServicoService service = new ServicoService();
        FirebaseListAdapter<Servico> mAdapter = servicosListFragment.mAdapter;
        DatabaseReference ref = mAdapter.getRef(servicosListFragment.currentSelectedItem);

        switch (item.getItemId()) {
            case R.id.action_edit:
                Servico servico = mAdapter.getItem(servicosListFragment.currentSelectedItem);
                ServicoFragment servicoFragment = ServicoFragment.newInstance(servico,ref);
                mode.finish();

                supportFragmentManager.beginTransaction().
                        replace(R.id.fragment_container, servicoFragment, null).
                        addToBackStack(null).commit();
                break;
            case R.id.action_delete:
                progressDialog = ProgressDialog.show(servicosListFragment.getContext(),"Aguarde","Excluindo serviço ...",true,false);
                service.delete(ref).addOnCompleteListener(onRemoveListener(mode));
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
                Context ctx = servicosListFragment.getActivity();

                if (task.isSuccessful()) {
                    Toast.makeText(ctx, "Serviço removido com sucesso", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(ctx, "Erro :" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        servicosListFragment.currentSelectedItem = -1;
        servicosListFragment.viewSelecionado.setBackgroundColor(Color.WHITE);
        servicosListFragment.viewSelecionado = null;
    }
}
