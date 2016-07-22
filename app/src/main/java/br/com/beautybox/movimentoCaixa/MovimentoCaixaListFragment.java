package br.com.beautybox.movimentoCaixa;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.com.beautybox.R;
import br.com.beautybox.dao.MovimentoCaixaDAO;
import br.com.beautybox.servicos.ServicoFragment;

public class MovimentoCaixaListFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_list_movimentos_caixa, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(onClickAddMovimento());

        Query query = MovimentoCaixaDAO.list();
        query.addListenerForSingleValueEvent(singleValueEventListener());

        setListAdapter(new MovimentoCaixaAdapter(getActivity(),query));

        return view;
    }

    private ValueEventListener singleValueEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                View view = getView();

                if (view != null) {
                    ProgressBar progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
                    progressBar.setVisibility(View.INVISIBLE);

                    if (!dataSnapshot.hasChildren())
                        Toast.makeText(getActivity(), "Não há movimentos no caixa atual", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.report(databaseError.toException());
            }
        };
    }

    private View.OnClickListener onClickAddMovimento() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovimentoCaixaFragment movimentoCaixaFragment = MovimentoCaixaFragment.newInstance(null);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, movimentoCaixaFragment, null)
                        .addToBackStack(null).commit();
            }
        };
    }
}