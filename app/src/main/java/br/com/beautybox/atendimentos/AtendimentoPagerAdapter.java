package br.com.beautybox.atendimentos;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.beautybox.domain.Atendimento;

/**
 * Created by lsimaocosta on 05/07/16.
 */
public class AtendimentoPagerAdapter extends FragmentPagerAdapter {

    private final String[] tabTitles = {"Atendimento", "Pagamento"};
    private Fragment[] fragments;
    private final Atendimento atendimento;

    public AtendimentoPagerAdapter(FragmentManager fm, Atendimento atendimento) {
        super(fm);
        this.atendimento = atendimento;
        this.fragments = new Fragment[tabTitles.length];
    }

    @Override
    public Fragment getItem(int position) {
        if (this.fragments[position] == null) {
            switch (position) {
                case 0:
                    this.fragments[position] = AtendimentoFragment.newInstance(atendimento);
                    break;
                case 1:
                    this.fragments[position] = PagamentoFragment.newInstance(atendimento);
                    break;
            }
        }

        return this.fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}