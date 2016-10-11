package com.blackbeard.sensors;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.dto.DeviceInfoDto;
import java.util.ArrayList;
import org.w3c.dom.Text;

/**
 * Created by sudendra.kamble on 12/10/16.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.RecyclerVH> {
  ArrayList<DeviceInfoDto> list;
  public SearchAdapter(ArrayList<DeviceInfoDto> list){
    super();
    this.list = list;
  }
  @Override public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
    return new RecyclerVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false));
  }

  @Override public void onBindViewHolder(RecyclerVH holder, int position) {
    DeviceInfoDto deviceInfoDto = list.get(position);
    holder.textView.setText(deviceInfoDto.toString());
  }

  @Override public int getItemCount() {
    return list.size();
  }

  public void refresh(ArrayList<DeviceInfoDto> list) {
    this.list.clear();
    this.list.addAll(list);
    notifyDataSetChanged();
  }

  class RecyclerVH extends RecyclerView.ViewHolder {
    TextView textView;
    public RecyclerVH(View itemView) {
      super(itemView);
      textView = (TextView) itemView.findViewById(R.id.textview);
    }
  }
}
