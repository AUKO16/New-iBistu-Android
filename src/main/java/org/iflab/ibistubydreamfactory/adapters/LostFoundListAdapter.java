package org.iflab.ibistubydreamfactory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.iflab.ibistubydreamfactory.MyApplication;
import org.iflab.ibistubydreamfactory.R;
import org.iflab.ibistubydreamfactory.models.LostFound;
import org.iflab.ibistubydreamfactory.models.LostFoundImageURL;
import org.iflab.ibistubydreamfactory.utils.ACache;
import org.iflab.ibistubydreamfactory.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class LostFoundListAdapter extends BaseAdapter {
    private List<LostFound> lostFoundList;
    private Context context;
    private String SESSION_TOKEN = ACache.get(MyApplication.getAppContext()).getAsString("SESSION_TOKEN");

    public LostFoundListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return lostFoundList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 绘制每个item
     *
     * @param position 点击的位置
     * @param convertView item对应的View
     * @param parent 可选的父控件
     * @return 要显示的单个item的View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lost_found, null);
            viewHolder = new ViewHolder();
            viewHolder.lostFoundTitle = (TextView) convertView.findViewById(R.id.title_lost_found);
            viewHolder.lostFoundIntro = (TextView) convertView.findViewById(R.id.content_lost_found);
            viewHolder.lostFoundTime = (TextView) convertView.findViewById(R.id.time_lost_found);
            viewHolder.lostFoundImage = (ImageView) convertView.findViewById(R.id.image_lost_found);
//            viewHolder.recyclerViewImages = (RecyclerView) convertView.findViewById(R.id.recyclerView_images);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.lostFoundTitle.setText(lostFoundList.get(position).getTitle());
        viewHolder.lostFoundIntro.setText(lostFoundList.get(position).getDetails());
        viewHolder.lostFoundTime.setText(lostFoundList.get(position).getCreateTime());
        List<LostFoundImageURL> list = JsonUtils.json2List(lostFoundList.get(position)
                                                                        .getImgUrlList(), LostFoundImageURL.class);
        if (list != null) {
            final ArrayList<String> imagePaths = new ArrayList<>();
            for (LostFoundImageURL url : list) {
                imagePaths.add(url.getUrl() + "?api_key=" + MyApplication.API_KEY + "&session_token=" + SESSION_TOKEN);
            }
            Glide.with(context)
                 .load(imagePaths.get(0))
                 .centerCrop()
                 .thumbnail(0.3f)
                 .placeholder(R.drawable.ic_image_loading_picture)
                 .error(me.iwf.photopicker.R.drawable.__picker_ic_broken_image_black_48dp)
                 .into(viewHolder.lostFoundImage);
        }

        return convertView;
    }

    /**
     * 把更新后的新闻列表同步到listView里
     *
     * @param list 更新后的新闻列表
     */
    public void addItem(List<LostFound> list) {
        lostFoundList = list;
    }

    /**
     * 起优化作用ListView的ViewHolder类，避免多次加载TextView
     */
    private class ViewHolder {
        private ImageView lostFoundImage;
        private TextView lostFoundTitle, lostFoundIntro, lostFoundTime;
//        private RecyclerView recyclerViewImages;
    }
}
