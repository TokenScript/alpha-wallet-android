package com.wallet.crypto.alphawallet.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wallet.crypto.alphawallet.R;
import com.wallet.crypto.alphawallet.entity.Ticket;
import com.wallet.crypto.alphawallet.entity.Token;
import com.wallet.crypto.alphawallet.entity.TokenInfo;
import com.wallet.crypto.alphawallet.ui.widget.adapter.TicketAdapter;
import com.wallet.crypto.alphawallet.ui.widget.entity.TicketRange;
import com.wallet.crypto.alphawallet.viewmodel.AssetDisplayViewModel;
import com.wallet.crypto.alphawallet.viewmodel.AssetDisplayViewModelFactory;
import com.wallet.crypto.alphawallet.widget.ProgressView;
import com.wallet.crypto.alphawallet.widget.SystemView;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.wallet.crypto.alphawallet.C.Key.TICKET;

/**
 * Created by James on 22/01/2018.
 */

/**
 *
 */
public class AssetDisplayActivity extends BaseActivity implements View.OnClickListener
{
    @Inject
    protected AssetDisplayViewModelFactory assetDisplayViewModelFactory;
    private AssetDisplayViewModel viewModel;
    private SystemView systemView;
    private ProgressView progressView;

    public TextView name;
    private Ticket ticket;
    private TicketAdapter adapter;
    private String balance = null;
    private String burnList = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_use_token);
        toolbar();

        ticket = getIntent().getParcelableExtra(TICKET);
        setTitle(getString(R.string.title_show_tickets));
        TokenInfo info = ticket.tokenInfo;

        systemView = findViewById(R.id.system_view);
        systemView.hide();
        progressView = findViewById(R.id.progress_view);
        progressView.hide();

        name = findViewById(R.id.textViewName);
        RecyclerView list = findViewById(R.id.listTickets);

        RelativeLayout rLL = findViewById(R.id.contract_address_layout);
        rLL.setVisibility(View.GONE);

        adapter = new TicketAdapter(this::onTicketIdClick, ticket);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        list.setHapticFeedbackEnabled(true);
        list.setClipToPadding(false);
        list.addItemDecoration(itemDecorator);

        String useName = String.valueOf(ticket.balanceArray.size()) + " " + info.name;

        name.setText(useName);

        viewModel = ViewModelProviders.of(this, assetDisplayViewModelFactory)
                .get(AssetDisplayViewModel.class);

        viewModel.queueProgress().observe(this, progressView::updateProgress);
        viewModel.pushToast().observe(this, this::displayToast);
        viewModel.ticket().observe(this, this::onTokenUpdate);

        findViewById(R.id.button_use).setOnClickListener(this);
        findViewById(R.id.button_sell).setOnClickListener(this);
        findViewById(R.id.button_transfer).setOnClickListener(this);
        findViewById(R.id.copy_address).setOnClickListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        viewModel.prepare(ticket);
    }

    private void onTokenUpdate(Token t)
    {
        ticket = (Ticket)t;
        if (!ticket.getBurnListStr().equals(burnList) || !ticket.getFullBalance().equals(balance))
        {
            adapter.setTicket(ticket);
            RecyclerView list = findViewById(R.id.listTickets);
            list.setAdapter(null);
            list.setAdapter(adapter);
            balance = ticket.getFullBalance();
            burnList = ticket.getBurnListStr();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_use:
            {
                viewModel.selectAssetIdsToRedeem(this, ticket);
            }
            break;
            case R.id.button_sell:
            {
                viewModel.sellTicketRouter(this, ticket);// showSalesOrder(this, ticket);
            }
            break;
            case R.id.button_transfer:
            {
                viewModel.showTransferToken(this, ticket);
            }
            break;
            case R.id.copy_address:
            {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getResources().getString(R.string.copy_addr_to_clipboard), ticket.getAddress());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, R.string.copy_addr_to_clipboard, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onTicketIdClick(View view, TicketRange range) {
        Context context = view.getContext();
        //viewModel.showSalesOrder(this, ticket, range);
        viewModel.showTransferToken(this, ticket, range);
    }
}
