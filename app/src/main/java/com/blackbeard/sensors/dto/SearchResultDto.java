package com.blackbeard.sensors.dto;

import java.util.ArrayList;
import lombok.Data;
import lombok.ToString;

/**
 * Created by sudendra.kamble on 12/10/16.
 */
@Data
@ToString
public class SearchResultDto extends APIResponseDto {
  ArrayList<DeviceInfoDto> response = new ArrayList<>(3);
}
