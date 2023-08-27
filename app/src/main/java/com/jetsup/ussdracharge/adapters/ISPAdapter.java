package com.jetsup.ussdracharge.adapters;

import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_NAME_EXT;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_SLOGAN_EXT;
import static com.jetsup.ussdracharge.custom.ISPConstants.SAME_CARRIER;
import static com.jetsup.ussdracharge.custom.ISPConstants.SIM_CARD_PRESENT;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.telephony.SubscriptionInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jetsup.ussdracharge.CodeContent;
import com.jetsup.ussdracharge.R;
import com.jetsup.ussdracharge.models.ISP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ISPAdapter extends RecyclerView.Adapter<ISPAdapter.MyISPViewHolder> {
    public static List<SubscriptionInfo> simInformation;
    Context context;
    List<ISP> serviceProviders;
    Map<String, String> simInfo;
    private Map<String, Bitmap> simIcons;
    private boolean hasSim, sameCarrier;

    public ISPAdapter(Context context, List<SubscriptionInfo> simInfoReceived, boolean sameCarrier) {
        List<String> ispNames;
        List<String> ispSlogans;
        this.context = context;
        this.sameCarrier = sameCarrier;
        ISPAdapter.simInformation = simInfoReceived;
        simInfo = new HashMap<>();
        simIcons = new HashMap<>();
        serviceProviders = new ArrayList<>();
        hasSim = true;
        if (sameCarrier) {
            for (int i = 0; i < simInformation.size(); i++) {
                simIcons.put(simInformation.get(i).getCarrierName().toString() + (i + 1),
                        simInformation.get(i).createIconBitmap(this.context));
                simInfo.put(simInformation.get(i).getCarrierName().toString() + (i + 1),
                        simInformation.get(i).getSimSlotIndex() + " " + simInformation.get(i).getIconTint());
            }
        } else {
            for (int i = 0; i < simInformation.size(); i++) {
                simIcons.put(simInformation.get(i).getCarrierName().toString(),
                        simInformation.get(i).createIconBitmap(this.context));
                simInfo.put(simInformation.get(i).getCarrierName().toString(),
                        simInformation.get(i).getSimSlotIndex() + " " + simInformation.get(i).getIconTint());
            }
        }
        ispNames = Arrays.asList(context.getResources().getStringArray(R.array.isp_names));
        ispSlogans = Arrays.asList(context.getResources().getStringArray(R.array.isp_slogans));
        for (int i = 0; i < ispNames.size(); i++) {
            serviceProviders.add(new ISP(ispNames.get(i), ispSlogans.get(i), getIspLogo(i)));
        }
    }

    public ISPAdapter(Context context) {
        List<String> ispNames;
        List<String> ispSlogans;
        this.context = context;
        serviceProviders = new ArrayList<>();
        ispNames = Arrays.asList(context.getResources().getStringArray(R.array.isp_names));
        ispSlogans = Arrays.asList(context.getResources().getStringArray(R.array.isp_slogans));
        for (int i = 0; i < ispNames.size(); i++) {
            serviceProviders.add(new ISP(ispNames.get(i), ispSlogans.get(i), getIspLogo(i)));
        }
    }

    @NonNull
    @Override
    public MyISPViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_main_activity, parent, false);
        return new MyISPViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyISPViewHolder holder, int position) {
        holder.ispLogo.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), serviceProviders.get(position).getISPLogoIcon(), null));
        holder.ispName.setText(serviceProviders.get(position).getISPName());
        holder.ispSlogan.setText(serviceProviders.get(position).getISPSlogan());
        if (hasSim) {
            holder.simLayout.setVisibility(View.VISIBLE);
            if (sameCarrier) {
                boolean firstIconSet = false;
                for (int i = 0; i < simInfo.size(); i++) {
                    if (simInfo.containsKey(serviceProviders.get(position).getISPName() + (i + 1))) {
                        holder.firstSimImgBitmap.setVisibility(View.VISIBLE);
                        holder.secondSimImgBitmap.setVisibility(View.VISIBLE);
                        if (!firstIconSet) {
                            holder.firstSimImgBitmap.setImageBitmap(simIcons.get(serviceProviders.get(position).getISPName() + (i + 1)));
                            firstIconSet = true;
                        } else {
                            holder.secondSimImgBitmap.setImageBitmap(simIcons.get(serviceProviders.get(position).getISPName() + (i + 1)));
                        }
                    }
                }
            } else {
                holder.firstSimImgBitmap.setVisibility(View.VISIBLE);
                holder.firstSimImgBitmap.setImageBitmap(simIcons.get(serviceProviders.get(position).getISPName()));
            }
        }
        holder.ispMainLayout.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(context, CodeContent.class);
            intent.putExtra(SIM_CARD_PRESENT, hasSim);
            intent.putExtra(ISP_NAME_EXT, serviceProviders.get(position).getISPName());
            intent.putExtra(ISP_SLOGAN_EXT, serviceProviders.get(position).getISPSlogan());
            intent.putExtra(SAME_CARRIER, sameCarrier);
            context.startActivity(intent);
        });
    }

    private int getIspLogo(int ispPosition) {
        switch (ispPosition) {
            case 0:
                return R.drawable.safaricom_logo;
            case 1:
                return R.drawable.airtel_logo;
            case 2:
                return R.drawable.telkom_logo;
            default:
                return R.drawable.faiba_logo;
        }
    }

    @Override
    public int getItemCount() {
        return serviceProviders.size();
    }

    public static class MyISPViewHolder extends RecyclerView.ViewHolder {
        CardView ispMainLayout;
        CircleImageView ispLogo;
        TextView ispName, ispSlogan;
        ImageView firstSimImgBitmap, secondSimImgBitmap;
        LinearLayout simLayout;

        public MyISPViewHolder(@NonNull View itemView) {
            super(itemView);
            ispMainLayout = itemView.findViewById(R.id.isp_main_layout);
            ispLogo = itemView.findViewById(R.id.isp_main_logo);
            ispName = itemView.findViewById(R.id.serviceProvider);
            ispSlogan = itemView.findViewById(R.id.serviceProviderSlogan);
            simLayout = itemView.findViewById(R.id.simLayout);
            firstSimImgBitmap = itemView.findViewById(R.id.firstSim);
            secondSimImgBitmap = itemView.findViewById(R.id.secondSim);
        }
    }
}
