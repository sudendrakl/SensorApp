package com.blackbeard.sensors.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.api.dto.AccelerometerDto;
import com.blackbeard.sensors.api.dto.BarometerDto;
import com.blackbeard.sensors.api.dto.BatteryDto;
import com.blackbeard.sensors.api.dto.BluetoothDto;
import com.blackbeard.sensors.api.dto.DeviceInfoDto;
import com.blackbeard.sensors.api.dto.GPSDto;
import com.blackbeard.sensors.api.dto.GyroDto;
import com.blackbeard.sensors.api.dto.NFCDto;
import com.blackbeard.sensors.api.dto.ProximityDto;
import com.blackbeard.sensors.api.dto.StepsDto;
import com.blackbeard.sensors.api.dto.ThermometerDto;
import com.blackbeard.sensors.utils.LocationUtils;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sudendra.kamble on 12/10/16.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.RecyclerVH> {
  ArrayList<DeviceInfoDto> list;

  public SearchAdapter(ArrayList<DeviceInfoDto> list) {
    super();
    this.list = list;
  }

  @Override public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
    return new RecyclerVH(
        LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false));
  }

  @SuppressLint("DefaultLocale") @Override public void onBindViewHolder(RecyclerVH holder, int position) {

    DeviceInfoDto deviceDto = list.get(position);
    Context context = holder.userInfo.getContext();
    if(deviceDto!=null) {
      //holder.userInfo.setText(holder.userInfo.getContext()
      //    .getString(R.string.device_info, deviceInfoDto.getDevice(), deviceInfoDto.getManufacturer(),
      //        deviceInfoDto.getModel(), deviceInfoDto.getSystemVersion(), deviceInfoDto.getSystemVersionName(),
      //        deviceInfoDto.getUserName(), deviceInfoDto.getPhone()));

      holder.userInfo.setText(context.getString(R.string.device_info_minimal, deviceDto.getUserName(), deviceDto.getPhone()));
      if (deviceDto.getTimestampMillis() != 0) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(deviceDto.getTimestampMillis());
        holder.userInfo.append("\n" + calendar.getTime());
      }
      ExpandableTextView sensorInfo = holder.sensorInfo;
      SpannableStringBuilder builder=new SpannableStringBuilder();

      SpannableStringBuilder sensorTextBuilder = new SpannableStringBuilder("");
      for(Object object : deviceDto.getHardwareDetails()) {
        if (object instanceof AccelerometerDto) {
          sensorTextBuilder.append(getTitleBuilder(builder,"Accelerometer"));
          sensorTextBuilder.append(String.format( "X:%.2f  Y:%.2f Z:%.2f", ((AccelerometerDto) object).getX(), ((AccelerometerDto) object).getY(), ((AccelerometerDto) object).getZ()));
        } else if(object instanceof BarometerDto) {
          sensorTextBuilder.append(getTitleBuilder(builder,"Barometer"));
          sensorTextBuilder.append(String.format( "Pressure:%.2f", ((BarometerDto) object).getPressure()));
        } else if(object instanceof BatteryDto) {
          sensorTextBuilder.append(getTitleBuilder(builder,"Battery"));
          sensorTextBuilder.append(String.format("Charging:%s Type:%s\nAvailable:%.0f%%", ((BatteryDto) object).getIsCharging(), ((BatteryDto) object).getChargingType(), ((BatteryDto) object).getLevel()));
        } else if(object instanceof BluetoothDto) {
          sensorTextBuilder.append(getTitleBuilder(builder,"Bluetooth"));
          sensorTextBuilder.append(String.format("Status:%s", ((BluetoothDto) object).getStatus()));
          if(((BluetoothDto) object).getBluetoothOnStatus() !=null)
          sensorTextBuilder.append("\n" +((BluetoothDto) object).getBluetoothOnStatus() + "\n" + (((BluetoothDto) object).getDevicesList().size() > 0 ? ((BluetoothDto) object).getDevicesList().toString() : "No devices"));
        } else if(object instanceof GPSDto) {
          sensorTextBuilder.append(getTitleBuilder(builder,"GPS"));
          sensorTextBuilder.append(String.format("Available:%s Provider:%s Enabled:%s Accuracy:%s\n", ((GPSDto) object).isAvailable(), ((GPSDto) object).getProvider(), ((GPSDto) object).isEnabled(), LocationUtils.getLocationMode(((GPSDto) object).getAccuracyMode())));
          sensorTextBuilder.append(String.format( "Latitude:%.3f  Longitude:%.3f Accuracy:%.3s", ((GPSDto) object).getLatitude(),
              ((GPSDto) object).getLongitude(), ((GPSDto) object).getAccuracy()));
        } else if(object instanceof GyroDto) {
          sensorTextBuilder.append(getTitleBuilder(builder,"Gyroscope"));
          sensorTextBuilder.append(String.format("Available:%s\n", ((GyroDto) object).isAvailable() ? "yes" : "no"));
          sensorTextBuilder.append(String.format( "V1:%.2f  V2:%.2f V3:%.2f", ((GyroDto) object).getV1(), ((GyroDto) object).getV2(), ((GyroDto) object).getV3()));
        } else if(object instanceof NFCDto) {
          sensorTextBuilder.append(getTitleBuilder(builder,"NFC"));
          String txt = (((NFCDto) object).isAvailable() ? (" Enabled:" + ((NFCDto) object).isEnabled()) : null);
          sensorTextBuilder.append("Available:" + (((NFCDto) object).isAvailable() ? "yes" : "no"));
          sensorTextBuilder.append(txt!=null? txt:"");
        } else if(object instanceof ProximityDto) {
          sensorTextBuilder.append(getTitleBuilder(builder,"Proximity"));
          sensorTextBuilder.append(String.format("Distance:%scm", ((ProximityDto) object).getValue()));
        } else if(object instanceof StepsDto) {
          sensorTextBuilder.append(getTitleBuilder(builder,"StepCounter"));
          sensorTextBuilder.append(String.format("Available:%s\n", ((StepsDto) object).isAvailableStepCounter() || ((StepsDto) object).isAvailableStepDetector() ? "yes" : "no"));
          sensorTextBuilder.append("Steps:"+((StepsDto) object).getSteps());
        } else if(object instanceof ThermometerDto) {
          sensorTextBuilder.append(getTitleBuilder(builder,"Thermometer"));
          sensorTextBuilder.append(String.format("Available:%s Temperature:%s", ((ThermometerDto) object).isAvailable() ? "yes" : "no", ((ThermometerDto) object).getValue()));
        }
      }
      sensorInfo.setText(sensorTextBuilder);


    } else {
      holder.userInfo.setText(R.string.zero_search_result);
    }


  }

  SpannableStringBuilder getTitleBuilder(SpannableStringBuilder builder, String title) {
    builder.clear();
    builder.append("\n\n"+title+"\n");
    builder.setSpan(new StyleSpan(Typeface.BOLD), 2, title.length()+2, 0);
    return builder;
  }

  @Override public int getItemCount() {
    return list.size();
  }

  public void refresh(ArrayList<DeviceInfoDto> list) {
    if(list == null || list.size() == 0) {
      this.list.clear();
      this.list.add(null);
    } else {
      this.list.clear();
      this.list.addAll(list);
    }
    notifyDataSetChanged();
  }

  class RecyclerVH extends RecyclerView.ViewHolder {
    TextView userInfo;
    ExpandableTextView sensorInfo;
    public RecyclerVH(View itemView) {
      super(itemView);
      userInfo = (TextView) itemView.findViewById(R.id.user_info);
      sensorInfo = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
    }
  }

}
