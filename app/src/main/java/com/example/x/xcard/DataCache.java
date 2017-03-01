package com.example.x.xcard;

import com.com.x.AppModel.ModelUtil;
import com.com.x.AppModel.UserModel;
import com.robin.lazy.cache.CacheLoaderManager;
import com.x.custom.XNetUtil;

/**
 * Created by X on 2016/10/2.
 */

public class DataCache {

    public UserModel User = new UserModel();

    public DataCache() {

        UserModel model=CacheLoaderManager.getInstance().loadSerializable("UserModel");

        if(model != null)
        {
            User = model;
        }
        else
        {
            ModelUtil.reSet(User);
        }



        XNetUtil.APPPrintln("User: "+User.toString());

    }
}
