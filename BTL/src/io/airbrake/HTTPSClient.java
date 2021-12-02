/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.airbrake;

import java.net.*; 
import java.io.*; 
import java.security.*; 
import javax.net.ssl.*; 
public class HTTPSClient { 
 
 public static void main(String[] args) { 
 
 if (args.length == 0) { 
 System.out.println("Usage: java HTTPSClient host"); 
 return; 
 } 
 
 int port = 443; // default https port 
 String host = args[0]; 
 
 try { 
 
 Security.addProvider(new 
com.sun.net.ssl.internal.ssl.Provider( )); 
 SSLSocketFactory factory 
 = (SSLSocketFactory) SSLSocketFactory.getDefault( ); 
 
 SSLSocket socket = (SSLSocket) factory.createSocket(host, port); 
 Writer out = new OutputStreamWriter(socket.getOutputStream( )); 
 // https requires the full URL in the GET line 
 out.write("GET http://" + host + "/ HTTP 1.1\r\n"); 
 out.write("\r\n"); 
 out.flush( ); 
 // read response 
 BufferedReader in = new BufferedReader( 
 new InputStreamReader(socket.getInputStream( ))); 
 int c; 
 while ((c = in.read( )) != -1) { 
 System.out.write(c); 
 } 
 
 out.close( ); 
 in.close( ); 
 socket.close( ); 
 
 } 
 catch (IOException e) { 
 System.err.println(e); 
 } 
 
 }
}
