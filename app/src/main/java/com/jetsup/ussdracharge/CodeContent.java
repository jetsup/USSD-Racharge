package com.jetsup.ussdracharge;

import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_AIRTEL_CUSTOMER_CARE;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_FAIBA_CUSTOMER_CARE;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_NAME_AIRTEL;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_NAME_EXT;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_NAME_FAIBA;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_NAME_SAFARICOM;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_NAME_TELKOM;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_SAFARICOM_CUSTOMER_CARE;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_SLOGAN_EXT;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_TELKOM_CUSTOMER_CARE;
import static com.jetsup.ussdracharge.custom.ISPConstants.SAME_CARRIER;
import static com.jetsup.ussdracharge.custom.ISPConstants.SELECT_SIM_SLOT;
import static com.jetsup.ussdracharge.custom.ISPConstants.SIM_CARD_PRESENT;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jetsup.ussdracharge.adapters.ISPAdapter;
import com.jetsup.ussdracharge.adapters.ISPContentAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CodeContent extends AppCompatActivity {
    String ispNameReceived, ispSloganReceived;
    RecyclerView ussdItemRecycler;
    ISPContentAdapter ispContentAdapter;
    Map<String, String> ispDeco = new HashMap<>();
    Map<String, String> ispCustomerCare = new HashMap<>();
    private boolean simPresent = false, sameCarrier;
    private int simSlot = 99;
    private String ispName;
    private boolean simMatched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_isp);
        ussdItemRecycler = findViewById(R.id.ussd_items_recycler);
        ussdItemRecycler.setLayoutManager(new LinearLayoutManager(this));

        ispDeco.put(ISP_NAME_SAFARICOM, "The Better Option");
        ispDeco.put(ISP_NAME_AIRTEL, "The Smartphone Network");
        ispDeco.put(ISP_NAME_TELKOM, "Moving Forward");
        ispDeco.put(ISP_NAME_FAIBA, "I am Future Proof");

        ispCustomerCare.put(ISP_NAME_SAFARICOM, ISP_SAFARICOM_CUSTOMER_CARE);
        ispCustomerCare.put(ISP_NAME_AIRTEL, ISP_AIRTEL_CUSTOMER_CARE);
        ispCustomerCare.put(ISP_NAME_TELKOM, ISP_TELKOM_CUSTOMER_CARE);
        ispCustomerCare.put(ISP_NAME_FAIBA, ISP_FAIBA_CUSTOMER_CARE);

        ispNameReceived = getIntent().getStringExtra(ISP_NAME_EXT);
        ispSloganReceived = getIntent().getStringExtra(ISP_SLOGAN_EXT);
        simPresent = getIntent().getBooleanExtra(SIM_CARD_PRESENT, false);
        sameCarrier = getIntent().getBooleanExtra(SAME_CARRIER, false);

        ispName = ispNameReceived;
        if (simPresent) {
            List<SubscriptionInfo> simInformation = ISPAdapter.simInformation;
            for (SubscriptionInfo sim : simInformation) {
                if (sim.getCarrierName().toString().equals(ispNameReceived)) {
                    simSlot = sim.getSimSlotIndex();
                    ispName = sim.getCarrierName().toString();
                    simMatched = true;
                }
            }
        }

        Map<String, Integer> actionColorSchemes = new HashMap<>();
        actionColorSchemes.put(ISP_NAME_SAFARICOM, R.color.safaricom_green);
        actionColorSchemes.put(ISP_NAME_TELKOM, R.color.telkom_blue);
        actionColorSchemes.put(ISP_NAME_AIRTEL, R.color.airtel_red);
        actionColorSchemes.put(ISP_NAME_FAIBA, R.color.faiba_green);

        Map<String, Integer> statusColorSchemes = new HashMap<>();
        statusColorSchemes.put(ISP_NAME_SAFARICOM, R.color.status_safaricom_green);
        statusColorSchemes.put(ISP_NAME_TELKOM, R.color.status_telkom_blue);
        statusColorSchemes.put(ISP_NAME_AIRTEL, R.color.status_airtel_red);
        statusColorSchemes.put(ISP_NAME_FAIBA, R.color.status_faiba_green);

        // Mod statusBar and actionbar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(Objects.requireNonNull(statusColorSchemes.get(ispNameReceived)), null));

        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setTitle(ispNameReceived);
        actionBar.setSubtitle(ispSloganReceived);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(Objects.requireNonNull(actionColorSchemes.get(ispNameReceived)), null)));


        Log.w("MyTag", "Slot: " + simSlot + " <> " + simMatched + " <> " + ispNameReceived + " <> ");
        if (simMatched) {
            ispContentAdapter = new ISPContentAdapter(CodeContent.this, ispNameReceived, simSlot, sameCarrier);
        } else {
            ispContentAdapter = new ISPContentAdapter(CodeContent.this, ispNameReceived);
        }
        ussdItemRecycler.setAdapter(ispContentAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.isp_content_menu, menu);
        if (simSlot != 99) {
            menu.getItem(1).setVisible(true);
            menu.add("STK").setOnMenuItemClickListener(item -> {
                Intent stk = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.android.stk");
                startActivity(stk); // TODO: select SIM Card automatically
                return false;
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.contentSearch) {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    ispContentAdapter.getFilter().filter(newText);
                    return false;
                }
            });
            return true;
        } else if (item.getItemId() == R.id.contentCallProvider) {
            if (simPresent) {
                Uri numberToCall = Uri.parse("tel:" + ispCustomerCare.get(ispName));
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(numberToCall);
                if (!sameCarrier) {
                    callIntent.putExtra(SELECT_SIM_SLOT, simSlot);
                }
                // TODO: Display a dialog to confirm user want to call
                startActivity(callIntent);
            }
            Toast.makeText(this, "Calling " + ispNameReceived + " :" + ispCustomerCare.get(ispName), Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.contentLocateShop) {
            startActivity(new Intent(CodeContent.this, OpenMap.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
