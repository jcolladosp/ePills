package devs.erasmus.epills.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import devs.erasmus.epills.R;
import devs.erasmus.epills.controller.AddPillGeneralActivity;
import devs.erasmus.epills.controller.ClockActivity;
import devs.erasmus.epills.controller.MedicineBoxActivity;

/**
 * Created by colla on 07/11/2017.
 */

public class NavigationDrawer {
    private static DrawerBuilder drawerBuilder;

    public static DrawerBuilder getDrawerBuilder(final Context context, final Activity activity, Toolbar toolbar){
        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header)
                .withSelectionListEnabledForSingleProfile(false)
                .withOnlyMainProfileImageVisible(true)
                .addProfiles(
                        new ProfileDrawerItem().withName("Jose").withEmail("jose@erasmusdevs.com").withIcon(R.drawable.profile)
                )

                .build();


        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(activity.getApplicationContext().getString(R.string.title_activity_clock));
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Add a Medicine");
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(activity.getString(R.string.medicinBox_Title));
        return  drawerBuilder =  new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Settings")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                context.startActivity(new Intent(context,ClockActivity.class));
                                activity.finish();

                                break;
                            case 2:
                                context.startActivity(new Intent(context,AddPillGeneralActivity.class));

                                break;
                            case 3:
                                context.startActivity(new Intent(context, MedicineBoxActivity.class));
                                activity.finish();

                                break;
                        }



                        return true;
                    }
                });
    }

}
