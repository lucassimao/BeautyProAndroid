package br.com.beautybox.servicos;

import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.beautybox.R;
import br.com.beautybox.domain.Servico;


public class ServicosListFragment extends ListFragment {

    private static final String TAG = ServicosListFragment.class.getSimpleName();
    View viewSelecionado;

    FirebaseListAdapter<Servico> mAdapter;
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
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        final DatabaseReference ref = instance.getReference(Servico.FIREBASE_NODE+"LLL");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // seta o listadapter assim que os dados estiverem prontos, ocultando a barra de progresso
                if (dataSnapshot.exists()){
                    mAdapter = new ServicosAdapter(getActivity(), ref);
                    setListAdapter(mAdapter);
                }else{
                    setListAdapter(null);
                    Toast.makeText(getActivity(),"Lista de serviços vazia",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        ListView listView = getListView();
        listView.setOnItemLongClickListener(onItemLongClickListener());

        callback = new ServicosActionBarCallback(this);
    }

    private AdapterView.OnItemLongClickListener onItemLongClickListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                ServicosListFragment servicosListFragment = ServicosListFragment.this;

                if (actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                }

//                Servico servico = mAdapter.getItem(position);
                servicosListFragment.currentSelectedItem = position;

                // deixando o item da lista com uma sombra para destacar
                float[] hsv = new float[3];
                int color = ((ColorDrawable) view.getBackground()).getColor();
                Color.colorToHSV(color, hsv);
                hsv[2] *= 0.8f; // value component
                view.setBackgroundColor(Color.HSVToColor(hsv));

                servicosListFragment.viewSelecionado = view;

                Toolbar toolBar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                actionMode = toolBar.startActionMode(servicosListFragment.callback);

                return true;
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter!=null)
            mAdapter.cleanup();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_servicos, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_new:
                ServicoFragment servicoFragment = ServicoFragment.newInstance();
                getActivity().getSupportFragmentManager().
                        beginTransaction().replace(R.id.fragment_container, servicoFragment, null).addToBackStack(null).commit();
                break;
            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }


}
