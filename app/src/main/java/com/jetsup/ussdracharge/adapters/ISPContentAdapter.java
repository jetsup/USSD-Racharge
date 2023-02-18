package com.jetsup.ussdracharge.adapters;


import static com.jetsup.ussdracharge.custom.ISPConstants.ARRAY_TYPE;
import static com.jetsup.ussdracharge.custom.ISPConstants.DRAWABLE_TYPE;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_LOGO_EXT;
import static com.jetsup.ussdracharge.custom.ISPConstants.SELECT_SIM_SLOT;
import static com.jetsup.ussdracharge.custom.ISPConstants.USSD_CODE_EXT;
import static com.jetsup.ussdracharge.custom.ISPConstants.USSD_CODE_NAME_EXT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jetsup.ussdracharge.R;
import com.jetsup.ussdracharge.models.ISPContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ISPContentAdapter extends RecyclerView.Adapter<ISPContentAdapter.ContentViewHolder> implements Filterable {

    final String TAG = "MyTag";
    List<ISPContent> ispContentList;
    List<ISPContent> ispContentListSearchable;
    private final Filter contentSearchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ISPContent> probableContentSearch = new ArrayList<>();
            if (constraint == null) {
                probableContentSearch.addAll(ispContentListSearchable);
            } else {
                String searchPattern = constraint.toString().toLowerCase().trim();
                for (ISPContent content : ispContentListSearchable) {
                    if (content.getUssdCodeName().toLowerCase().contains(searchPattern)) {
                        probableContentSearch.add(content);
                    }
                }
            }
            FilterResults searchResult = new FilterResults();
            searchResult.values = probableContentSearch;
            return searchResult;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ispContentList.clear();
            //noinspection unchecked
            ispContentList.addAll((Collection<? extends ISPContent>) results.values);
            notifyDataSetChanged();
        }
    };
    Context context;
    Drawable ispLogoIcon;
    private int simSlot;
    private boolean simPresent = false;

    @SuppressLint("DiscouragedApi")
    public ISPContentAdapter(Context context, String isp_name) {
        this.context = context;
        List<String> ussdCodeNames;
        List<String> ussdCodes;
        ispContentList = new ArrayList<>();
        int codeNamesIdentifier = context.getResources().getIdentifier(isp_name + USSD_CODE_NAME_EXT, ARRAY_TYPE, context.getPackageName());
        ussdCodeNames = Arrays.asList(context.getResources().getStringArray(codeNamesIdentifier));

        int codeIdentifier = context.getResources().getIdentifier(isp_name + USSD_CODE_EXT, ARRAY_TYPE, context.getPackageName());
        ussdCodes = Arrays.asList(context.getResources().getStringArray(codeIdentifier));

        int drawableIdentifier = context.getResources().getIdentifier(isp_name + ISP_LOGO_EXT, DRAWABLE_TYPE, context.getPackageName());
        ispLogoIcon = ResourcesCompat.getDrawable(context.getResources(), drawableIdentifier, null);
        for (int i = 0; i < ussdCodeNames.size(); i++) {
            ispContentList.add(new ISPContent(ussdCodeNames.get(i), ussdCodes.get(i)));
        }
        ispContentListSearchable = new ArrayList<>(ispContentList);
    }

    @SuppressLint("DiscouragedApi")
    public ISPContentAdapter(Context context, String isp_name, int simSlot) {
        List<String> ussdCodeNames;
        List<String> ussdCodes;
        this.context = context;
        ispContentList = new ArrayList<>();
        this.simSlot = simSlot;
        simPresent = true;
        int codeNamesIdentifier = context.getResources().getIdentifier(isp_name + USSD_CODE_NAME_EXT, ARRAY_TYPE, context.getPackageName());
        ussdCodeNames = Arrays.asList(context.getResources().getStringArray(codeNamesIdentifier));

        int codeIdentifier = context.getResources().getIdentifier(isp_name + USSD_CODE_EXT, ARRAY_TYPE, context.getPackageName());
        ussdCodes = Arrays.asList(context.getResources().getStringArray(codeIdentifier));

        int drawableIdentifier = context.getResources().getIdentifier(isp_name + ISP_LOGO_EXT, DRAWABLE_TYPE, context.getPackageName());
        ispLogoIcon = ResourcesCompat.getDrawable(context.getResources(), drawableIdentifier, null);
        for (int i = 0; i < ussdCodeNames.size(); i++) {
            ispContentList.add(new ISPContent(ussdCodeNames.get(i), ussdCodes.get(i)));
        }
        ispContentListSearchable = new ArrayList<>(ispContentList);
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ussd_option_item_holder, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.ussdCodeName.setText(ispContentList.get(position).getUssdCodeName());
        holder.ussdCode.setText(ispContentList.get(position).getUssdCode());
        holder.ispLogoImage.setImageDrawable(ispLogoIcon);
        holder.cardViewLayout.setOnClickListener(v -> {
            // TODO: Extract this and use it to handle data from user input dialog
            Uri dialReqUri = Uri.fromParts("tel", ispContentList.get(position).getUssdCode(), null);
            Intent dialIntent = new Intent(Intent.ACTION_CALL, dialReqUri);
            if (simPresent) {
                dialIntent.putExtra(SELECT_SIM_SLOT, simSlot);
            }
            context.startActivity(dialIntent);
        });
    }

    @Override
    public int getItemCount() {
        return ispContentList.size();
    }

    @Override
    public Filter getFilter() {
        return contentSearchFilter;
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewLayout;
        CircleImageView ispLogoImage;
        TextView ussdCodeName, ussdCode;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewLayout = itemView.findViewById(R.id.ussd_card_layout);
            ispLogoImage = itemView.findViewById(R.id.isp_content_logo);
            ussdCodeName = itemView.findViewById(R.id.ussd_code_name);
            ussdCode = itemView.findViewById(R.id.ussd_code);
        }

    }
}
