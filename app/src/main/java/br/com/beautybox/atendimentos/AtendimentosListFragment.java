package br.com.beautybox.atendimentos;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.com.beautybox.R;
import br.com.beautybox.domain.Atendimento;
import br.com.beautybox.service.AtendimentoService;

/**
 * Created by lsimaocosta on 17/06/16.
 */
public class AtendimentosListFragment extends Fragment {

    private static final String TAG = AtendimentosListFragment.class.getSimpleName();

    private AtendimentosAdapter mAdapter;
    private ActionMode actionMode = null;
    private AtendimentosActionBarCallback callback;
    Atendimento currentSelectedItem;
    View viewSelecionado;

    public static AtendimentosListFragment newInstance() {
        AtendimentosListFragment fragment = new AtendimentosListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_atendimentos, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton floatingActionButton = (FloatingActionButton) getView().findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(onClickFab());

        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        String currentBucket = AtendimentoService.getCurrentBucket();
        final Query query = instance.getReference(Atendimento.FIREBASE_NODE).orderByKey().startAt(currentBucket);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                View view = getView();
                ProgressBar progressDialog = (ProgressBar) view.findViewById(R.id.progress_bar);
                progressDialog.setVisibility(View.INVISIBLE);

                if (dataSnapshot.exists()) {
                    mAdapter = new AtendimentosAdapter(getContext());
                    ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.expandable_list_view);
                    expandableListView.setVisibility(View.VISIBLE);
                    expandableListView.setAdapter(mAdapter);
                } else {
                    Toast.makeText(getActivity(), "Lista de atendimentos vazia", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.logcat(Log.DEBUG,TAG,"Erro ao carregar atendimentos");
                FirebaseCrash.report(databaseError.toException());
            }
        });

        ExpandableListView expandableListView = (ExpandableListView) getView().findViewById(R.id.expandable_list_view);
        expandableListView.setOnItemLongClickListener(onItemLongClickListener());
        callback = new AtendimentosActionBarCallback(this);
    }

    private View.OnClickListener onClickFab() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AtendimentoFragment atendimentoFragment = AtendimentoFragment.newInstance(null);
                getActivity().getSupportFragmentManager().
                        beginTransaction().replace(R.id.fragment_container, atendimentoFragment, null).addToBackStack(null).commit();
            }
        };
    }

    private AdapterView.OnItemLongClickListener onItemLongClickListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    int childPosition = ExpandableListView.getPackedPositionChild(id);

                    AtendimentosListFragment fragment = AtendimentosListFragment.this;

                    if (actionMode != null) {
                        actionMode.finish();
                        actionMode = null;
                    }

                    Atendimento atendimento = (Atendimento) mAdapter.getChild(groupPosition, childPosition);
                    fragment.currentSelectedItem = atendimento;

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

                return false;
            }
        };
    }

    @Override
    public void onPause() {
        super.onDestroy();
        if (mAdapter != null)
            mAdapter.unregisterListeners();
    }
}
