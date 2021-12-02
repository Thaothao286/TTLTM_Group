/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.airbrake;

import java.net.*;
import java.io.*;
import java.util.*;
public class Redirector implements Runnable {
 private final int port;
 private final String newSite;

 public Redirector(String site, int port) {
 this.port = port;
 this.newSite = site;
 }
 @Override
 public void run( ) {

 try {

 ServerSocket server = new ServerSocket(this.port);
 System.out.println("Redirecting connections on port "
 + server.getLocalPort( ) + " to " + newSite);

 while (true) {

 try {
 Socket s = server.accept( ); 
 Thread t = new RedirectThread(s);
 t.start( );
 } // end try
 catch (IOException e) {
 }

 } // end while

 } // end try
 catch (BindException e) {
 System.err.println("Could not start server. Port Occupied");
 }
 catch (IOException e) {
 System.err.println(e);
 }
 } // end run
 class RedirectThread extends Thread {

 private Socket connection;

 RedirectThread(Socket s) {
 this.connection = s;
 }

 @Override
 public void run( ) {

 try {

 Writer out = new BufferedWriter(
 new OutputStreamWriter(
 connection.getOutputStream( ), "ASCII"
 )
 );
 Reader in = new InputStreamReader(
 new BufferedInputStream(
 connection.getInputStream( )
 )
 );

 // read the first line only; that's all we need
 StringBuffer request = new StringBuffer(80);
 while (true) {
 int c = in.read( );
 if (c == '\r' || c == '\n' || c == -1) break;
 request.append((char) c);
 }
 // If this is HTTP 1.0 or later send a MIME header
 String get = request.toString( );
 int firstSpace = get.indexOf(' ');
 int secondSpace = get.indexOf(' ', firstSpace+1);
 String theFile = get.substring(firstSpace+1, secondSpace);
 if (get.indexOf("HTTP") != -1) {
 out.write("HTTP1.0 302 FOUND\r\n");
 Date now = new Date( );
 out.write("Date: " + now + "\r\n");
 out.write("Server: Redirector 1.0\r\n");
 out.write("Location: " + newSite + theFile + "\r\n");
 out.write("Content-type: text/html\r\n\r\n");
 out.flush( ); 
 }
 
 } // end try
 catch (IOException e) {
 }
 finally {
 try {
 if (connection != null) connection.close( );
 }
 catch (IOException e) {}
 }

 } // end run

 }
 public static void main(String[] args) {
 int thePort;
 String theSite;

 try {
 theSite = args[0];
 // trim trailing slash
 if (theSite.endsWith("/")) {
 theSite = theSite.substring(0, theSite.length( )-1);
 }
 }
 catch (Exception e) {
 System.out.println(
 "Usage: java Redirector http://google.com.vn/ port");
 return;
 }

 try {
 thePort = Integer.parseInt(args[1]);
 }
 catch (Exception e) {
 thePort = 80;
 }

 Thread t = new Thread(new Redirector(theSite, thePort));
 t.start( );

 } // end main
} 