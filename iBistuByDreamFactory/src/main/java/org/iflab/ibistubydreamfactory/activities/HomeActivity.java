package org.iflab.ibistubydreamfactory.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import org.iflab.ibistubydreamfactory.R;
import org.iflab.ibistubydreamfactory.fragments.HomeFragment;
import org.iflab.ibistubydreamfactory.utils.CheckUpdateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页
 */
public class HomeActivity extends AppCompatActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener {

    private static final int PERMISSION_CODE_STORAGE = 2;
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment menuDialogFragment;
    private long exitTime = 0;//记录按返回键的时间点
    private View parentView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        setContentView(parentView);
        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();
        addFragment(new HomeFragment(), true, R.id.container);
        intent = new Intent();

    }


    /**
     * 初始化菜单的Fragment
     */
    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        menuDialogFragment.setItemClickListener(this);
        menuDialogFragment.setItemLongClickListener(this);
    }

    /**
     * 添加菜单选项对象
     *
     * @return 包含选项的集合
     */
    private List<MenuObject> getMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject cancelOption = new MenuObject();
        cancelOption.setResource(R.drawable.selector_option_fold);

        MenuObject userOption = new MenuObject("个人中心");
        userOption.setResource(R.drawable.ic_option_user);

        MenuObject aboutOption = new MenuObject("关于我们");
        aboutOption.setResource(R.drawable.ic_option_about);

        MenuObject updateOption = new MenuObject("检查更新");
        updateOption.setResource(R.drawable.ic_option_update);

        menuObjects.add(cancelOption);
        menuObjects.add(userOption);
        menuObjects.add(aboutOption);
        menuObjects.add(updateOption);
        return menuObjects;
    }

    /**
     * 初始化工具栏
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("iBistu");
        toolbar.setLogo(R.drawable.ic_action_logo_bistu);//设置静态logo
        setSupportActionBar(toolbar);//把ToolBar设置为ActionBar
    }

    /**
     * 添加Fragment
     *
     * @param fragment fragment
     * @param addToBackStack addToBackStack
     * @param containerId containerId
     */
    protected void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(containerId, fragment, backStackName)
                       .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            if (addToBackStack) {
                transaction.addToBackStack(backStackName);
            }
            transaction.commit();
        }
    }


    /**
     * 加载菜单
     *
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    /**
     * 监听工具栏上的按钮
     *
     * @param item 点击Toolbar上的按钮时传入
     * @return super.onOptionsItemSelected(item)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    menuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 监听菜单选项
     *
     * @param clickedView 点击菜单内的选项
     * @param position 点击的位置
     */
    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch (position) {
            case 0://返回主界面
                break;
            case 1://个人中心
                intent.setClass(this, UserCenterActivity.class);
                startActivity(intent);
                break;
            case 2://关于
                intent.setClass(this, AboutActivity.class);
                startActivity(intent);
                break;
            case 3://检查更新
                if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    CheckUpdateUtil.checkUpdate(parentView, HomeActivity.this);
                } else {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSION_CODE_STORAGE);
                }
                break;
        }

    }

    /**
     * 授权回调
     *
     * @param grantResults grantResults[i] 对应的权限用户是否授权
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE_STORAGE://同类权限只要有一个被允许，其他都算允许
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户授权了
                    CheckUpdateUtil.checkUpdate(parentView, HomeActivity.this);
                } else {
                    Toast.makeText(HomeActivity.this, "没有访问存储权限，将无法下载软件更新包！", Toast.LENGTH_SHORT)
                         .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 当在主页按返回键时，双击退出，并确保fragment被dismiss
     */
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(HomeActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            if (menuDialogFragment != null && menuDialogFragment.isAdded()) {
                menuDialogFragment.dismiss();
            }
            finish();
        }
    }

    /**
     * 监听按键
     *
     * @param keyCode 正在点击的按键代码
     * @param event 触发的事件
     * @return onKeyDown
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*按菜单键时也能弹出菜单*/
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                menuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 监听长安菜单选项
     */
    @Override
    public void onMenuItemLongClick(View clickedView, int position) {

    }

}
