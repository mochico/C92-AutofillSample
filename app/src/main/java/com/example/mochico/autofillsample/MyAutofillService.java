package com.example.mochico.autofillsample;

import android.app.assist.AssistStructure;
import android.content.SharedPreferences;
import android.os.CancellationSignal;
import android.service.autofill.AutofillService;
import android.service.autofill.Dataset;
import android.service.autofill.FillCallback;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveInfo;
import android.service.autofill.SaveRequest;
import android.text.TextUtils;
import android.view.View;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

public class MyAutofillService extends AutofillService {

    public static String PREF_NAME = "USER_NAME_PREF";
    public static String PREF_KEY_USER_NAME = "username";

    @Override
    public void onFillRequest(FillRequest fillRequest, CancellationSignal cancellationSignal, FillCallback fillCallback) {

        AssistStructure structure = fillRequest.getFillContexts()
                .get(fillRequest.getFillContexts().size() - 1).getStructure();

        List<AssistStructure.ViewNode> userNameFields = new ArrayList<>();

        for (int i = 0; i < structure.getWindowNodeCount(); i++) {
            AssistStructure.WindowNode node = structure.getWindowNodeAt(i);
            AssistStructure.ViewNode rootViewNode = node.getRootViewNode();
            findUserNameFields(rootViewNode, userNameFields);
        }

        if (userNameFields.size() == 0) {
            return;
        }

        FillResponse.Builder responseBuilder = new FillResponse.Builder();

        SharedPreferences sharedPreferences =
                getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String username =
                sharedPreferences.getString(PREF_KEY_USER_NAME, "");

        for (AssistStructure.ViewNode userNameField : userNameFields) {
            if (userNameField.getAutofillId() == null) {
                continue;
            }
            RemoteViews userNameSuggestionRemoteView =
                    new RemoteViews(getPackageName(),
                            R.layout.user_name_suggestion);
            Dataset usernameDataSet =
                    new Dataset.Builder(userNameSuggestionRemoteView)
                            .setValue(
                                    userNameField.getAutofillId(),
                                    AutofillValue.forText(username)
                            ).build();

            responseBuilder.addDataset(usernameDataSet);

            AutofillId ids[] = {userNameField.getAutofillId()};
            responseBuilder.setSaveInfo(new SaveInfo.Builder(SaveInfo.SAVE_DATA_TYPE_USERNAME, ids).build());
        }

        fillCallback.onSuccess(responseBuilder.build());
    }

    @Override
    public void onSaveRequest(SaveRequest saveRequest, SaveCallback saveCallback) {
        AssistStructure structure = saveRequest.getFillContexts()
                .get(saveRequest.getFillContexts().size() - 1).getStructure();

        List<AssistStructure.ViewNode> userNameFields = new ArrayList<>();

        for (int i = 0; i < structure.getWindowNodeCount(); i++) {
            AssistStructure.WindowNode node = structure.getWindowNodeAt(i);
            AssistStructure.ViewNode rootViewNode = node.getRootViewNode();
            findUserNameFields(rootViewNode, userNameFields);
        }

        if (userNameFields.size() == 0) {
            return;
        }

        String userName = userNameFields.get(0).getAutofillValue().getTextValue().toString();

        SharedPreferences sharedPreferences =
                getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_USER_NAME, userName);
        editor.apply();
    }

    void findUserNameFields(AssistStructure.ViewNode node,
                            List<AssistStructure.ViewNode> userNameFields) {
        if (node.getAutofillHints() != null && node.getAutofillHints().length > 0) {
            String autofillHint = node.getAutofillHints()[0];
            if (!TextUtils.isEmpty(autofillHint) && autofillHint.equals(View.AUTOFILL_HINT_USERNAME)) {
                userNameFields.add(node);
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            findUserNameFields(node.getChildAt(i), userNameFields);
        }
    }
}
