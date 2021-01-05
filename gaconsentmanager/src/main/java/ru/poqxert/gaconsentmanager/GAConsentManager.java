package ru.poqxert.gaconsentmanager;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.godotengine.godot.Godot;
import org.godotengine.godot.Dictionary;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import com.explorestack.consent.Consent;
import com.explorestack.consent.ConsentForm;
import com.explorestack.consent.ConsentFormListener;
import com.explorestack.consent.ConsentInfoUpdateListener;
import com.explorestack.consent.ConsentManager;
import com.explorestack.consent.Vendor;
import com.explorestack.consent.exception.ConsentManagerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.explorestack.consent.Consent.Zone.*;
import static com.explorestack.consent.ConsentManager.Storage.SHARED_PREFERENCE;

public class GAConsentManager extends GodotPlugin {
    private Activity activity;

    private FrameLayout layout = null;

    private ConsentManager consentManager = null;
    private ConsentFormListener listener = null;
    private ConsentForm dialog = null;

    public GAConsentManager(Godot godot) {
        super(godot);
        activity = godot;
        consentManager = ConsentManager.getInstance(activity);
        listener = new ConsentFormListener() {
            @Override
            public void onConsentFormLoaded() {
                emitSignal("dialog_loaded");
            }

            @Override
            public void onConsentFormError(ConsentManagerException e) {
                emitSignal("dialog_failed", e.toString());
            }

            @Override
            public void onConsentFormOpened() {
                emitSignal("dialog_shown");
            }

            @Override
            public void onConsentFormClosed(Consent consent) {
                emitSignal("dialog_closed");
            }
        };
        dialog = new ConsentForm.Builder(activity).withListener(listener).build();
    }

    @Nullable
    @Override
    public View onMainCreate(Activity activity) {
        layout = new FrameLayout(activity);
        return layout;
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "GAConsentManager";
    }

    @NonNull
    @Override
    public List<String> getPluginMethods() {
        return Arrays.asList(
                "synchronize",
                "synchronizeWithParams",
                "shouldShowConsentDialog",
                "loadConsentDialog",
                "isConsentDialogReady",
                "showConsentDialog",
                "isConsentDialogPresenting",
                "getRegulation",
                "getStatus",
                "getConsent",
                "getIABConsentString",
                "hasConsentForVendor",
                "enableIABStorage"
        );
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signalInfoSet = new HashSet<>();
        signalInfoSet.add(new SignalInfo("synchronized"));
        signalInfoSet.add(new SignalInfo("synchronization_failed", String.class));
        signalInfoSet.add(new SignalInfo("dialog_loaded"));
        signalInfoSet.add(new SignalInfo("dialog_failed"));
        signalInfoSet.add(new SignalInfo("dialog_shown"));
        signalInfoSet.add(new SignalInfo("dialog_closed"));
        return signalInfoSet;
    }

    public void synchronize(String appKey) {
        consentManager.requestConsentInfoUpdate(appKey, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(Consent consent) {
                emitSignal("synchronized");
            }

            @Override
            public void onFailedToUpdateConsentInfo(ConsentManagerException e) {
                emitSignal("synchronization_failed", e.toString());
            }
        });
    }

    public void synchronizeWithParams(String appKey, Dictionary params) {
        if(params.isEmpty() || !params.containsKey("url")) {
            synchronize(appKey);
        } else {
           consentManager.requestConsentInfoUpdate(appKey, (String)params.get("url"), new ConsentInfoUpdateListener() {
                @Override
                public void onConsentInfoUpdated(Consent consent) {
                    emitSignal("synchronized");
                }

                @Override
                public void onFailedToUpdateConsentInfo(ConsentManagerException e) {
                    emitSignal("synchronization_failed", e.toString());
                }
            });
        }
    }

    public boolean shouldShowConsentDialog() {
        return consentManager.shouldShowConsentDialog() == Consent.ShouldShow.TRUE;
    }

    public void loadConsentDialog() {
        dialog.load();
    }

    public boolean isConsentDialogReady() {
        return dialog.isLoaded();
    }

    public void showConsentDialog() {
        dialog.showAsActivity();
    }

    public boolean isConsentDialogPresenting() {
        return dialog.isShowing();
    }

    public int getRegulation() {
        switch (consentManager.getConsent().getZone()) {
            case NONE:
                return 1;
            case GDPR:
                return 2;
            case CCPA:
                return 3;
            default:
                return 0;
        }
    }

    public int getStatus() {
        switch (consentManager.getConsent().getStatus()) {
            case NON_PERSONALIZED:
                return 1;
            case PARTLY_PERSONALIZED:
                return 2;
            case PERSONALIZED:
                return 3;
            default:
                return 0;
        }
    }

    public boolean getConsent() {
        return getStatus() != 1;
    }

    public String getIABConsentString() {
        if(consentManager.getConsent().getZone() == CCPA) {
            return consentManager.getUSPrivacyString();
        }
        return consentManager.getIabConsentString();
    }

    public boolean hasConsentForVendor(String bundle) {
        return consentManager.getConsent().hasConsentForVendor(bundle) == Consent.HasConsent.TRUE;
    }

    public void enableIABStorage() {
        consentManager.setStorage(SHARED_PREFERENCE);
    }
}
