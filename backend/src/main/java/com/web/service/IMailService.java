/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service;

/**
 *
 * @author ZZ
 */
public interface IMailService {
  void sendText(String to, String subject, String text);
  void sendHtml(String to, String subject, String html);
}