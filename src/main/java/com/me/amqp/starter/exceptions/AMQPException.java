package com.me.amqp.starter.exceptions;

public class AMQPException extends RuntimeException { 
 
 private static final long serialVersionUID = 8339661146128257545L; 
 
 public AMQPException(String message) { 
  super(message); 
 } 
 
 public AMQPException(String message, Throwable cause) { 
  super(message, cause); 
 } 
}