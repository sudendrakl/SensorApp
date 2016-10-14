package com.blackbeard.sensors.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by sudendra.kamble on 29/09/16.
 */
@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class RegisterDto {
  String userName;
  String mailId;
  String uid;
}
