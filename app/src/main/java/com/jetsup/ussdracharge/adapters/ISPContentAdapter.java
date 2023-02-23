package com.jetsup.ussdracharge.adapters;


import static com.jetsup.ussdracharge.custom.ISPConstants.AIRTEL_CREDIT_LENGTH;
import static com.jetsup.ussdracharge.custom.ISPConstants.ARRAY_TYPE;
import static com.jetsup.ussdracharge.custom.ISPConstants.DRAWABLE_TYPE;
import static com.jetsup.ussdracharge.custom.ISPConstants.FAIBA_CREDIT_LENGTH;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_LOGO_EXT;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_NAME_AIRTEL;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_NAME_FAIBA;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_NAME_SAFARICOM;
import static com.jetsup.ussdracharge.custom.ISPConstants.ISP_NAME_TELKOM;
import static com.jetsup.ussdracharge.custom.ISPConstants.REQUEST_PHONE_NUMBER;
import static com.jetsup.ussdracharge.custom.ISPConstants.REQUEST_PIN;
import static com.jetsup.ussdracharge.custom.ISPConstants.SAFARICOM_CREDIT_LENGTH;
import static com.jetsup.ussdracharge.custom.ISPConstants.SELECT_SIM_SLOT;
import static com.jetsup.ussdracharge.custom.ISPConstants.TELKOM_CREDIT_LENGTH;
import static com.jetsup.ussdracharge.custom.ISPConstants.USSD_CODE_EXT;
import static com.jetsup.ussdracharge.custom.ISPConstants.USSD_CODE_NAME_EXT;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.jetsup.ussdracharge.R;
import com.jetsup.ussdracharge.models.ISPContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ISPContentAdapter extends RecyclerView.Adapter<ISPContentAdapter.ContentViewHolder> implements Filterable {

    final String TAG = "MyTag";
    private final String ispNameReceived;
    Pattern inputNumberPattern = Pattern.compile("^[*]\\d+[*][A-Za-z\\s]+#$");
    Pattern phoneNumberFormat1 = Pattern.compile("^0[1 7]\\d{8}$");
    Pattern pinFormat = Pattern.compile("\\d{12,16}");
    Pattern phoneNumberFormat2 = Pattern.compile("^\\+254[1|7]\\d{8}$");
    String uriString;
    Uri dialReqUri;// = Uri.fromParts("tel", ispContentList.get(position).getUssdCode(), null);
    Map<String, Integer> pinLength = new HashMap<>();
    List<ISPContent> ispContentList;
    String inputType;
    boolean sameCarrier;
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
    private boolean dial;

    {
        pinLength.put(ISP_NAME_SAFARICOM, SAFARICOM_CREDIT_LENGTH);
        pinLength.put(ISP_NAME_AIRTEL, AIRTEL_CREDIT_LENGTH);
        pinLength.put(ISP_NAME_TELKOM, TELKOM_CREDIT_LENGTH);
        pinLength.put(ISP_NAME_FAIBA, FAIBA_CREDIT_LENGTH);
    }

    @SuppressLint("DiscouragedApi")
    public ISPContentAdapter(Context context, String isp_name) {
        this.context = context;
        this.ispNameReceived = isp_name;
        isp_name = isp_name.toLowerCase();
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
    public ISPContentAdapter(Context context, String isp_name, int simSlot, boolean sameCarrier) {
        List<String> ussdCodeNames;
        List<String> ussdCodes;
        this.context = context;
        this.simSlot = simSlot;
        this.sameCarrier = sameCarrier;
        this.ispNameReceived = isp_name;
        isp_name = isp_name.toLowerCase();
        ispContentList = new ArrayList<>();
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
            Intent dialIntent = new Intent();

            if (inputNumberPattern.matcher(ispContentList.get(position).getUssdCode()).matches()) {
                Dialog dialog = new Dialog(context);
                dialog.setTitle("Input");
                dialog.setOnKeyListener((dialog1, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog1.dismiss();
                    }
                    return false;
                });
                dialog.setContentView(R.layout.card_dialler); // set the hint and textInput programmatically
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);

                TextInputLayout inputLayout = dialog.findViewById(R.id.variableInputLayout);
                Objects.requireNonNull(inputLayout.getEditText()).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (inputType.equals(REQUEST_PIN)) {
                            if (inputLayout.isErrorEnabled()) {
                                if (pinFormat.matcher(s).matches() && s.length() == Objects.requireNonNull(pinLength.get(ispNameReceived))) {
                                    inputLayout.setErrorEnabled(false);
                                }
                            }
                        } else if (inputType.equals(REQUEST_PHONE_NUMBER)) {
                            if (inputLayout.isErrorEnabled()) {
                                if (phoneNumberFormat1.matcher(s).matches() || phoneNumberFormat1.matcher(s).matches()) {
                                    inputLayout.setErrorEnabled(false);
                                    inputLayout.setHelperText("Phone format check good");
                                }
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (inputType.equals(REQUEST_PIN)) {
                            if (!pinFormat.matcher(s).matches() && s.length() != Objects.requireNonNull(pinLength.get(ispNameReceived))) {
                                inputLayout.setHelperText("PIN format does not match the selected ISP");
                            } else {
                                inputLayout.setHelperText("PIN format matched");
                            }
                        } else if (inputType.equals(REQUEST_PHONE_NUMBER)) {
                            if (!phoneNumberFormat1.matcher(s.toString()).matches()) {
                                if (!phoneNumberFormat2.matcher(s).matches()) {
                                    inputLayout.setHelperText("Phone number format unrecognized");
                                } else {
                                    inputLayout.setHelperText("Phone number format correct");
                                }
                            } else {
                                inputLayout.setHelperText("Phone number format correct");
                            }
                        }
                    }
                });
                if (ispContentList.get(position).getUssdCode().toLowerCase().contains("pin")) {
                    inputLayout.setHint("Enter the scratch card PIN");
                    inputType = REQUEST_PIN;
                } else if (ispContentList.get(position).getUssdCode().toLowerCase().contains("number")) {
                    inputLayout.setHint("Enter the Recipient Phone Number");
                    inputType = REQUEST_PHONE_NUMBER;
                }
                Button btnDial = dialog.findViewById(R.id.btnInputDialogDial), btnCancel = dialog.findViewById(R.id.btnInputDialogCancel);
                btnCancel.setOnClickListener(v1 -> dialog.dismiss());
                btnDial.setOnClickListener(v12 -> {
                    if (inputType.equals(REQUEST_PIN)) {
                        String scratchPIN = Objects.requireNonNull(inputLayout.getEditText()).getText().toString();
                        if (pinFormat.matcher(scratchPIN).matches() &&
                                scratchPIN.length() >= Objects.requireNonNull(pinLength.get(ispNameReceived))) {
                            uriString = scratchPIN;
                            dial = true;
                            dialog.dismiss();
                        } else {
                            inputLayout.setError("Scratch PIN format incorrect");
                        }
                    } else if (inputType.equals(REQUEST_PHONE_NUMBER)) {
                        String phoneNumber = Objects.requireNonNull(inputLayout.getEditText()).getText().toString();
                        if (phoneNumberFormat1.matcher(phoneNumber).matches() || phoneNumberFormat2.matcher(phoneNumber).matches()) {
                            uriString = phoneNumber;
                            dial = true;
                            // TODO: confirmation dialog
                            dialog.dismiss();
                        } else {
                            inputLayout.setError("Unrecognized Number Format");
                        }
                    }
                    if (dial) {
                        dialReqUri = Uri.fromParts("tel",
                                ispContentList.get(position).getUssdCode().substring(0,
                                        (ispContentList.get(position).getUssdCode().indexOf("*", 2) + 1)) +
                                        uriString + "#", null);

                        dialIntent.setAction(Intent.ACTION_CALL).setData(dialReqUri);
                        if (simPresent && !sameCarrier) {
                            dialIntent.putExtra(SELECT_SIM_SLOT, simSlot);
                        }
                        context.startActivity(dialIntent);
                        dial = false;
                    }
                });

                dialog.create();
                dialog.show();
            } else {
                dialReqUri = Uri.fromParts("tel", ispContentList.get(position).getUssdCode(), null);
                dialIntent.setAction(Intent.ACTION_CALL).setData(dialReqUri);
                if (simPresent && !sameCarrier) {
                    dialIntent.putExtra(SELECT_SIM_SLOT, simSlot);
                }
                context.startActivity(dialIntent);
            }
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
