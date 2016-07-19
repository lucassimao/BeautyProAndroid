package br.com.beautybox.servicos;

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

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.com.beautybox.R;
import br.com.beautybox.dao.ServicoDAO;
import br.com.beautybox.domain.Servico;


public class ServicosListFragment extends ListFragment {

    private static final String TAG = ServicosListFragment.class.getSimpleName();

    FirebaseListAdapter<Servico> mAdapter;
    View viewSelecionado;
    int currentSelectedItem = -1;
    private ActionMode actionMode = null;
    private ServicosActionBarCallback callback;

    public static ServicosListFragment newInstance() {
        ServicosListFragment fragment = new ServicosListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_servicos, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(onClickFab());

        final Query query = ServicoDAO.list();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                View view = getView();

                if (view != null) {
                    mAdapter = new ServicosAdapter(getActivity(), query);
                    setListAdapter(mAdapter);

                    ProgressBar progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
                    progressBar.setVisibility(View.INVISIBLE);

                    if (!dataSnapshot.exists()) {
                        Toast.makeText(getActivity(), "Lista de serviços vazia", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.report(databaseError.toException());
            }
        });

        getListView().setOnItemLongClickListener(onItemLongClickListener());

        callback = new ServicosActionBarCallback(this);
    }

    private View.OnClickListener onClickFab() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServicoFragment servicoFragment = ServicoFragment.newInstance();
                getActivity().getSupportFragmentManager().
                        beginTransaction().replace(R.id.fragment_container, servicoFragment, null).addToBackStack(null).commit();
            }
        };
    }

    private AdapterView.OnItemLongClickListener onItemLongClickListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                ServicosListFragment fragment = ServicosListFragment.this;

                if (actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                }

//                Servico servico = mAdapter.getItem(position);
                fragment.currentSelectedItem = position;

                // deixando o item da lista com uma sombra para destacar
                float[] hsv = new float[3];
                int color = Color.WHITE;
                Color.colorToHSV(color, hsv);
                hsv[2] *= 0.8f; // value component
                view.setBackgroundColor(Color.HSVToColor(hsv));

                fragment.viewSelecionado = view;

                Toolbar toolBar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                actionMode = toolBar.startActionMode(fragment.callback);

                return true;
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null)
            mAdapter.cleanup();
    }

}
