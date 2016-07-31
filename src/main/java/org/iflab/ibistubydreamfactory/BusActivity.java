package org.iflab.ibistubydreamfactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.iflab.ibistubydreamfactory.adapters.BusAdapter;
import org.iflab.ibistubydreamfactory.apis.APISource;
import org.iflab.ibistubydreamfactory.apis.BusAPI;
import org.iflab.ibistubydreamfactory.models.Bus;
import org.iflab.ibistubydreamfactory.models.ErrorMessage;
import org.iflab.ibistubydreamfactory.models.Resource;
import org.iflab.ibistubydreamfactory.utils.ACache;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusActivity extends Activity {

    private ListView listViewBus;
    private ProgressBar progressBar;
    private List<Bus> busList;
    private Resource<Bus> busResource;
    private ACache aCache;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        init();//初始化
        if (busResource == null) {
            /*如果缓存没有就从网络获取*/
            getBusResource();
        } else {
            loadData();
        }
        listViewBus.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent();
                intent.putExtra("line", busList.get(position).getBusLine());
                intent.putExtra("name", busList.get(position).getBusName());
                intent.setClass(BusActivity.this, BusLineActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar_bus);
        listViewBus = (ListView) findViewById(R.id.listView_bus);
        aCache = ACache.get(MyApplication.getAppContext());
        busResource = (Resource<Bus>) aCache.getAsObject("busResource");

    }


    /**
     * 填充部门列表数据到ListView
     */
    private void loadData() {
        busList = busResource.getResource();
        progressBar.setVisibility(View.GONE);
        listViewBus.setAdapter(new BusAdapter(busList, BusActivity.this));
    }

    /**
     * 获得班车列表信息
     */
    private void getBusResource() {
        BusAPI busAPI = APISource.getInstance()
                                            .getAPIObject(BusAPI.class);
        Call<Resource<Bus>> call = busAPI.getBus();
        call.enqueue(new Callback<Resource<Bus>>() {
            @Override
            public void onResponse(Call<Resource<Bus>> call, Response<Resource<Bus>> response) {
                if (response.isSuccessful()) {
                    busResource = response.body();
                    aCache.put("busResource", busResource);
                    loadData();
                }else {
                    ErrorMessage e = APISource.getErrorMessage(response);//解析错误信息
                    onFailure(call, e.toException());
                }
            }

            @Override
            public void onFailure(Call<Resource<Bus>> call, Throwable t) {
                System.out.println("error：" + t.toString());
                Toast.makeText(BusActivity.this, "错误：" + t.getMessage(), Toast.LENGTH_LONG)
                     .show();
            }
        });

    }
}

