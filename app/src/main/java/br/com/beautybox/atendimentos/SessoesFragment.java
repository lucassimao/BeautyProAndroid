package br.com.beautybox.atendimentos;

import android.support.v4.app.Fragment;

/**
 * Created by lsimaocosta on 22/06/16.
 */

public class SessoesFragment extends Fragment implements  AtendimentoTabListener {

    /*
    private Atendimento atendimento;
    private SessoesAdapter mAdapter;

    public SessoesFragment() {
    }

    public static SessoesFragment newInstance(Atendimento atendimento) {
        SessoesFragment fragment = new SessoesFragment();
        fragment.atendimento = atendimento;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sessoes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        mAdapter = new SessoesAdapter(atendimento.getSessoes());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                TextView txtQtdeSessoes = (TextView) getView().findViewById(R.id.txt_qtde_sessoes);
                int itemCount = mAdapter.getItemCount();
                String str = getResources().getQuantityString(R.plurals.sessoes_str, itemCount, itemCount);

                txtQtdeSessoes.setText(str);
            }
        });

        ImageButton btnAddSessao = (ImageButton) view.findViewById(R.id.btn_add_sessao);
        btnAddSessao.setOnClickListener(onClickAddSessao());

    }

    private View.OnClickListener onClickAddSessao() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtViewData = (TextView) getView().findViewById(R.id.txt_view_data);
                TextView txtViewHorario = (TextView) getView().findViewById(R.id.txt_view_horario);

                String string = txtViewData.getText() + " " + txtViewHorario.getText();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

                try {
                    Date dt = sdf.parse(string);
                    mAdapter.addSessao(dt.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),"Informe a data e a hora da sessão",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

*/
    @Override
    public void writeChanges() {
//        atendimento.setSessoes(mAdapter.getSessoes());
    }

    @Override
    public boolean validate() {
/*        if (mAdapter.getSessoes().size() == 0){
            Toast.makeText(getContext(),"Informe as sessoes do atendimento",Toast.LENGTH_SHORT).show();
            return false;
        }*/
        return true;
    }

}
