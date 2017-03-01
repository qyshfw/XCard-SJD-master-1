package com.x.custom;

import android.content.Context;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.example.x.xcard.ApplicationClass;

import java.lang.ref.WeakReference;

/**
 * Created by X on 2016/10/2.
 */

public class XActivityindicator {

    private static SVProgressHUD hud;
    private static WeakReference<AlertView> alert;

    public static void setAlert(AlertView alert) {
        XActivityindicator.alert = new WeakReference<AlertView>(alert);
    }

    public static void hide()
    {
        if(alert != null && alert.get() != null)
        {
            alert.get().dismissImmediately();
            alert.clear();
            alert = null;
        }

        if(hud != null)
        {
            hud.dismissImmediately();
            hud = null;
        }
    }

    public static SVProgressHUD getHud() {
        return hud;
    }

    public static SVProgressHUD create(Context context)
    {
        if(alert != null && alert.get() != null)
        {
            alert.get().dismissImmediately();
        }

        if(hud != null)
        {
            hud.dismissImmediately();
            hud = null;
        }

        XNetUtil.APPPrintln("ApplicationClass.context: "+ApplicationClass.context);

        hud = new SVProgressHUD(context);

        XNetUtil.APPPrintln("hud: "+hud);

        hud.getProgressBar().setRoundWidth(DensityUtil.dip2px(ApplicationClass.context,1));



        return hud;
    }

}
