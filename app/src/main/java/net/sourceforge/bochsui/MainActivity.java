package net.sourceforge.bochsui;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import net.sourceforge.bochsui.adapter.TabsPagerAdapter;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    public static String path;
    public static MainActivity main;

    private ViewPager viewPager;
    private ActionBar actionBar;

    // Tab titles
    private String[] tabs = {"Storage", "Hardware", "Misc"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        main = this;
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/net.sourceforge.bochs/files/bochsrc.txt";

        if (!Config.configLoaded) {
            try {
                Config.readConfig();
            } catch (FileNotFoundException e) {
                Toast.makeText(MainActivity.this, "config not found", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(MainActivity.this, "config loaded", Toast.LENGTH_SHORT).show();
            Config.configLoaded = true;
        }


        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        TabsPagerAdapter mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.start) {
            save();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    public void save() {
        try {
            Config.writeConfig();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Error, config not saved", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(MainActivity.this, "config saved", Toast.LENGTH_SHORT).show();

        // run bochs app
        ComponentName cn = new ComponentName("net.sourceforge.bochs", "net.sourceforge.bochs.MainActivity");
        Intent intent = new Intent();
        intent.setComponent(cn);
        startActivity(intent);
    }

    static String getFileName(String path) {
        String result;
        if (path.contains("/"))
            result = path.substring(path.lastIndexOf("/") + 1, path.length());
        else
            result = path;
        return result;
    }

}
