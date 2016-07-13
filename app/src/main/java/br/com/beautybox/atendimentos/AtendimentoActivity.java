package br.com.beautybox.atendimentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import br.com.beautybox.R;
import br.com.beautybox.dao.AtendimentoDAO;
import br.com.beautybox.dao.ServicoDAO;
import br.com.beautybox.domain.Atendimento;
import br.com.beautybox.domain.Servico;

public class AtendimentoActivity extends AppCompatActivity {

    public static final String ATENDIMENTO_OBJ = "atendimento_obj";
    private final String TAG = AtendimentoActivity.class.getSimpleName();

    private Atendimento atendimento;
    private Map<String, Servico> servicoMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atendimento);
        servicoMap = new HashMap<>();

        ServicoDAO.list(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Servico servico = ServicoDAO.load(child);
                    servicoMap.put(child.getKey(), servico);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.logcat(Log.DEBUG, TAG, "Erro ao carregar serviços em " + AtendimentoActivity.class.getName());
                FirebaseCrash.report(databaseError.toException());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(ATENDIMENTO_OBJ)) {
            this.atendimento = (Atendimento) bundle.getSerializable(ATENDIMENTO_OBJ);
            toolbar.setTitle("Editar Atendimento");
        } else {
            toolbar.setTitle("Cadastrar Atendimento");
            this.atendimento = new Atendimento();
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PagerAdapter pagerAdapter = new AtendimentoPagerAdapter(getSupportFragmentManager(), atendimento);

        final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //Ocultando o teclado que eventualmente esteja aberto
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(mViewPager);

        int cor = ContextCompat.getColor(this, android.R.color.white);
        // Cor branca no texto (o fundo pink foi definido no layout)
        tabLayout.setTabTextColors(cor, cor);

        Button btnSalvar = (Button) findViewById(R.id.btn_salvar);
        btnSalvar.setOnClickListener(onClickSalvar());

        Button btnCancelar = (Button) findViewById(R.id.btn_cancelar);
        btnCancelar.setOnClickListener(onClickCancelar());

    }

    private View.OnClickListener onClickCancelar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
    }

    private View.OnClickListener onClickSalvar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
                final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
                FragmentPagerAdapter pagerAdapter = (FragmentPagerAdapter) mViewPager.getAdapter();

                int count = pagerAdapter.getCount();

                // validando os campos preenchidos
                for (int i = 0; i < count; ++i) {
                    AtendimentoTabListener listener = (AtendimentoTabListener) pagerAdapter.getItem(i);

                    if (listener.validate()) {
                        listener.writeChanges();
                    } else {
                        tabLayout.getTabAt(i).select();
                        return;
                    }
                }

                Task task = AtendimentoDAO.save(atendimento);
                final ProgressDialog dialog = ProgressDialog.show(AtendimentoActivity.this, "Aguarde", "Salvando atendimento ...", true, false);
                dialog.show();

                task.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        dialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(AtendimentoActivity.this, "Atendimento agendado !", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String text = "Erro ao agendar atendimento: " + task.getException().getMessage();
                            Toast.makeText(AtendimentoActivity.this, text, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
