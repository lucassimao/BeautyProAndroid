package br.com.beautybox.movimentoCaixa;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.com.beautybox.R;
import br.com.beautybox.dao.MovimentoCaixaDAO;

public class MovimentoCaixaListFragment extends ListFragment {

    View viewSelecionado;
    int currentSelectedItem = -1;
    private ActionMode actionMode = null;
    private MovimentoCaixaActionBarCallback callback;
    MovimentoCaixaAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_list_movimentos_caixa, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(onClickAddMovimento());

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Query query = MovimentoCaixaDAO.list();
        query.addListenerForSingleValueEvent(singleValueEventListener());

        this.adapter = new MovimentoCaixaAdapter(getActivity(), query);
        setListAdapter(adapter);
        getListView().setOnItemLongClickListener(onItemLongClickListener());

        callback = new MovimentoCaixaActionBarCallback(this);
    }

    private AdapterView.OnItemLongClickListener onItemLongClickListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                MovimentoCaixaListFragment fragment = MovimentoCaixaListFragment.this;

                if (actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                }

                fragment.currentSelectedItem = position;

                // deixando o item da lista com uma sombra para destacar
                float[] hsv = new float[3];
                int color = Color.WHITE;
                Color.colorToHSV(color, hsv);
                hsv[2] *= 0.8f; // value component
                view.setBackgroundColor(Color.HSVToColor(hsv));

                fragment.viewSelecionado = view;

                Toolbar toolBar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                actionMode =  toolBar.startActionMode(fragment.callback);

                return true;
            }
        };

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