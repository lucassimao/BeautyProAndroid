package br.com.beautybox.caixa;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import br.com.beautybox.R;
import br.com.beautybox.dao.Caixa;
import br.com.beautybox.domain.MovimentoCaixa;

public class CaixaListFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_caixa,container,false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(onClickAddMovimento());

        DatabaseReference query = Caixa.getCurrent();
        setListAdapter(new FirebaseListAdapter<MovimentoCaixa>(getActivity(),MovimentoCaixa.class,R.layout.list_item_movimento_caixa,query) {
            @Override
            protected void populateView(View v, MovimentoCaixa model, int position) {

            }

            @Override
            protected MovimentoCaixa parseSnapshot(DataSnapshot snapshot) {
                return super.parseSnapshot(snapshot);
            }
        });

        return view;
    }

    private View.OnClickListener onClickAddMovimento() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }
}